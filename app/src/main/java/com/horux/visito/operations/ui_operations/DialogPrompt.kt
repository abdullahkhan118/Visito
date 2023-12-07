package com.horux.visito.operations.ui_operations

import android.app.Activity
import androidx.databinding.DataBindingUtil

class DialogPrompt {
    fun showMessage(
        activity: Activity?,
        inflater: LayoutInflater?,
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
        activity: Activity?,
        inflater: LayoutInflater?,
        weather: CurrentWeatherResponse
    ): WeatherInformationBinding {
        val binding: WeatherInformationBinding =
            DataBindingUtil.inflate(inflater, R.layout.weather_information, null, false)
        binding.temperature.setText(weather.getCurrent().getTempC().toString() + "\u2103")
        binding.feelsLike.setText(weather.getCurrent().getFeelslikeC().toString() + "\u2103")
        binding.changeTemperature.setText(weather.getCurrent().getTempC().toString() + "\u2103")
        binding.humidity.setText(weather.getCurrent().getHumidity().toString() + " %")
        binding.pressure.setText(weather.getCurrent().getPressureIn().toString() + " in")
        binding.precipitation.setText(weather.getCurrent().getPrecipIn().toString() + " in")
        binding.windSpeed.setText(weather.getCurrent().getWindKph().toString() + " km/h")
        binding.windDirection.setText(weather.getCurrent().getWindDir())
        binding.windAngle.setText(weather.getCurrent().getWindDegree().toString() + " Degrees")
        binding.uv.setText(weather.getCurrent().getUv().toString())
        binding.cloud.setText(weather.getCurrent().getCloud().toString() + " %")
        binding.visibility.setText(weather.getCurrent().getVisKm().toString() + " km")
        val dialog: AlertDialog = AlertDialog.Builder(activity)
            .setView(binding.getRoot())
            .show()
        return binding
    }
}
