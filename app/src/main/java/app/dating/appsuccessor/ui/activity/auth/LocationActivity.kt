package app.dating.appsuccessor.ui.activity.auth

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.graphics.Color
import android.location.Criteria
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import app.dating.appsuccessor.databinding.ActivityLocationBinding
import app.dating.appsuccessor.ui.activity.MainActivity
import app.dating.appsuccessor.ui.utils.Config
import app.dating.appsuccessor.ui.utils.clickTo
import app.dating.appsuccessor.ui.utils.setStatusBarColor
import app.dating.appsuccessor.viewModel.EmailViewModel
import app.dating.appsuccessor.viewModel.LocationViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class LocationActivity : AppCompatActivity() {
    private lateinit var ui: ActivityLocationBinding
    private val viewModel: LocationViewModel by viewModels()
    private lateinit var locationManager: LocationManager

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = ActivityLocationBinding.inflate(layoutInflater)
        setContentView(ui.root)
        setStatusBarColor(Color.parseColor("#F7F9F7"))

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        ui.enableLocation.clickTo {
            if (isLocationPermissionGranted()) {
                // Location permission is already granted
                checkLocationSettings()
            } else {
                // Request location permission
                requestLocationPermission()
            }
        }

    }


    private fun isLocationPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun checkLocationSettings() {
        if (locationManager.isLocationEnabled) {
            // Location is enabled, proceed with location retrieval
            retrieveLocation()
        } else {
            // Location is disabled, prompt the user to enable it
            showLocationDisabledDialog()
        }
    }

    private fun retrieveLocation() {
        if (isLocationPermissionGranted()) {
            Config.showDialog(this@LocationActivity)  // showing the dialog
            val criteria = Criteria()
            criteria.accuracy = Criteria.ACCURACY_FINE

            val locationListener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    // Location has changed
                    val latitude = location.latitude
                    val longitude = location.longitude
                    // Use the latitude and longitude as needed
                    viewModel.updateUserLocation("$latitude,$longitude")
                    viewModel.updateLocationSuccess.observe(this@LocationActivity, { success ->
                        if (success) {
                            startActivity(Intent(this@LocationActivity, InfoOneActivity::class.java))
                            finish()
                            Config.hideDialog()
                        } else {
                            // error updating email
                            Toast.makeText(
                                this@LocationActivity,
                                "Something went wrong",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
                    locationManager.removeUpdates(this)
                }

                override fun onProviderDisabled(provider: String) {}
                override fun onProviderEnabled(provider: String) {}
                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            }

            lifecycleScope.launch(Dispatchers.Main) {
                withContext(Dispatchers.Main) {
                    if (ActivityCompat.checkSelfPermission(
                            this@LocationActivity,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            this@LocationActivity,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        return@withContext
                    }
                    locationManager.requestSingleUpdate(criteria, locationListener, null)
                }
            }
        }
    }

    private fun showLocationDisabledDialog() {
        AlertDialog.Builder(this)
            .setMessage("Location services are disabled. Please enable them to proceed.")
            .setPositiveButton("Settings") { _, _ ->
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
            .setNegativeButton("Cancel") { dialog: DialogInterface, _ ->
                dialog.dismiss()
                // Handle location disabled case as needed
            }
            .setCancelable(false)
            .show()
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Location permission granted
                checkLocationSettings()
            } else {
                // Location permission denied
                // Handle permission denied case as needed
            }
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }
}