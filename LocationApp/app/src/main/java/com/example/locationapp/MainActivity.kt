package com.example.locationapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.locationapp.databinding.ActivityMainBinding
import com.google.android.gms.location.*

class MainActivity : AppCompatActivity() {

    private val permissionID: Int by lazy { 1111 }
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var binding: ActivityMainBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        getLastLocation()
    }

    override fun onResume() {
        super.onResume()
        newLocationData()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode == permissionID){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Log.d("Debug:","You have the Permission")
            }
        }
    }


    @SuppressLint("MissingPermission")
    fun getLastLocation(){
        if(checkPermissions()) {
            if(isLocationEnabled()){
                fusedLocationProviderClient.lastLocation.addOnCompleteListener {task->
                   task.result?.let {
                       binding.txtLatLon.text = "Long: ${it.longitude} '\n' Lat: ${it.latitude} '\n' Bearing: ${it.bearing}"
                       updateCircleUI(it.accuracy, it.bearing)
                   } ?: newLocationData()
                }
            }else{
                Toast.makeText(this,"Please Turn on Your device Location",Toast.LENGTH_SHORT).show()
            }
        }else{
            requestRuntimePermission()
        }
    }


    @SuppressLint("MissingPermission")
    fun newLocationData(){
        if (checkPermissions()) {
            LocationRequest.create()?.apply {
                interval = 10000
                fastestInterval = 5000
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }.also { locationRequest ->
                fusedLocationProviderClient.run {
                    requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
                }
            }
        }
    }

    private val locationCallback = object : LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation.let {
                binding.txtLatLon.text = "Long: ${it.longitude} '\n' Lat: ${it.latitude} '\n' Bearing: ${it.bearing}"
                updateCircleUI(it.accuracy, it.bearing)
            }
        }
    }


    private fun checkPermissions() : Boolean {
        if (ActivityCompat
                .checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat
                .checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true
        }
        return false
    }

    private fun requestRuntimePermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION),
            permissionID
        )
    }


    private fun isLocationEnabled() : Boolean{
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun updateCircleUI(horizontalAccuracy: Float, horizontalDirection: Float) {
        binding.circleView.setHorizontalAccuracy(horizontalAccuracy)
        binding.circleView.setHorizontalDirection(horizontalDirection)
    }

}