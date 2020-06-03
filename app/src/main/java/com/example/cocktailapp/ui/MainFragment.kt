package com.example.cocktailapp.ui

import android.annotation.SuppressLint
import android.content.Context.INPUT_METHOD_SERVICE
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.cocktailapp.R
import com.example.cocktailapp.databinding.FragmentMainBinding
import com.example.cocktailapp.db.CocktailDatabase
import com.example.cocktailapp.repository.CocktailRepository
import com.example.cocktailapp.ui.adapters.CocktailAdapter
import com.example.cocktailapp.ui.adapters.CocktailClickListener
import com.example.cocktailapp.util.Constants
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainFragment : Fragment() {

    private lateinit var viewModel: CocktailListViewModel

    @SuppressLint("RestrictedApi")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentMainBinding.inflate(inflater)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        //Setting
        val cocktailTypeSetting : String = PreferenceManager.getDefaultSharedPreferences(requireActivity()).getString(getString(
            R.string.pref_cocktail_category_key
        ),
            getString(R.string.pref_Cocktail_value))!!

        val cocktailRepository = CocktailRepository(CocktailDatabase(requireActivity()))
        val viewModelFactory = CocktailListViewModelFactory(cocktailTypeSetting, app = requireActivity().application, repository = cocktailRepository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(CocktailListViewModel::class.java)

        // Giving the binding access to the View Model
        binding.cocktailListViewModel = viewModel

        // Initialize the adapter of the RecyclerView
        val adapter = CocktailAdapter(CocktailClickListener { cocktailId ->
                    viewModel.displayCocktailDetails(cocktailId)
                })
        // Sets the adapter of the RecyclerView
        binding.cocktailRecycleView.adapter = adapter
        // Fill the List
        viewModel.cocktailList.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })

        // Add dividers between RecyclerView's row items
        val decoration = DividerItemDecoration(requireActivity(), DividerItemDecoration.VERTICAL)
        binding.cocktailRecycleView.addItemDecoration(decoration)

        viewModel.navigateToSelectedDrink.observe(viewLifecycleOwner, Observer {
            if(it != null) {
                hideTheKeyboard()
                val bundle = bundleOf( "drinkId" to it)
                this.findNavController().navigate(R.id.action_nav_home_to_cocktailDetailsFragment, bundle)
                viewModel.displayCocktailDetailsComplete()
            }
        })

        searchCocktailByName(binding, adapter)

        val fab: FloatingActionButton = requireActivity().findViewById(R.id.fab)
        fab.visibility = View.INVISIBLE

        setHasOptionsMenu(true)
        return binding.root
    }

    private fun searchCocktailByName(binding: FragmentMainBinding, adapter: CocktailAdapter) {
        viewModel.word.observe(viewLifecycleOwner, Observer { newWord ->
            if (newWord.isNotEmpty()) {
                binding.searchEditText.setText(newWord)
            }
        })

        // Fill the List with the given name
        viewModel.cocktailListByGivenName.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })

        var job: Job? = null
        binding.searchEditText.addTextChangedListener { editable ->
            job?.cancel()
            job = MainScope().launch {
                delay(Constants.SEARCH_TIME_DELAY)
                editable?.let {
                    if(editable.toString().isNotEmpty()) {
                        viewModel.getCocktailByNameWhenUsersTypes(editable.toString())
                        adapter.submitList(viewModel.cocktailListByGivenName.value)
                    }
                }
            }
        }

        binding.clearButton.setOnClickListener {
            hideTheKeyboard()
            binding.searchEditText.setText("")
            viewModel.getCocktailByNameWhenUsersTypes("")
        }

        viewModel.showClearedSnackBar.observe(viewLifecycleOwner, Observer {
            if (it) {
                Snackbar.make(requireActivity().findViewById(R.id.searchEditText), getString(R.string.cleared_message), Snackbar.LENGTH_SHORT).apply {
                    view.setBackgroundColor(Color.BLUE)
                    show()
                }
                viewModel.showClearedSnackBarComplete()
            }
        })
    }

    private fun hideTheKeyboard() {
        val imm = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        if (imm.isActive)
            imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.main, menu)
        menu.removeItem(R.id.action_favorite)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if(item.itemId == R.id.action_settings) {
            this.findNavController().navigate(MainFragmentDirections.actionNavHomeToSettingsActivity())
            true
        }else {
            false
        }
    }

    override fun onPause() {
        super.onPause()
        hideTheKeyboard()
    }
}