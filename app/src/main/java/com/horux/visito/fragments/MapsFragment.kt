package com.horux.visito.fragments

import android.Manifest
import android.content.Context
import android.hardware.Sensor
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.horux.visito.models.tomtom.autocomplete.Result
import java.util.Arrays
import java.util.Locale
import java.util.function.Predicate
import java.util.stream.Collectors

class MapsFragment : Fragment(), SensorEventListener {
    var mAzimuth = 0
    var haveSensor = false
    var haveSensor2 = false
    var rMat = FloatArray(9)
    var orientation = FloatArray(3)
    private var permissionActivity: PermissionActivity? = null
    private var binding: FragmentMapsBinding? = null
    private var googleMap: GoogleMap? = null
    private var currentLocationMarker: Marker? = null
    private var destinationMarker: Marker? = null

    //    private Polyline polyline;
    private var cameraPositionBuilder: CameraPosition.Builder? = null
    private var viewModel: MapViewModel? = null
    private var addressAdapter: AddressAdapter? = null
    private val categories = ArrayList<String>()
    private val radiuses = ArrayList<String>()
    private var mSensorManager: SensorManager? = null
    private var mRotationV: Sensor? = null
    private var mAccelerometer: Sensor? = null
    private var mMagnetometer: Sensor? = null
    private val mLastAccelerometer = FloatArray(3)
    private val mLastMagnetometer = FloatArray(3)
    private var mLastAccelerometerSet = false
    private var mLastMagnetometerSet = false
    fun onCreateView(
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

    fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.map.onCreate(savedInstanceState)
        permissionActivity = requireActivity() as PermissionActivity?
        categories.addAll(Arrays.asList(getResources().getStringArray(R.array.categories)))
        radiuses.addAll(Arrays.asList(getResources().getStringArray(R.array.radius)))
        mSensorManager =
            permissionActivity.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        setAdapters()
        binding.searchPlaces.setDropDownBackgroundResource(R.color.white)
    }

    private fun setAdapters() {
        binding.category.setAdapter(
            ArrayAdapter<Any?>(
                permissionActivity,
                R.layout.row_spinner,
                R.id.chosen_value,
                categories.toTypedArray()
            )
        )
        binding.category.setText(categories[categories.size - 1], false)
        binding.radius.setAdapter(
            ArrayAdapter<Any?>(
                permissionActivity,
                R.layout.row_spinner,
                R.id.chosen_value,
                radiuses.toTypedArray()
            )
        )
        binding.radius.setText(radiuses[radiuses.size - 1], false)
    }

