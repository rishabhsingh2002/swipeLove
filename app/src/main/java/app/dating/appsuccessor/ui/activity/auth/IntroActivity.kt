package app.dating.appsuccessor.ui.activity.auth

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import app.dating.appsuccessor.databinding.ActivityIntroBinding
import app.dating.appsuccessor.ui.utils.clickTo
import app.dating.appsuccessor.ui.utils.setStatusBarColor

class IntroActivity : AppCompatActivity() {
    lateinit var ui: ActivityIntroBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(ui.root)

        setStatusBarColor(Color.parseColor("#F7F9F7"))

        ui.apply {
            createAccount.clickTo {
                startActivity(Intent(this@IntroActivity, LoginActivity::class.java))
            }
            login.clickTo {
                startActivity(Intent(this@IntroActivity, LoginActivity::class.java))
            }
        }
    }
}