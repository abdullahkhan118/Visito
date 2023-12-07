package com.horux.visito.fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.horux.visito.R
import com.horux.visito.activities.HomeActivity
import com.horux.visito.activities.PermissionActivity
import com.horux.visito.adapters.AddressAdapter
import com.horux.visito.databinding.FragmentMapsBinding
import com.horux.visito.globals.AppConstants
import com.horux.visito.models.dao.EventModel
import com.horux.visito.models.dao.PlaceModel
import com.horux.visito.models.tomtom.autocomplete.Result
import com.horux.visito.models.tomtom.route.RouteResponse
import com.horux.visito.operations.ui_operations.DialogPrompt
import com.horux.visito.viewmodels.MapViewModel
import java.util.Arrays
import java.util.Locale

class MapsFragment : Fragment(), SensorEventListener {
    var mAzimuth = 0
    var haveSensor = false
    var haveSensor2 = false
    var rMat = FloatArray(9)
    var orientation = FloatArray(3)
    private lateinit var permissionActivity: PermissionActivity
    private lateinit var binding: FragmentMapsBinding
    private lateinit var googleMap: GoogleMap
    private lateinit var currentLocationMarker: Marker
    private lateinit var destinationMarker: Marker

    //    private Polyline polyline;
    private lateinit var cameraPositionBuilder: CameraPosition.Builder
    private lateinit var viewModel: MapViewModel
    private lateinit var addressAdapter: AddressAdapter
    private val categories = ArrayList<String>()
    private val radiuses = ArrayList<String>()
    private lateinit var mSensorManager: SensorManager
    private lateinit var mRotationV: Sensor
    private lateinit var mAccelerometer: Sensor
    private lateinit var mMagnetometer: Sensor
    private val mLastAccelerometer = FloatArray(3)
    private val mLastMagnetometer = FloatArray(3)
    private var mLastAccelerometerSet = false
    private var mLastMagnetometerSet = false
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentMapsBinding.inflate(inflater)
        viewModel = ViewModelProvider(this).get<MapViewModel>(MapViewModel::class.java)
        binding.weatherCard.setVisibility(View.GONE)
        return binding.getRoot()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.map.onCreate(savedInstanceState)
        permissionActivity = requireActivity() as PermissionActivity
        categories.addAll(getResources().getStringArray(R.array.categories).toList())
        radiuses.addAll(getResources().getStringArray(R.array.radius).toList())
        mSensorManager =
            permissionActivity.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        setAdapters()
        binding.searchPlaces.setDropDownBackgroundResource(R.color.white)
    }

    private fun setAdapters() {
        binding.category.setAdapter(
            ArrayAdapter(
                permissionActivity,
                R.layout.row_spinner,
                R.id.chosen_value,
                categories.toTypedArray()
            )
        )
        binding.category.setText(categories[categories.size - 1], false)
        binding.radius.setAdapter(
            ArrayAdapter(
                permissionActivity,
                R.layout.row_spinner,
                R.id.chosen_value,
                radiuses.toTypedArray()
            )
        )
        binding.radius.setText(radiuses[radiuses.size - 1], false)
    }

    override fun onStart() {
        super.onStart()
        permissionActivity = requireActivity() as HomeActivity
        binding.map.onStart()
        permissionActivity.startLocationUpdates()
        viewModel.autoComplete.observe(
            getViewLifecycleOwner(),
            Observer { autoCompleteResponse ->
                if (autoCompleteResponse != null && autoCompleteResponse.results != null) {
                    Log.e("Result", autoCompleteResponse.results.toString())
                    val addresses = ArrayList<Result>()
                    val categoryString: String = binding.category.getText().toString().toLowerCase()
                    Log.e("CategoryString", categoryString)
                    val results: List<Result> = autoCompleteResponse.results!!.filter {
                        it.poi!!.categories!!.stream().anyMatch { s ->
                            s!!.lowercase(
                                Locale.getDefault()
                            ) == categoryString
                        }
                    }
                    if (results.isEmpty()) addresses.addAll(autoCompleteResponse.results!!) else addresses.addAll(
                        results
                    )
                    Log.e("Addresses", Arrays.toString(addresses.toTypedArray()))
                    if (!addresses.isEmpty()) binding.searchPlaces.showDropDown()
                    addressAdapter = AddressAdapter(permissionActivity, addresses)
                    binding.searchPlaces.setAdapter(addressAdapter)
                }
            })
        viewModel.weather.observe(
            getViewLifecycleOwner(),
            Observer { currentWeatherResponse ->
                if (currentWeatherResponse != null) {
                    binding.weatherCard.setVisibility(View.VISIBLE)
                    binding.weatherCondition.setText(
                        currentWeatherResponse.current!!.condition!!.text
                    )
                    binding.temperature.setText(
                        currentWeatherResponse.current!!.tempC.toString() + " \u2103"
                    )
                } else {
                    binding.weatherCard.setVisibility(View.GONE)
                }
            })
        binding.map.getMapAsync(object : OnMapReadyCallback {
            override fun onMapReady(googleMap: GoogleMap) {
                this@MapsFragment.googleMap = googleMap
                setMapUi()
                cameraPositionBuilder = CameraPosition.Builder()
                val location: Location? = permissionActivity.fusedLocation.currentLocation.getValue()
                val latLngBoundsBuilder: LatLngBounds.Builder = LatLngBounds.Builder()
                if (location != null) {
                    val currentPosition = LatLng(location.latitude, location.longitude)
                    if (currentLocationMarker == null) currentLocationMarker =
                        googleMap.addMarker(MarkerOptions().position(currentPosition))!! else currentLocationMarker.setPosition(
                        currentPosition
                    )
                    googleMap.animateCamera(
                        CameraUpdateFactory.newCameraPosition(
                            CameraPosition.Builder().target(
                                currentPosition
                            ).zoom(16f).build()
                        )
                    )
                    var destinationPosition: LatLng? = null
                    if (getArguments() != null) {
                        val type: String = requireArguments().getString(AppConstants.STRING_TYPE)!!
                        if (type != null) {
                            if (type == AppConstants.STRING_PLACES) {
                                val place: PlaceModel =
                                    requireArguments().getParcelable(AppConstants.STRING_DATA)!!
                                if (place != null) {
                                    destinationPosition =
                                        LatLng(place.latitude, place.longitude)
                                }
                            } else {
                                val event: EventModel =
                                    requireArguments().getParcelable(AppConstants.STRING_DATA)!!
                                if (event != null) {
                                    destinationPosition =
                                        LatLng(event.latitude, event.longitude)
                                }
                            }
                            destinationMarker.setPosition(
                            destinationPosition!!
                        )
                        }
                    }
                    if (currentPosition != null || destinationPosition != null) {
                        if (currentPosition != null) latLngBoundsBuilder.include(currentPosition)
                        if (destinationPosition != null) latLngBoundsBuilder.include(
                            destinationPosition
                        )
                        try {
                            googleMap.animateCamera(
                                CameraUpdateFactory.newLatLngBounds(
                                    LatLngBounds.builder().build(), 16
                                )
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        })
        permissionActivity.fusedLocation.currentLocation.observe(
            getViewLifecycleOwner(),
            object : Observer<Location> {
                override fun onChanged(location: Location) {
                    viewModel.currentLocation = location
                    if (googleMap != null) {
                        val currentPosition = LatLng(location.latitude, location.longitude)
                        if (currentLocationMarker == null) {
                            currentLocationMarker =
                                googleMap.addMarker(MarkerOptions().position(currentPosition))!!
                            cameraPositionBuilder.target(currentLocationMarker.getPosition())
                            cameraPositionBuilder.zoom(16f)
                            googleMap.animateCamera(
                                CameraUpdateFactory.newCameraPosition(
                                    cameraPositionBuilder.build()
                                )
                            )
                            viewModel.weatherForecast(getViewLifecycleOwner())
                        } else {
                            currentLocationMarker.setPosition(currentPosition)
                        }
                    }
                }
            })
        setListeners()
    }

    private fun setMapUi() {
        if (googleMap != null) {
            googleMap.getUiSettings().setCompassEnabled(true)
            if (permissionActivity.checkSelfPermission(
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
                && permissionActivity.checkSelfPermission(
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                googleMap.setMyLocationEnabled(true)
                googleMap.getUiSettings().setMyLocationButtonEnabled(true)
                googleMap.setOnMyLocationButtonClickListener(object :
                    GoogleMap.OnMyLocationButtonClickListener {
                    override fun onMyLocationButtonClick(): Boolean {
                        cameraPositionBuilder.target(currentLocationMarker.getPosition())
                        cameraPositionBuilder.zoom(16f)
                        googleMap.animateCamera(
                            CameraUpdateFactory.newCameraPosition(
                                cameraPositionBuilder.build()
                            )
                        )
                        Log.e("onMyLocationButton", "Called")
                        viewModel.weatherForecast(getViewLifecycleOwner())
                        return true
                    }
                })
                googleMap.setOnMyLocationClickListener(object : GoogleMap.OnMyLocationClickListener {
                    override fun onMyLocationClick(location: Location) {
                        permissionActivity.fusedLocation.currentLocation.setValue(location)
                        viewModel.currentLocation = location
                        val currentPosition = LatLng(location.latitude, location.longitude)
                        currentLocationMarker.setPosition(currentPosition)
                        cameraPositionBuilder.target(currentLocationMarker.getPosition())
                        cameraPositionBuilder.zoom(16f)
                        googleMap.animateCamera(
                            CameraUpdateFactory.newCameraPosition(
                                cameraPositionBuilder.build()
                            )
                        )
                        Log.e("onMyLocation", "Called")
                        viewModel.weatherForecast(getViewLifecycleOwner())
                    }
                })
            }

//            googleMap.getUiSettings().setZoomControlsEnabled(true);
            googleMap.getUiSettings().setMapToolbarEnabled(true)
            googleMap.getUiSettings().setTiltGesturesEnabled(true)
            googleMap.getUiSettings().setRotateGesturesEnabled(true)
        }
    }

    private fun setListeners() {
        binding.weatherCard.setOnClickListener(View.OnClickListener {
            if (viewModel.weather.getValue() != null) {
                DialogPrompt().showWeather(
                    permissionActivity,
                    getLayoutInflater(),
                    viewModel.weather.getValue()!!
                )
            }
        })
        binding.category.setOnItemClickListener(object : AdapterView.OnItemClickListener {
            override fun onItemClick(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                if (position < categories.size - 1) {
                    binding.searchPlaces.setText(categories[position])
                    fetchPlaces()
                }
            }
        })
        binding.searchPlaces.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                val result: Result = addressAdapter.resultList.get(position)
                val latLng = LatLng(result.position!!.lat!!, result.position!!.lon!!)
                if (destinationMarker != null) destinationMarker.remove()
                destinationMarker = googleMap.addMarker(MarkerOptions().position(latLng))!!
                //                viewModel.fetchRoutes(latLng).observe(getViewLifecycleOwner(), new Observer<RouteResponse>() {
    //                    @Override
    //                    public void onChanged(RouteResponse routeResponse) {
    //                        if (routeResponse != null) createRoute(routeResponse);
    //                    }
    //                });
                binding.searchPlaces.setText(result.poi!!.name, false)
                binding.searchPlaces.dismissDropDown()
            }
        }
        binding.searchPlaces.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (!binding.category.getText().toString()
                        .equals("-")
                ) binding.category.setText("-")
                fetchPlaces()
            }
        })
    }

    private fun fetchPlaces() {
        val query: String = binding.searchPlaces.getText().toString()
        val radius: Int = binding.radius.getText().toString().toInt()
        if (!query.isEmpty()) {
            if (currentLocationMarker != null) Log.e("Query", query + "")
            Log.e("Radius", radius.toString() + "")
            Log.e("Position", currentLocationMarker.getPosition().toString())
            viewModel.autoComplete(
                query,
                radius,
                currentLocationMarker.getPosition(),
                getViewLifecycleOwner()
            )
        }
    }

    private fun createRoute(routeResponse: RouteResponse) {
        if (destinationMarker != null) destinationMarker.remove()
        //        if (polyline != null) polyline.remove();
//        PolylineOptions polylineOptions = new PolylineOptions().color(R.color.fav_red);
        val markerOptions = MarkerOptions()
        //                        if (!routeResponse.getRoutes().isEmpty()) {
//        List<Leg> legs = routeResponse.getRoutes().get(0).getLegs();
////                            if (!legs.isEmpty()) {
//        List<Point> points = legs.get(0).getPoints();
//        for (Point point : points) {
//            polylineOptions.add(new LatLng(point.latitude, point.longitude));
//        }
        markerOptions.position(viewModel.destination!!)
        destinationMarker = googleMap.addMarker(markerOptions)!!
        Log.e("Destination", "Marker Added")
        val bounds: LatLngBounds = LatLngBounds.Builder()
            .include(currentLocationMarker.getPosition())
            .include(destinationMarker.getPosition())
            .build()
        //        polyline = googleMap.addPolyline(polylineOptions);
        Log.e("Destination", "Polyline Added")
        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 16))
        //                            }
//                        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            SensorManager.getRotationMatrixFromVector(rMat, event.values)
            mAzimuth = (Math.toDegrees(
                SensorManager.getOrientation(rMat, orientation).get(0).toDouble()
            ) + 360).toInt() % 360
        }
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, mLastAccelerometer, 0, event.values.size)
            mLastAccelerometerSet = true
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, mLastMagnetometer, 0, event.values.size)
            mLastMagnetometerSet = true
        }
        if (mLastAccelerometerSet && mLastMagnetometerSet) {
            SensorManager.getRotationMatrix(rMat, null, mLastAccelerometer, mLastMagnetometer)
            SensorManager.getOrientation(rMat, orientation)
            mAzimuth = (Math.toDegrees(
                SensorManager.getOrientation(rMat, orientation).get(0).toDouble()
            ) + 360).toInt() % 360
        }
        mAzimuth = Math.round(mAzimuth.toFloat())
        binding.imgCompass.rotation = -mAzimuth.toFloat()
    }

    override fun onAccuracyChanged(sensor: Sensor, i: Int) {}
    fun start() {
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) == null) {
            if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) == null || mSensorManager.getDefaultSensor(
                    Sensor.TYPE_MAGNETIC_FIELD
                ) == null
            ) {
                binding.imgCompass.setVisibility(View.GONE)
            } else {
                mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)!!
                mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)!!
                haveSensor = mSensorManager.registerListener(
                    this,
                    mAccelerometer,
                    SensorManager.SENSOR_DELAY_UI
                )
                haveSensor2 = mSensorManager.registerListener(
                    this,
                    mMagnetometer,
                    SensorManager.SENSOR_DELAY_UI
                )
            }
        } else {
            mRotationV = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)!!
            haveSensor =
                mSensorManager.registerListener(this, mRotationV, SensorManager.SENSOR_DELAY_UI)
        }
    }

    fun stop() {
        if (haveSensor) {
            mSensorManager.unregisterListener(this, mRotationV)
        } else {
            mSensorManager.unregisterListener(this, mAccelerometer)
            mSensorManager.unregisterListener(this, mMagnetometer)
        }
    }

    override fun onPause() {
        super.onPause()
        stop()
    }

    override fun onResume() {
        super.onResume()
        start()
    }
}
