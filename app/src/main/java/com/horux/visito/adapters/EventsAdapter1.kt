package com.horux.visito.adapters

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import java.util.Objects

class EventsAdapter(
    private val activity: Activity,
    inflater: LayoutInflater,
    list: ArrayList<EventModel>
) : RecyclerView.Adapter<EventsViewHolder?>() {
    private val inflater: LayoutInflater
    private var list: ArrayList<EventModel>

    init {
        this.inflater = inflater
        this.list = list
    }

    fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventsViewHolder {
        val view: View = inflater.inflate(R.layout.places_row, parent, false)
        return EventsViewHolder(Objects.requireNonNull(DataBindingUtil.bind(view)))
    }

    fun onBindViewHolder(holder: EventsViewHolder, position: Int) {
        val event: EventModel = list[position]
        holder.binding.name.setText(event.getTitle())
        val options: RequestOptions = RequestOptions()
            .fitCenter()
            .placeholder(R.drawable.img_loading)
            .error(R.drawable.img_no_image)
        Glide.with(activity)
            .load(event.getImage())
            .apply(options)
            .into(holder.binding.image)
        holder.itemView.setOnClickListener(View.OnClickListener {
            val intent = Intent(activity, EventDescriptionActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable(AppConstants.STRING_DATA, event)
            intent.putExtras(bundle)
            activity.startActivity(intent)
        })
    }

    val itemCount: Int
        get() = list.size

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
