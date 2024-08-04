package com.example.greenswuniex

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FavoritePlacesAdapter(private val favoritePlaces: List<FavoritePlace>) :
    RecyclerView.Adapter<FavoritePlacesAdapter.FavoritePlaceViewHolder>() {

    class FavoritePlaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val placeName: TextView = itemView.findViewById(R.id.place_name)
        val placeAddress: TextView = itemView.findViewById(R.id.place_address)
        val placeCategory: TextView = itemView.findViewById(R.id.place_category)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritePlaceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_recycler_item, parent, false)
        return FavoritePlaceViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoritePlaceViewHolder, position: Int) {
        val place = favoritePlaces[position]
        holder.placeName.text = place.name
        holder.placeAddress.text = place.address
        holder.placeCategory.text = place.category
    }

    override fun getItemCount() = favoritePlaces.size
}
