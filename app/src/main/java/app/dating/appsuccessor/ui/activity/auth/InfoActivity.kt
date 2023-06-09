package app.dating.appsuccessor.ui.activity.auth

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import app.dating.appsuccessor.R
import app.dating.appsuccessor.databinding.ActivityInfoBinding
import app.dating.appsuccessor.ui.utils.clickTo
import app.dating.appsuccessor.ui.utils.setStatusBarColor

class InfoActivity : AppCompatActivity() {
    lateinit var ui: ActivityInfoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = ActivityInfoBinding.inflate(layoutInflater)
        setContentView(ui.root)

        setStatusBarColor(Color.parseColor("#F7F9F7"))

        ui.datingPreferences.clickTo {
            startActivity(Intent(this@InfoActivity, LookingForActivity::class.java))
            finish()
        }

    }
}