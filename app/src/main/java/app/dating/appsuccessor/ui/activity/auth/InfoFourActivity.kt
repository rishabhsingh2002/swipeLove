package app.dating.appsuccessor.ui.activity.auth

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import app.dating.appsuccessor.R
import app.dating.appsuccessor.databinding.ActivityInfoFourBinding
import app.dating.appsuccessor.ui.activity.MainActivity
import app.dating.appsuccessor.ui.utils.clickTo
import app.dating.appsuccessor.ui.utils.setStatusBarColor

class InfoFourActivity : AppCompatActivity() {
    lateinit var ui: ActivityInfoFourBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = ActivityInfoFourBinding.inflate(layoutInflater)
        setContentView(ui.root)

        setStatusBarColor(Color.parseColor("#F7F9F7"))

        ui.letsGo.clickTo {
            startActivity(Intent(this@InfoFourActivity, MainActivity::class.java))
            finish()
        }


    }
}