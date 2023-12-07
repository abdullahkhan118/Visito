package com.horux.visito.operations.ui_operations

import android.app.Activity
import android.app.AlertDialog
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.horux.visito.R
import com.horux.visito.databinding.MessagePromptBinding
import com.horux.visito.databinding.WeatherInformationBinding
import com.horux.visito.models.weather.CurrentWeatherResponse

class DialogPrompt {
    fun showMessage(
        activity: Activity,
        inflater: LayoutInflater,
        message: String?
    ): MessagePromptBinding {
        val binding: MessagePromptBinding =
            DataBindingUtil.inflate(inflater, R.layout.message_prompt, null, false)
        val dialog: AlertDialog = AlertDialog.Builder(activity)
            .setView(binding.getRoot())
            .show()
        binding.message.setText(message)
        binding.btnOk.setOnClickListener { view -> dialog.dismiss() }
        return binding
    }

    fun showWeather(
        activity: Activity,
        inflater: LayoutInflater,
        weather: CurrentWeatherResponse
    ): WeatherInformationBinding {
        val binding: WeatherInformationBinding =
            DataBindingUtil.inflate(inflater, R.layout.weather_information, null, false)
        binding.temperature.setText(weather.current?.tempC.toString() + "\u2103")
        binding.feelsLike.setText(weather.current?.feelslikeC.toString() + "\u2103")
        binding.changeTemperature.setText(weather.current?.tempC.toString() + "\u2103")
        binding.humidity.setText(weather.current?.humidity.toString() + " %")
        binding.pressure.setText(weather.current?.pressureIn.toString() + " in")
        binding.precipitation.setText(weather.current?.precipIn.toString() + " in")
        binding.windSpeed.setText(weather.current?.windKph.toString() + " km/h")
        binding.windDirection.setText(weather.current?.windDir)
        binding.windAngle.setText(weather.current?.windDegree.toString() + " Degrees")
        binding.uv.setText(weather.current?.uv.toString())
        binding.cloud.setText(weather.current?.cloud.toString() + " %")
        binding.visibility.setText(weather.current?.visKm.toString() + " km")
        val dialog: AlertDialog = AlertDialog.Builder(activity)
            .setView(binding.getRoot())
            .show()
        return binding
    }
}
