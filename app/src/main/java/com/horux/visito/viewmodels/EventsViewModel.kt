package com.horux.visito.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.horux.visito.models.dao.EventModel
import com.horux.visito.repositories.HomeRepository

class EventsViewModel : ViewModel() {
    var repository: HomeRepository? = null
    var allEvents = true
    var events = MutableLiveData<ArrayList<EventModel>>()
    fun fetchEvents(): MutableLiveData<ArrayList<EventModel>> {
        if (repository == null) repository = HomeRepository.instance
        events = repository!!.fetchEvents()
        return events
    }
}
