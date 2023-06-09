package app.dating.appsuccessor.ui.activity.auth

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import app.dating.appsuccessor.databinding.ActivityInfoThreeBinding
import app.dating.appsuccessor.ui.utils.clickTo
import app.dating.appsuccessor.ui.utils.setStatusBarColor

class InfoThreeActivity : AppCompatActivity() {
    lateinit var ui: ActivityInfoThreeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = ActivityInfoThreeBinding.inflate(layoutInflater)
        setContentView(ui.root)

        setStatusBarColor(Color.parseColor("#F7F9F7"))

        ui.apply {
            accurateTest.clickTo {
                startActivity(Intent(this@InfoThreeActivity, AccurateTestActivity::class.java))
            }
            shortTest.clickTo {
                startActivity(Intent(this@InfoThreeActivity, ShortTestActivity::class.java))
            }
        }
    }
}