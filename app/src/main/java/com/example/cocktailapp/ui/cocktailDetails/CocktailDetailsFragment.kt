package com.example.cocktailapp.ui.cocktailDetails

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.cocktailapp.R
import com.example.cocktailapp.databinding.CocktailDetailBinding
import com.example.cocktailapp.db.CocktailDatabase
import com.example.cocktailapp.repository.CocktailRepository
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

class CocktailDetailsFragment : Fragment(){

    private lateinit var viewModel: CocktailDetailsViewModel

    private var snackBar: Snackbar? = null

    /**
     * Inflates the layout with Data Binding, sets its lifecycle owner to the CocktailDetailsFragment
     * to enable Data Binding to observe LiveData, and sets up the RecyclerView with an adapter
     */
    @SuppressLint("RestrictedApi")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = CocktailDetailBinding.inflate(inflater)

        val application = requireNotNull(activity).application

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        val cocktailId = requireArguments().getString("drinkId")

        val cocktailRepository = CocktailRepository(CocktailDatabase(requireActivity()))
        val viewModelFactory = CocktailDetailsViewModelFactory(cocktailId!!, application, cocktailRepository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(CocktailDetailsViewModel::class.java)

        // Giving the binding access to the CocktailDetailsViewModel
        binding.cocktailDetailsVieModel = viewModel

        viewModel.insertedToBDStatus.observe(viewLifecycleOwner, Observer {
            if(it) {
                snackBar = Snackbar.make(requireActivity().findViewById(R.id.cocktail_details_container), requireContext().getString(R.string.cocktail_saved_message),
                    Snackbar.LENGTH_SHORT).apply {
                    view.setBackgroundColor(resources.getColor(R.color.successfulColor))
                    show()
                }
            }
        })

        viewModel.deletedFromBDStatus.observe(viewLifecycleOwner, Observer {
            if (it) {
                snackBar = Snackbar.make(requireActivity().findViewById(R.id.cocktail_details_container), requireContext().getString(R.string.cocktail_deleted_successfully_from_favorites),
                    Snackbar.LENGTH_SHORT).apply {
                    view.setBackgroundColor(resources.getColor(R.color.successfulColor))
                    show()
                }
                viewModel.deletedFromBDMessageCompleted()
            }
        })

        val fab: FloatingActionButton = requireActivity().findViewById(R.id.fab)
        fab.visibility = View.VISIBLE
        fab.setOnClickListener {
            sendEmail()
        }
        requireActivity().invalidateOptionsMenu()
        setHasOptionsMenu(true)
        return binding.root
    }

    private fun sendEmail() {
        val text: String = viewModel.getCocktailDetailsString()
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:") // only email apps should handle this

        intent.putExtra(Intent.EXTRA_SUBJECT, "Check this cocktail")
        intent.putExtra(Intent.EXTRA_TEXT, text)
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.main, menu)
        menu.removeItem(R.id.action_settings)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if(item.itemId == R.id.action_favorite) {
            viewModel.saveOrDeleteCocktailInFavorites()
            true
        }else
            false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        snackBar?.dismiss()
    }

}