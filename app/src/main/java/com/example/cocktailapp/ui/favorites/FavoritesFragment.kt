package com.example.cocktailapp.ui.favorites

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.cocktailapp.R
import com.example.cocktailapp.databinding.FragmentFavoritesBinding
import com.example.cocktailapp.db.CocktailDatabase
import com.example.cocktailapp.repository.CocktailRepository
import com.example.cocktailapp.ui.adapters.CocktailAdapter
import com.example.cocktailapp.ui.adapters.CocktailClickListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

class FavoritesFragment : Fragment() {

    private lateinit var favoritesViewModel: FavoritesViewModel

    private var snackBar: Snackbar? = null

    @SuppressLint("RestrictedApi")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val binding = FragmentFavoritesBinding.inflate(inflater)
        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        val cocktailRepository = CocktailRepository(CocktailDatabase(requireActivity()))
        val viewModelFactory = FavoritesViewModelFactory(app = requireActivity().application, repository = cocktailRepository)
        favoritesViewModel = ViewModelProvider(this, viewModelFactory).get(FavoritesViewModel::class.java)

        binding.viewModel = favoritesViewModel

        // Initialize the adapter of the RecyclerView
        val adapter = CocktailAdapter(CocktailClickListener { cocktailId ->
            favoritesViewModel.displayCocktailDetails(cocktailId)
        })
        // Sets the adapter of the RecyclerView
        binding.cocktailsFavoritesRecycleView.adapter = adapter

        // Fill the List
        favoritesViewModel.cocktailList.observe(viewLifecycleOwner, Observer {
            showMessageWhenEmptyList(it?.size == 0, binding)
            it?.let {
                adapter.submitList(it)
            }
        })

        // Add dividers between RecyclerView's row items
        val decoration = DividerItemDecoration(requireActivity(), DividerItemDecoration.VERTICAL)
        binding.cocktailsFavoritesRecycleView.addItemDecoration(decoration)

        setRemoveWithUndoOptionOnItem(adapter, favoritesViewModel, binding)

        favoritesViewModel.navigateToSelectedDrink.observe(viewLifecycleOwner, Observer {
            if(it != null) {
                val bundle = bundleOf( "drinkId" to it)
                this.findNavController().navigate(R.id.action_nav_favorites_to_cocktailDetailsFragment, bundle)
                favoritesViewModel.displayCocktailDetailsComplete()
            }
        })

        val fab: FloatingActionButton = requireActivity().findViewById(R.id.fab)
        fab.visibility = View.INVISIBLE

        return binding.root
    }

    private fun setRemoveWithUndoOptionOnItem(adapter : CocktailAdapter, favoritesViewModel : FavoritesViewModel, binding: FragmentFavoritesBinding) {

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return true
            }
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val cocktail = adapter.currentList[position]
                favoritesViewModel.deleteCocktail(cocktail)
                view?.let {
                    snackBar = Snackbar.make(it, context!!.getString(R.string.favorite_cocktail_deleted_successfully), Snackbar.LENGTH_LONG).apply {
                        setAction("UNDO") {
                            favoritesViewModel.saveDeletedCocktail(cocktail)
                        }
                        setActionTextColor(Color.BLACK)
                        view.setBackgroundColor(resources.getColor(R.color.successfulColor))
                        show()
                    }
                }
            }
        }
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.cocktailsFavoritesRecycleView)
    }

    private fun showMessageWhenEmptyList(show: Boolean, binding: FragmentFavoritesBinding) {
        if (show) {
            binding.favoritesTextView.visibility = View.VISIBLE
            binding.cocktailsFavoritesRecycleView.visibility = View.GONE
        } else {
            binding.favoritesTextView.visibility = View.GONE
            binding.cocktailsFavoritesRecycleView.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        snackBar?.dismiss()
    }
}
