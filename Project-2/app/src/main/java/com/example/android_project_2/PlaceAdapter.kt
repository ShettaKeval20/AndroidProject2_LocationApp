package com.example.android_project_2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng


class PlaceAdapter : RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder>(){
    private val placesList: MutableList<PlacesFragment.Place> = mutableListOf()

    fun updatePlaces(newPlacesList: MutableList<PlacesFragment.Place>) {
        placesList.clear()
        placesList.addAll(newPlacesList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.display_item, parent, false)
        return PlaceViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        val place = placesList[position]
        holder.bind(place)
    }

    override fun getItemCount(): Int {
        return placesList.size
    }

    inner class PlaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        private val vicinityTextView: TextView = itemView.findViewById(R.id.vicinityTextView)

        fun bind(place: PlacesFragment.Place) {
            nameTextView.text = place.name
            vicinityTextView.text = place.vicinity
        }
    }

    data class Place(
        val name: String,
        val vicinity: String,
        val location: LatLng
    )
}
