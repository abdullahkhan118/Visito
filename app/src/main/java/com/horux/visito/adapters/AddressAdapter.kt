package com.horux.visito.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import com.horux.visito.R
import com.horux.visito.databinding.AddressItemBinding
import com.horux.visito.models.tomtom.autocomplete.Result

class AddressAdapter(var activity: Activity, list: ArrayList<Result>) : ArrayAdapter<Result>(
    activity, 0, list
) {
    @JvmField
    var resultList = ArrayList<Result>()

    init {
        resultList = list
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var listItem = convertView
        if (listItem == null) listItem =
            LayoutInflater.from(activity).inflate(R.layout.address_item, parent, false)
        val binding: AddressItemBinding = DataBindingUtil.bind(listItem!!)!!
        val result = resultList[position]
        binding.addressTitle.setText(result.poi!!.name)
        binding.address.setText(
            StringBuilder().append(String.format("%.2f", result.dist!! / 1000f)).append(" Km")
                .toString()
        )
        return listItem
    }

    fun updateList(updatedList: ArrayList<Result>) {
        resultList = updatedList
        notifyDataSetChanged()
    }
}