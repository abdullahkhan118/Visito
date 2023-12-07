package com.horux.visito.adapters

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.horux.visito.R
import com.horux.visito.activities.PlaceDescriptionActivity
import com.horux.visito.databinding.PlacesRowBinding
import com.horux.visito.globals.AppConstants
import com.horux.visito.loadImage
import com.horux.visito.models.dao.PlaceModel
import java.util.Objects

class PlacesAdapter(
    private val activity: Activity,
    inflater: LayoutInflater,
    list: ArrayList<PlaceModel>
) : RecyclerView.Adapter<PlacesAdapter.PlacesViewHolder?>() {
    private val inflater: LayoutInflater
    private var list: ArrayList<PlaceModel>

    init {
        this.inflater = inflater
        this.list = list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlacesViewHolder {
        val view: View = inflater.inflate(R.layout.places_row, parent, false)
        return PlacesViewHolder(Objects.requireNonNull(DataBindingUtil.bind(view)))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: PlacesViewHolder, position: Int) {
        val place: PlaceModel = list[position]
        holder.binding.name.setText(place.title)
        loadImage(activity,place.image,holder.binding.image)
        holder.itemView.setOnClickListener(View.OnClickListener {
            val intent = Intent(activity, PlaceDescriptionActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable(AppConstants.STRING_DATA, place)
            Log.e("ParcelPlace", "" + bundle[AppConstants.STRING_DATA].toString())
            intent.putExtras(bundle)
            activity.startActivity(intent)
        })
    }

    val itemCount: Int
        get() = list.size

    fun updateList(updatedList: ArrayList<PlaceModel>) {
        list = updatedList
        Log.e("UpdatedList", list.toString())
        notifyDataSetChanged()
    }

    inner class PlacesViewHolder(binding: PlacesRowBinding) :
        RecyclerView.ViewHolder(binding.getRoot()) {
        var binding: PlacesRowBinding

        init {
            this.binding = binding
        }
    }
}
