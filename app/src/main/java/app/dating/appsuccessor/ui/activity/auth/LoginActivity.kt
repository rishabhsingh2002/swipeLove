package app.dating.appsuccessor.ui.activity.auth

import FcmTokenHandler
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.text.style.StyleSpan
import android.text.style.TypefaceSpan
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import app.dating.appsuccessor.R
import app.dating.appsuccessor.databinding.ActivityLoginBinding
import app.dating.appsuccessor.model.UserModel
import app.dating.appsuccessor.ui.activity.MainActivity
import app.dating.appsuccessor.ui.utils.Config
import app.dating.appsuccessor.ui.utils.clickTo
import app.dating.appsuccessor.ui.utils.setStatusBarColor
import app.dating.appsuccessor.ui.utils.showToast
import app.dating.appsuccessor.viewModel.LoginViewModel
import com.google.firebase.auth.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.regex.Pattern

class LoginActivity : AppCompatActivity() {
    lateinit var ui: ActivityLoginBinding

    private lateinit var viewModel: LoginViewModel
    val auth = FirebaseAuth.getInstance()
    var data: UserModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(ui.root)

        setStatusBarColor(Color.parseColor("#F7F9F7"))
        ui.apply {
            back.clickTo {
                onBackPressed()
            }
            backFromOtp.clickTo {
                numberLayout.visibility = VISIBLE
                otpLayout.visibility = GONE
            }
        }

        checkForActiveButtonPhone()
        checkForActiveButtonOTP()

