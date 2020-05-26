package com.example.cocktailapp.ui.cocktailDetails

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat.invalidateOptionsMenu
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.cocktailapp.R
import com.example.cocktailapp.databinding.CocktailDetailBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CocktailDetailsFragment : Fragment(){

    /**
     * Inflates the layout with Data Binding, sets its lifecycle owner to the OverviewFragment
     * to enable Data Binding to observe LiveData, and sets up the RecyclerView with an adapter.
     */
    @SuppressLint("RestrictedApi")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = CocktailDetailBinding.inflate(inflater)

        val application = requireNotNull(activity).application

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        val cocktailId = CocktailDetailsFragmentArgs.fromBundle(requireArguments()).cocktailIdArg

        val viewModelFactory = CocktailDetailsViewModelFactory(cocktailId, application)
        val viewModel: CocktailDetailsViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(CocktailDetailsViewModel::class.java)

        // Giving the binding access to the CocktailDetailsViewModel
        binding.cocktailDetailsVieModel = viewModel

        val fab: FloatingActionButton = requireActivity().findViewById(R.id.fab)
        fab.visibility = View.VISIBLE
        fab.setOnClickListener {
            var text : String = viewModel.getCocktailDetailsString()
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:") // only email apps should handle this

            intent.putExtra(Intent.EXTRA_SUBJECT, "Check this cocktail")
            intent.putExtra(Intent.EXTRA_TEXT, text)
            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(intent) //esto por si no hay ninguna app q lo haga, no hay crash aqui
            }
        }
        requireActivity().invalidateOptionsMenu()
        setHasOptionsMenu(false)
        return binding.root
    }

    override fun onPrepareOptionsMenu(menu: Menu) {

        menu.removeGroup(R.id.menuSet)
    }

}