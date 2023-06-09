package app.dating.appsuccessor.ui.activity.splash

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import app.dating.appsuccessor.R
import app.dating.appsuccessor.model.UserModel
import app.dating.appsuccessor.ui.activity.MainActivity
import app.dating.appsuccessor.ui.activity.auth.DOBActivity
import app.dating.appsuccessor.ui.activity.auth.EmailActivity
import app.dating.appsuccessor.ui.activity.auth.GenderActivity
import app.dating.appsuccessor.ui.activity.auth.IntroActivity
import app.dating.appsuccessor.ui.activity.auth.LikeToDateActivity
import app.dating.appsuccessor.ui.activity.auth.LocationActivity
import app.dating.appsuccessor.ui.activity.auth.LocationRangeActivity
import app.dating.appsuccessor.ui.activity.auth.LookingForActivity
import app.dating.appsuccessor.ui.activity.auth.NameActivity
import app.dating.appsuccessor.ui.activity.auth.ProfileImagesActivity
import app.dating.appsuccessor.ui.activity.auth.StoryActivity
import app.dating.appsuccessor.ui.utils.setStatusBarColor
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class SplashScreenActivity : AppCompatActivity() {
    var data: UserModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        setStatusBarColor(Color.parseColor("#51238C"))

        val user = FirebaseAuth.getInstance().currentUser

        Handler().postDelayed({
            if (user == null) {
                Log.d("MAINACITIGHG", "inside the user")
                startActivity(Intent(this@SplashScreenActivity, IntroActivity::class.java))
                finish()
            }
        }, 1200)


        Handler().postDelayed({
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val snapshot = withContext(Dispatchers.IO) {
                        FirebaseDatabase.getInstance().getReference("users")
                            .child(FirebaseAuth.getInstance().currentUser!!.phoneNumber!!).get()
                            .await()
                    }
                    Log.d("MAINACITIGHG", "entered")
                    if (snapshot.exists()) {
                        Log.d("MAINACITIGHG", "exitst")
                        data = snapshot.getValue(UserModel::class.java)
                        Log.d("MAINACITIGHG", "$data")
                        Log.d("MAINACITIGHG", "${data?.name}")

                        when {
                            user == null -> {
                                Log.d("MAINACITIGHG", "${data?.email} Navigated email splash")
                                startActivity(
                                    Intent(this@SplashScreenActivity, IntroActivity::class.java)
                                )
                                finish()
                            }

                            data?.email == null -> {
                                Log.d("MAINACITIGHG", "${data?.email} Navigated email splash")
                                startActivity(
                                    Intent(this@SplashScreenActivity, EmailActivity::class.java)
                                )
                                finish()
                            }

                            data?.location == null -> {
                                Log.d("MAINACITIGHG", "${data?.gender} Navigated location splash")
                                startActivity(
                                    Intent(this@SplashScreenActivity, LocationActivity::class.java)
                                )
                                finish()
                            }

                            data?.name == null -> {
                                Log.d("MAINACITIGHG", "${data?.name} Navigated name splash")
                                startActivity(
                                    Intent(this@SplashScreenActivity, NameActivity::class.java)
                                )
                                finish()
                            }

                            data?.dob == null -> {
                                Log.d("MAINACITIGHG", "${data?.name} Navigated dob splash")
                                startActivity(
                                    Intent(this@SplashScreenActivity, DOBActivity::class.java)
                                )
                                finish()
                            }

                            data?.gender == null -> {
                                Log.d("MAINACITIGHG", "${data?.name} Navigated gender splash")
                                startActivity(
                                    Intent(this@SplashScreenActivity, GenderActivity::class.java)
                                )
                                finish()
                            }

                            (data?.images?.size ?: 0) <= 1 -> {
                                Log.d("MAINACITIGHG", "${data?.name} Navigated gender splash")
                                startActivity(
                                    Intent(
                                        this@SplashScreenActivity,
                                        ProfileImagesActivity::class.java
                                    )
                                )
                                finish()
                            }

                            data?.story == null -> {
                                Log.d("MAINACITIGHG", "${data?.name} Navigated gender splash")
                                startActivity(
                                    Intent(this@SplashScreenActivity, StoryActivity::class.java)
                                )
                                finish()
                            }

                            data?.lookingFor == null -> {
                                Log.d("MAINACITIGHG", "${data?.name} Navigated gender splash")
                                startActivity(
                                    Intent(
                                        this@SplashScreenActivity,
                                        LookingForActivity::class.java
                                    )
                                )
                                finish()
                            }

                            data?.likeToDate == null -> {
                                Log.d("MAINACITIGHG", "${data?.name} Navigated gender splash")
                                startActivity(
                                    Intent(
                                        this@SplashScreenActivity,
                                        LikeToDateActivity::class.java
                                    )
                                )
                                finish()
                            }

                            data?.locationRange == null -> {
                                Log.d("MAINACITIGHG", "${data?.name} Navigated gender splash")
                                startActivity(
                                    Intent(
                                        this@SplashScreenActivity,
                                        LocationRangeActivity::class.java
                                    )
                                )
                                finish()
                            }

                            else -> {
                                Log.d("MAINACITIGHG", "${data?.name} Navigated main")
                                startActivity(
                                    Intent(this@SplashScreenActivity, MainActivity::class.java)
                                )
                                finish()
                            }
                        }
                    } else {
                        startActivity(
                            Intent(this@SplashScreenActivity, IntroActivity::class.java)
                        )
                        finish()
                    }
                } catch (e: Exception) {
                }
            }
        }, 700)


    }
}