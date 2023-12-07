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
import com.horux.visito.activities.EventDescriptionActivity
import com.horux.visito.databinding.PlacesRowBinding
import com.horux.visito.globals.AppConstants
import com.horux.visito.loadImage
import com.horux.visito.models.dao.EventModel
import java.util.Objects

class EventsAdapter(
    private val activity: AppCompatActivity,
    inflater: LayoutInflater,
    list: ArrayList<EventModel>
) : RecyclerView.Adapter<EventsAdapter.EventsViewHolder?>() {
    private val inflater: LayoutInflater
    private var list: ArrayList<EventModel>

    init {
        this.inflater = inflater
        this.list = list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventsViewHolder {
        val view: View = inflater.inflate(R.layout.places_row, parent, false)
        return EventsViewHolder(Objects.requireNonNull(DataBindingUtil.bind<PlacesRowBinding>(view))!!)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: EventsViewHolder, position: Int) {
        val event: EventModel = list[position]
        holder.binding.name.setText(event.title)
        loadImage(activity,event.image,holder.binding.image)
        holder.itemView.setOnClickListener(View.OnClickListener {
            val intent = Intent(activity, EventDescriptionActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable(AppConstants.STRING_DATA, event)
            intent.putExtras(bundle)
            activity.startActivity(intent)
        })
    }

    fun updateList(updatedList: ArrayList<EventModel>) {
        list = updatedList
        notifyDataSetChanged()
    }

    inner class EventsViewHolder(binding: PlacesRowBinding) :
        RecyclerView.ViewHolder(binding.getRoot()) {
        var binding: PlacesRowBinding

        init {
            this.binding = binding
        }
    }
}
