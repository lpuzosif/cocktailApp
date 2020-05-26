package com.example.cocktailapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.navigateUp
import androidx.preference.PreferenceManager
import com.example.cocktailapp.databinding.FragmentMainBinding
import com.example.cocktailapp.ui.cocktailDetails.CocktailDetailsViewModel
import com.example.cocktailapp.ui.cocktailDetails.CocktailDetailsViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainFragment : Fragment() {

    @SuppressLint("RestrictedApi")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = FragmentMainBinding.inflate(inflater)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        //Setting
        var cocktailTypeSetting : String = PreferenceManager.getDefaultSharedPreferences(requireActivity()).getString(getString(R.string.pref_cocktail_Type_key),
            getString(R.string.pref_Cocktail_value))!!
        Log.d("lilian onCreateSett ", cocktailTypeSetting)
        val viewModelFactory = CocktailListViewModelFactory(cocktailTypeSetting)
        val viewModel: CocktailListViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(CocktailListViewModel::class.java)

        // Giving the binding access to the View Model
        binding.cocktailListViewModel = viewModel

        // Initialize the adapter of the RecyclerView
        val adapter = CocktailAdapter( CocktailClickListener{ cocktailId ->
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

        viewModel.navigateToSelectedDrink.observe(viewLifecycleOwner, Observer {
            if(it != null){
                this.findNavController().navigate(MainFragmentDirections.actionNavHomeToCocktailDetailsFragment(it))
                viewModel.displayCocktailDetailsComplete()
            }
        })

        val fab: FloatingActionButton = requireActivity().findViewById(R.id.fab)
        fab.visibility = View.INVISIBLE

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if(item.itemId == R.id.action_settings) {
            this.findNavController().navigate(MainFragmentDirections.actionNavHomeToSettingsActivity())
            true
        }else {
            false
        }
    }

}