        init()
    }

    fun init() {
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        ui.next.clickTo {
            if (isValidMobile("+91${ui.phoneNumber.text}")) {
                viewModel.sendOtp("+91${ui.phoneNumber.text}", this@LoginActivity)
            } else {
                showToast(this@LoginActivity, "Please enter valid Phone Number")
            }
        }
        ui.resendOtp.clickTo {
            val phoneNumber = ui.phoneNumber.text.toString()
            viewModel.resendOtp(phoneNumber = "+91" + phoneNumber, activity = this@LoginActivity)
        }

        ui.nextFromOtp.clickTo {
            val otp = ui.otp.text.toString()
            if (otp.length == 6) {
                viewModel.verifyOtp(otp)
            } else {
                showToast(
                    this@LoginActivity,
                    "The OTP you have entered is incorrect! Please try again."
                )
            }

        }

        viewModel.showLoading.observe(this) {
            if (it) {
                Config.showDialog(this@LoginActivity)
            } else {
                Config.hideDialog()
            }
        }

        viewModel.showVerification.observe(this, {
            if (it) {
                ui.otpLayout.visibility = View.VISIBLE
                ui.numberLayout.visibility = View.GONE
            } else {
                ui.numberLayout.visibility = View.VISIBLE
                ui.otpLayout.visibility = View.GONE
            }
        })

        viewModel.navigateToHome.observe(this, {
            if (it) {
                checkUserExist(ui.phoneNumber.text.toString())
            }
        })
        viewModel.showOTPError.observe(this, {
            if (it) {
                showToast(
                    this@LoginActivity,
                    "The OTP you have entered is incorrect! Please try again."
                )
            }
        })
    }


    private fun checkForActiveButtonOTP() {
        val editText = ui.otp
        val button = ui.nextFromOtp
        val activeButtonStyle = R.style.ButtonActive
        val inactiveButtonStyle = R.style.ButtonInactive
        val attrs = intArrayOf(android.R.attr.textColor, android.R.attr.background)
        val typedArray = obtainStyledAttributes(activeButtonStyle, attrs)
        val activeTextColor = typedArray.getColor(0, 0)
        val activeBackground = typedArray.getDrawable(1)
        typedArray.recycle()

        val inactiveTextColor = ContextCompat.getColor(this, R.color.primary_light_blue)
        val inactiveBackground = ContextCompat.getDrawable(this, R.drawable.edit_text_bg_unselected)

        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No action needed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // No action needed
            }

            override fun afterTextChanged(s: Editable?) {
                val enteredText = s.toString()

                // Check if the entered text has 10 digits
                if (enteredText.length == 6) {
                    // Enable the EditText and button for interaction
                    button.setTextColor(activeTextColor)
                    button.background = activeBackground
                    button.setTextAppearance(this@LoginActivity, activeButtonStyle)
                } else {
                    // Disable the EditText and button
                    button.setTextColor(inactiveTextColor)
                    button.background = inactiveBackground
                    button.setTextAppearance(this@LoginActivity, inactiveButtonStyle)
                }
            }
        })
    }

    private fun checkForActiveButtonPhone() {
        val editText = ui.phoneNumber
        val button = ui.next

        val activeButtonStyle = R.style.ButtonActive
        val inactiveButtonStyle = R.style.ButtonInactive

        val attrs = intArrayOf(android.R.attr.textColor, android.R.attr.background)
        val typedArray = obtainStyledAttributes(activeButtonStyle, attrs)
        val activeTextColor = typedArray.getColor(0, 0)
        val activeBackground = typedArray.getDrawable(1)
        typedArray.recycle()

        val inactiveTextColor = ContextCompat.getColor(this, R.color.primary_light_blue)
        val inactiveBackground = ContextCompat.getDrawable(this, R.drawable.edit_text_bg_unselected)

        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No action needed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // No action needed
            }

            override fun afterTextChanged(s: Editable?) {
                val enteredText = s.toString()

                // Check if the entered text has 10 digits
                if (enteredText.length == 10) {
                    // Enable the EditText and button for interaction
                    button.setTextColor(activeTextColor)
                    button.background = activeBackground
                    button.setTextAppearance(this@LoginActivity, activeButtonStyle)
                } else {
                    // Disable the EditText and button
                    button.setTextColor(inactiveTextColor)
                    button.background = inactiveBackground
                    button.setTextAppearance(this@LoginActivity, inactiveButtonStyle)
                }
            }
        })
    }


    private val fcmTokenHandler = FcmTokenHandler(this)

    private fun checkUserExist(number: String) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val snapshot = withContext(Dispatchers.IO) {
                    FirebaseDatabase.getInstance().getReference("users").child("+91$number")
                        .get().await()
                }

                if (snapshot.exists()) {
                    data = snapshot.getValue(UserModel::class.java)
                    // Updating the FCM token
                    fcmTokenHandler.updateFcmToken()
                    Log.d("LOginRIshabhSingn", "NAIgated")

                    when {
                        data?.email == null -> {
                            Log.d("MAINACITIGHG", "${data?.email} Navigated email splash")
                            startActivity(
                                Intent(this@LoginActivity, EmailActivity::class.java)
                            )
                            finish()
                        }

                        data?.location == null -> {
                            Log.d("MAINACITIGHG", "${data?.gender} Navigated location splash")
                            startActivity(
                                Intent(this@LoginActivity, LocationActivity::class.java)
                            )
                            finish()
                        }

                        data?.name == null -> {
                            Log.d("MAINACITIGHG", "${data?.name} Navigated name splash")
                            startActivity(
                                Intent(this@LoginActivity, NameActivity::class.java)
                            )
                            finish()
                        }

                        data?.dob == null -> {
                            Log.d("MAINACITIGHG", "${data?.name} Navigated dob splash")
                            startActivity(
                                Intent(this@LoginActivity, DOBActivity::class.java)
                            )
                            finish()
                        }

                        data?.gender == null -> {
                            Log.d("MAINACITIGHG", "${data?.name} Navigated gender splash")
                            startActivity(
                                Intent(this@LoginActivity, GenderActivity::class.java)
                            )
                            finish()
                        }

                        (data?.images?.size ?: 0) <= 1 -> {
                            Log.d("MAINACITIGHG", "${data?.name} Navigated gender splash")
                            startActivity(
                                Intent(
                                    this@LoginActivity,
                                    ProfileImagesActivity::class.java
                                )
                            )
                            finish()
                        }

                        data?.story == null -> {
                            Log.d("MAINACITIGHG", "${data?.name} Navigated gender splash")
                            startActivity(
                                Intent(this@LoginActivity, StoryActivity::class.java)
                            )
                            finish()
                        }

                        data?.lookingFor == null -> {
                            Log.d("MAINACITIGHG", "${data?.name} Navigated gender splash")
                            startActivity(
                                Intent(
                                    this@LoginActivity,
                                    LookingForActivity::class.java
                                )
                            )
                            finish()
                        }

                        data?.likeToDate == null -> {
                            Log.d("MAINACITIGHG", "${data?.name} Navigated gender splash")
                            startActivity(
                                Intent(
                                    this@LoginActivity,
                                    LikeToDateActivity::class.java
                                )
                            )
                            finish()
                        }

                        data?.locationRange == null -> {
                            Log.d("MAINACITIGHG", "${data?.name} Navigated gender splash")
                            startActivity(
                                Intent(
                                    this@LoginActivity,
                                    LocationRangeActivity::class.java
                                )
                            )
                            finish()
                        }

                        else -> {
                            Log.d("MAINACITIGHG", "${data?.name} Navigated main")
                            startActivity(
                                Intent(this@LoginActivity, MainActivity::class.java)
                            )
                            finish()
                        }
                    }
                } else {
                    Log.d("MAINACITIGHG", "${data?.name} Navigated email Login")
                    startActivity(
                        Intent(this@LoginActivity, EmailActivity::class.java)
                    )
                    finish()
                }
            } catch (error: Throwable) {
                try {
                    Config.hideDialog()
                } catch (e: Exception) {
                }
                Toast.makeText(this@LoginActivity, error.message, Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun isValidMobile(phone: String): Boolean {
        return if (!Pattern.matches("[a-zA-Z]+", phone)) {
            phone.length == 13
        } else false
    }


}