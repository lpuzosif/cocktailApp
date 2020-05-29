package com.example.cocktailapp.ui.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.example.cocktailapp.R
import com.example.cocktailapp.databinding.FragmentGalleryBinding

class GalleryFragment : Fragment() {

    private lateinit var galleryViewModel: GalleryViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = FragmentGalleryBinding.inflate(inflater)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        //Setting
        val cocktailTypeSetting : String = PreferenceManager.getDefaultSharedPreferences(requireActivity()).getString(getString(R.string.pref_cocktail_category_key),
            getString(R.string.pref_Cocktail_value))!!

        val viewModelFactory = GalleryViewModelFactory(cocktailTypeSetting)
        val viewModel: GalleryViewModel = ViewModelProvider(this, viewModelFactory).get(GalleryViewModel::class.java)

        // Giving the binding access to the GalleryViewModel
        binding.galleryListViewModel = viewModel

        // Initialize the adapter of the RecyclerView
        val adapter = GalleryAdapter()
        // Sets the adapter of the RecyclerView
        binding.cocktailRecycleView.adapter = adapter
        // Fill the gridList
        viewModel.cocktailList.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })

        return binding.root
    }
}
