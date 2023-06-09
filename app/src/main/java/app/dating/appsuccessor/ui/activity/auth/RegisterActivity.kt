package app.dating.appsuccessor.ui.activity.auth

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import app.dating.appsuccessor.databinding.ActivityRegisterBinding
import app.dating.appsuccessor.model.UserModel
import app.dating.appsuccessor.ui.utils.Config
import app.dating.appsuccessor.ui.utils.Config.hideDialog
import app.dating.appsuccessor.ui.utils.Constants
import app.dating.appsuccessor.ui.utils.clickTo
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage

class RegisterActivity : AppCompatActivity() {
    lateinit var ui: ActivityRegisterBinding
    private var imageUri: Uri? = null
    var userLocation: String? = ""


    private val selectImage = registerForActivityResult(ActivityResultContracts.GetContent()) {
        imageUri = it
        ui.profileImage.setImageURI(imageUri)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(ui.root)

        //location permission
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                Constants.REQUEST_LOCATION_PERMISSION
            )
        } else {
            enableLocation()
        }

        ui.apply {
            profileImage.clickTo {
                selectImage.launch("image/*")
            }
            location.clickTo {
                try {
                    Config.showDialog(this@RegisterActivity)
                } catch (e: Exception) {
                }
                if (ActivityCompat.checkSelfPermission(
                        this@RegisterActivity, Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this@RegisterActivity,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        Constants.REQUEST_LOCATION_PERMISSION
                    )
                } else {
                    enableLocation()
                }
            }
            saveData.clickTo {
                validateData()
            }
        }
    }

    private fun validateData() {
        if (ui.name.text.toString().isEmpty()) {
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show()
        } else if (ui.email.text.toString().isEmpty()) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
        } else if (userLocation.toString().isEmpty()) {
            Toast.makeText(this, "Please enable your location", Toast.LENGTH_SHORT).show()
        } else if (imageUri == null) {
            Toast.makeText(this, "Please select your image", Toast.LENGTH_SHORT).show()
        } else {
            uploadImage()
        }
    }

    private fun uploadImage() {
        Config.showDialog(this)
        val storageRef = FirebaseStorage.getInstance().getReference("profile")
            .child(FirebaseAuth.getInstance().currentUser!!.uid).child("profile.jpg")

        storageRef.putFile(imageUri!!).addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener {
                storeData(it)
            }.addOnFailureListener {
                hideDialog()
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun storeData(imageUrl: Uri?) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }
            val token = task.result
            val number = FirebaseAuth.getInstance().currentUser?.phoneNumber

            val data = UserModel(
                name = ui.name.text.toString(),
                email = ui.email.text.toString(),
                location = userLocation,
//                image = imageUrl.toString(),
                number = number,
                fcmToken = token,
                rightSwiped = null
            )

            FirebaseDatabase.getInstance().getReference("users")
                .child(FirebaseAuth.getInstance().currentUser!!.phoneNumber!!).setValue(data)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        hideDialog()
                        Log.d("RISHDATAD", data.toString())
                        startActivity(Intent(this, GenderActivity::class.java))
                        finish()
//                        Toast.makeText(this, "User Register Successful", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, it.exception!!.message, Toast.LENGTH_SHORT).show()
                    }
                }
        })

    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableLocation()
            } else {
                // Permission denied
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
                if (ActivityCompat.checkSelfPermission(
                        this@RegisterActivity, Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this@RegisterActivity,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        Constants.REQUEST_LOCATION_PERMISSION
                    )
                } else {
                    enableLocation()
                }
            }
        }
    }

    private fun enableLocation() {
        val lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivityForResult(intent, Constants.REQUEST_ENABLE_LOCATION)
        } else {
            // Location is already enabled
            startLocationUpdates()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.REQUEST_ENABLE_LOCATION) {
            val lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                enableLocation()
                Toast.makeText(this, "Location services are still disabled", Toast.LENGTH_SHORT)
                    .show()
            } else {
                // Location is enabled by the user
                startLocationUpdates()
            }
        }
    }

    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location = locationResult.lastLocation
                val latitude = location?.latitude
                val longitude = location?.longitude
                userLocation = "$latitude,$longitude"
                ui.location.setText("Granted")
                try {
                    Config.hideDialog()
                } catch (e: Exception) {
                }
                // Do something with the user's location
            }
        }
        if (ActivityCompat.checkSelfPermission(
                this@RegisterActivity, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this@RegisterActivity, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        LocationServices.getFusedLocationProviderClient(this@RegisterActivity)
            .requestLocationUpdates(
                locationRequest, locationCallback, Looper.getMainLooper()
            )
    }
}