    fun onStart() {
        super.onStart()
        if (permissionActivity == null) permissionActivity = requireActivity() as HomeActivity?
        binding.map.onStart()
        permissionActivity.startLocationUpdates()
        viewModel.autoComplete.observe(
            getViewLifecycleOwner(),
            Observer<Any?> { autoCompleteResponse ->
                if (autoCompleteResponse != null && autoCompleteResponse.getResults() != null) {
                    Log.e("Result", autoCompleteResponse.getResults().toString())
                    val addresses = ArrayList<Result>()
                    val categoryString: String = binding.category.getText().toString().toLowerCase()
                    Log.e("CategoryString", categoryString)
                    val results: List<Result> = autoCompleteResponse.getResults().stream().filter(
                        Predicate<Result> { result ->
                            result.poi.categories.stream().anyMatch { s ->
                                s.lowercase(
                                    Locale.getDefault()
                                ) == categoryString
                            }
                        }).collect<List<Result>, Any>(
                        Collectors.toList<Result>()
                    )
                    if (results.isEmpty()) addresses.addAll(autoCompleteResponse.getResults()) else addresses.addAll(
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
            Observer<Any?> { currentWeatherResponse ->
                if (currentWeatherResponse != null) {
                    binding.weatherCard.setVisibility(View.VISIBLE)
                    binding.weatherCondition.setText(
                        currentWeatherResponse.getCurrent().getCondition().getText()
                    )
                    binding.temperature.setText(
                        currentWeatherResponse.getCurrent().getTempC().toString() + " \u2103"
                    )
                } else {
                    binding.weatherCard.setVisibility(View.GONE)
                }
            })
        binding.map.getMapAsync(object : OnMapReadyCallback() {
            fun onMapReady(googleMap: GoogleMap) {
                this@MapsFragment.googleMap = googleMap
                setMapUi()
                cameraPositionBuilder = Builder()
                val location: Location = permissionActivity.fusedLocation.currentLocation.getValue()
                val latLngBoundsBuilder: LatLngBounds.Builder = Builder()
                if (location != null) {
                    val currentPosition = LatLng(location.latitude, location.longitude)
                    if (currentLocationMarker == null) currentLocationMarker =
                        googleMap.addMarker(MarkerOptions().position(currentPosition)) else currentLocationMarker.setPosition(
                        currentPosition
                    )
                    googleMap.animateCamera(
                        CameraUpdateFactory.newCameraPosition(
                            Builder().target(
                                currentPosition
                            ).zoom(16).build()
                        )
                    )
                    var destinationPosition: LatLng? = null
                    if (getArguments() != null) {
                        val type: String = getArguments().getString(AppConstants.STRING_TYPE)
                        if (type != null) {
                            if (type == AppConstants.STRING_PLACES) {
                                val place: PlaceModel =
                                    getArguments().getParcelable(AppConstants.STRING_DATA)
                                if (place != null) {
                                    destinationPosition =
                                        LatLng(place.getLatitude(), place.getLongitude())
                                }
                            } else {
                                val event: EventModel =
                                    getArguments().getParcelable(AppConstants.STRING_DATA)
                                if (event != null) {
                                    destinationPosition =
                                        LatLng(event.getLatitude(), event.getLongitude())
                                }
                            }
                            if (destinationPosition != null) {
                                if (destinationMarker == null) destinationMarker =
                                    googleMap.addMarker(MarkerOptions().position(destinationPosition)) else destinationMarker.setPosition(
                                    destinationPosition
                                )
                            }
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
            object : Observer<Location?> {
                override fun onChanged(location: Location) {
                    viewModel.currentLocation = location
                    if (googleMap != null) {
                        val currentPosition = LatLng(location.latitude, location.longitude)
                        if (currentLocationMarker == null) {
                            currentLocationMarker =
                                googleMap.addMarker(MarkerOptions().position(currentPosition))
                            cameraPositionBuilder.target(currentLocationMarker.getPosition())
                            cameraPositionBuilder.zoom(16)
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
            if (ActivityCompat.checkSelfPermission(
                    permissionActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                    permissionActivity,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                googleMap.setMyLocationEnabled(true)
                googleMap.getUiSettings().setMyLocationButtonEnabled(true)
                googleMap.setOnMyLocationButtonClickListener(object :
                    OnMyLocationButtonClickListener() {
                    fun onMyLocationButtonClick(): Boolean {
                        cameraPositionBuilder.target(currentLocationMarker.getPosition())
                        cameraPositionBuilder.zoom(16)
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
                googleMap.setOnMyLocationClickListener(object : OnMyLocationClickListener() {
                    fun onMyLocationClick(location: Location) {
                        permissionActivity.fusedLocation.currentLocation.setValue(location)
                        viewModel.currentLocation = location
                        val currentPosition = LatLng(location.latitude, location.longitude)
                        currentLocationMarker.setPosition(currentPosition)
                        cameraPositionBuilder.target(currentLocationMarker.getPosition())
                        cameraPositionBuilder.zoom(16)
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
                    viewModel.weather.getValue()
                )
            }
        })
        binding.category.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                if (position < categories.size - 1) {
                    binding.searchPlaces.setText(categories[position])
                    fetchPlaces()
                }
            }
        })
        binding.searchPlaces.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                val result: Result = addressAdapter.resultList.get(position)
                val latLng = LatLng(result.position.lat, result.position.lon)
                if (destinationMarker != null) destinationMarker.remove()
                destinationMarker = googleMap.addMarker(MarkerOptions().position(latLng))
                //                viewModel.fetchRoutes(latLng).observe(getViewLifecycleOwner(), new Observer<RouteResponse>() {
//                    @Override
//                    public void onChanged(RouteResponse routeResponse) {
//                        if (routeResponse != null) createRoute(routeResponse);
//                    }
//                });
                binding.searchPlaces.setText(result.poi.name, false)
                binding.searchPlaces.dismissDropDown()
            }
        })
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
//            polylineOptions.add(new LatLng(point.getLatitude(), point.getLongitude()));
//        }
        markerOptions.position(viewModel.destination)
        destinationMarker = googleMap.addMarker(markerOptions)
        Log.e("Destination", "Marker Added")
        val bounds: LatLngBounds = Builder()
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
        binding.imgCompass.setRotation(-mAzimuth)
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
                mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
                mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
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
            mRotationV = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
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

    fun onPause() {
        super.onPause()
        stop()
    }

    fun onResume() {
        super.onResume()
        start()
    }
}
