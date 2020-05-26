package com.example.cocktailapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cocktailapp.databinding.CocktailItemViewBinding

import com.example.cocktailapp.service.Cocktail

class CocktailAdapter (private val clickListener: CocktailClickListener) : ListAdapter<Cocktail, CocktailAdapter.ViewHolder>(CocktailDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)!!
        holder.itemView.setOnClickListener{
            clickListener.onClick(item.cocktailPrice)
        }
        holder.bind(item)
    }

    class ViewHolder private constructor(private val binding: CocktailItemViewBinding) : RecyclerView.ViewHolder (binding.root) {

        fun bind(item: Cocktail) {
            //To set the data variable that I created on the xml
            binding.cocktail = item
            // This is important, because it forces the data binding to execute immediately,
            // which allows the RecyclerView to make the correct view size measurements
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = CocktailItemViewBinding.inflate(inflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

//To update only the item that have changed if it´s on the screen
class CocktailDiffCallback : DiffUtil.ItemCallback<Cocktail>() {
    override fun areItemsTheSame(oldItem: Cocktail, newItem: Cocktail): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: Cocktail, newItem: Cocktail): Boolean {
        return oldItem.cocktailPrice == newItem.cocktailPrice
    }
}

class CocktailClickListener (val clickListener: (cocktailId: String) -> Unit){
    fun onClick(cocktailId: String) = clickListener(cocktailId)
}