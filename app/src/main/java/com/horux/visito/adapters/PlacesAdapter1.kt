package com.horux.visito.adapters

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import java.util.Objects

class PlacesAdapter(
    private val activity: Activity,
    inflater: LayoutInflater,
    list: ArrayList<PlaceModel>
) : RecyclerView.Adapter<PlacesViewHolder?>() {
    private val inflater: LayoutInflater
    private var list: ArrayList<PlaceModel>

    init {
        this.inflater = inflater
        this.list = list
    }

    fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlacesViewHolder {
        val view: View = inflater.inflate(R.layout.places_row, parent, false)
        return PlacesViewHolder(Objects.requireNonNull(DataBindingUtil.bind(view)))
    }

    fun onBindViewHolder(holder: PlacesViewHolder, position: Int) {
        val place: PlaceModel = list[position]
        holder.binding.name.setText(place.getTitle())
        val options: RequestOptions = RequestOptions()
            .fitCenter()
            .placeholder(R.drawable.img_loading)
            .error(R.drawable.img_no_image)
        Glide.with(activity).load(place.getImage()).apply(options).into(holder.binding.image)
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
