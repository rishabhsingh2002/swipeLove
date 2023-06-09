package app.dating.appsuccessor.ui.activity.auth

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import app.dating.appsuccessor.R
import app.dating.appsuccessor.databinding.ActivityInfoOneBinding
import app.dating.appsuccessor.ui.utils.clickTo
import app.dating.appsuccessor.ui.utils.setStatusBarColor

class InfoOneActivity : AppCompatActivity() {
    private lateinit var ui: ActivityInfoOneBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        ui = ActivityInfoOneBinding.inflate(layoutInflater)
        setContentView(ui.root)

        setStatusBarColor(Color.parseColor("#F7F9F7"))

        ui.apply {
            buildMyProfile.clickTo {
                startActivity(Intent(this@InfoOneActivity, NameActivity::class.java))
                finish()
            }
        }
    }

}