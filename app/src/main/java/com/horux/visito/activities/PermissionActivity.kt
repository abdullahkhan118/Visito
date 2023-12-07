package com.horux.visito.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.horux.visito.operations.business_logic.FusedLocation
import java.util.Arrays

open class PermissionActivity : AppCompatActivity() {
    private val LOCATION_REQUEST_CODE = 101
    private val CALL_REQUEST_CODE = 102
    private val LOCATION_PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    private val CALL_PERMISSIONS = arrayOf(
        Manifest.permission.CALL_PHONE
    )
    var fusedLocation: FusedLocation = FusedLocation.instance
    var phoneNumber = ""
    fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, LOCATION_PERMISSIONS, LOCATION_REQUEST_CODE)
            return
        }
        fusedLocation.startLocationUpdates(this)
    }

    fun stopLocationUpdates() {
        fusedLocation.stopLocationUpdates()
    }

    val lastKnownLocation: Unit
        get() {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(this, LOCATION_PERMISSIONS, LOCATION_REQUEST_CODE)
                return
            }
            fusedLocation.getLastLocation(this)
        }

    fun callPhoneNumber(phoneNumber: String) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CALL_PHONE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, CALL_PERMISSIONS, CALL_REQUEST_CODE)
        } else {
            this.phoneNumber = phoneNumber.replace("(", "").replace(")", "").trim { it <= ' ' }
            val callIntent = Intent(
                Intent.ACTION_CALL, Uri.parse(
                    "tel:$phoneNumber"
                )
            )
            startActivity(callIntent)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.e("RequestCode", requestCode.toString() + "")
        Log.e("GrantResults", Arrays.toString(grantResults))
        if (requestCode == LOCATION_REQUEST_CODE) {
            var allGranted = true
            for (grantResult in grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false
                    break
                }
            }
            Log.e("AllGranted", allGranted.toString() + "")
            if (allGranted) {
                fusedLocation.startLocationUpdates(this)
            }
        } else if (requestCode == CALL_REQUEST_CODE) {
            val callIntent = Intent(
                Intent.ACTION_CALL, Uri.parse(
                    "tel:$phoneNumber"
                )
            )
            startActivity(callIntent)
        }
    }

    override fun onStop() {
        super.onStop()
        fusedLocation.stopLocationUpdates()
    }
}
