package app.dating.appsuccessor.ui.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import app.dating.appsuccessor.R
import app.dating.appsuccessor.databinding.ActivityMainBinding
import app.dating.appsuccessor.ui.activity.auth.GenderActivity
import app.dating.appsuccessor.ui.utils.Config
import app.dating.appsuccessor.viewModel.MainActivityViewModel
import app.dating.appsuccessor.viewModel.NameViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.messaging.FirebaseMessaging


class MainActivity : AppCompatActivity() {
    private lateinit var ui: ActivityMainBinding
    private lateinit var navController: NavController
    private val viewModel: MainActivityViewModel by viewModels()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this@MainActivity, "Permission Granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(
                this@MainActivity, "Please enable notification to get updates", Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = ActivityMainBinding.inflate(layoutInflater)
        setContentView(ui.root)

        // Check notification permission
        checkNotificationPermission()

        navController = findNavController(R.id.fragment)
        setUpSmoothBottomMenu()

        updateFcmToken()

    }

    fun updateFcmToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d("FCMTKEN", "$token")
                viewModel.updateUserFCMToken(fcmToken = token)
                viewModel.updateFcmTokenSuccess.observe(this@MainActivity) { success ->
                    if (success) {
                    } else {
                        Toast.makeText(
                            this@MainActivity, "Something went wrong", Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
                // Handle error getting FCM token
            }
        }
    }

    fun setUpSmoothBottomMenu() {
        val popMenu = PopupMenu(this, null)
        popMenu.inflate(R.menu.bottom_menu)
        val menu = popMenu.menu
        ui.bottomBar.setupWithNavController(menu, navController)
    }

    private fun checkNotificationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this@MainActivity, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED -> {
            }

            shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                Snackbar.make(
                    getRootView() ?: return, "Notification blocked", Snackbar.LENGTH_LONG
                ).setAction("Settings") {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    val uri: Uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                }.show()
            }

            else -> {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun getRootView(): View? {
        val contentViewGroup = findViewById<View>(android.R.id.content) as ViewGroup
        return contentViewGroup.getChildAt(0) ?: window?.decorView?.rootView
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

}
