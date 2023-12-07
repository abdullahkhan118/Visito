package com.horux.visito.adapters

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.horux.visito.R
import com.horux.visito.activities.PlaceDescriptionActivity
import com.horux.visito.databinding.PlacesRowBinding
import com.horux.visito.globals.AppConstants
import com.horux.visito.loadImage
import com.horux.visito.models.dao.PlaceModel
import java.util.Objects

class FavoritesAdapter(
    private val activity: AppCompatActivity,
    inflater: LayoutInflater,
    list: ArrayList<PlaceModel>
) : RecyclerView.Adapter<FavoritesAdapter.FavoritesViewHolder?>() {
    private val inflater: LayoutInflater
    private var list: ArrayList<PlaceModel>

    init {
        this.inflater = inflater
        this.list = list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritesViewHolder {
        val view: View = inflater.inflate(R.layout.places_row, parent, false)
        return FavoritesViewHolder(Objects.requireNonNull(DataBindingUtil.bind<PlacesRowBinding>(view))!!)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: FavoritesViewHolder, position: Int) {
        val place: PlaceModel = list[position]
        holder.binding.name.setText(place.title)
        loadImage(activity,place.image,holder.binding.image)
        holder.itemView.setOnClickListener(View.OnClickListener {
            val intent = Intent(activity, PlaceDescriptionActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable(AppConstants.STRING_DATA, place)
            intent.putExtras(bundle)
            activity.startActivity(intent)
        })
    }

    fun updateList(updatedList: ArrayList<PlaceModel>) {
        list = updatedList
        notifyDataSetChanged()
    }

    inner class FavoritesViewHolder(binding: PlacesRowBinding) :
        RecyclerView.ViewHolder(binding.getRoot()) {
        var binding: PlacesRowBinding

        init {
            this.binding = binding
        }
    }
}
