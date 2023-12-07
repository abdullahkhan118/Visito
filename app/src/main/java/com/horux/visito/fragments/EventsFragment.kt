package com.horux.visito.fragments

import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.model.LatLng
import com.horux.visito.R
import com.horux.visito.activities.HomeActivity
import com.horux.visito.adapters.EventsAdapter
import com.horux.visito.databinding.FragmentEventsBinding
import com.horux.visito.models.dao.EventModel
import com.horux.visito.viewmodels.EventsViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit

class EventsFragment : Fragment() {
    private lateinit var homeActivity: HomeActivity
    private lateinit var viewModel: EventsViewModel
    private lateinit var binding: FragmentEventsBinding
    private lateinit var adapter: EventsAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentEventsBinding.inflate(inflater)
        viewModel = ViewModelProvider(this).get<EventsViewModel>(EventsViewModel::class.java)
        return binding.getRoot()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeActivity = requireActivity() as HomeActivity
        adapter = EventsAdapter(homeActivity, getLayoutInflater(), ArrayList<EventModel>())
        binding.recycleList.setAdapter(adapter)
        binding.currentEvents.setOnClickListener(View.OnClickListener {
            viewModel.allEvents = false
            binding.currentEvents.setBackgroundResource(R.drawable.bordered_background)
            binding.allEvents.setBackgroundResource(R.drawable.app_background)
            if (viewModel.events.getValue() != null) adapter.updateList(getCurrentEvents(viewModel.events.getValue()!!)) else adapter.updateList(
                ArrayList<EventModel>()
            )
        })
        binding.allEvents.setOnClickListener(View.OnClickListener {
            viewModel.allEvents = true
            binding.allEvents.setBackgroundResource(R.drawable.bordered_background)
            binding.currentEvents.setBackgroundResource(R.drawable.app_background)
            if (viewModel.events.getValue() != null) adapter.updateList(viewModel.events.getValue()!!) else adapter.updateList(
                ArrayList<EventModel>()
            )
        })
    }

    override fun onStart() {
        super.onStart()
        homeActivity.startLocationUpdates()
        if (homeActivity.isInternetAvailable) {
            viewModel.fetchEvents()
                .observe(
                    viewLifecycleOwner
                ) { eventModels ->
                    if (viewModel.allEvents) adapter.updateList(eventModels) else adapter.updateList(
                        getCurrentEvents(eventModels)
                    )
                    homeActivity.setLoaderVisibility(false)
                }
        }
    }

    private fun getCurrentEvents(eventModels: ArrayList<EventModel>): ArrayList<EventModel> {
        val currentEvents: ArrayList<EventModel> = ArrayList<EventModel>()
        val currentLocation: Location? = homeActivity.fusedLocation.currentLocation.getValue()
        if (currentLocation != null) {
            val currentLatLng = LatLng(currentLocation.latitude, currentLocation.longitude)
            for (eventModel in eventModels) {
                try {
                    val sdf = SimpleDateFormat("EEE MMM d yyyy")
                    var eventDate: String = eventModel.startTime
                        .substring(0, eventModel.startTime.indexOf(" at"))
                        .replace("\u00A0".toRegex(), "")
                    eventDate = eventDate.trim { it <= ' ' }
                    Log.e("EventDate", eventDate)
                    val date = sdf.parse(eventDate)
                    if (isDateInCurrentWeek(date)) currentEvents.add(eventModel)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        Log.e("CurrentEvents", currentEvents.toString())
        return currentEvents
    }

    override fun onStop() {
        super.onStop()
    }

    fun isDateInCurrentWeek(date: Date?): Boolean {
        val currentCalendar = Calendar.getInstance()
        val week = currentCalendar[Calendar.WEEK_OF_YEAR]
        val year = currentCalendar[Calendar.YEAR]
        val targetCalendar = Calendar.getInstance()
        targetCalendar.time = date
        val targetWeek = targetCalendar[Calendar.WEEK_OF_YEAR]
        val targetYear = targetCalendar[Calendar.YEAR]
        val dateDifference = Math.abs(currentCalendar.timeInMillis - targetCalendar.timeInMillis)
        return (week == targetWeek || TimeUnit.DAYS.convert(
            dateDifference,
            TimeUnit.MILLISECONDS
        ) <= 7) && year == targetYear
    }
}
