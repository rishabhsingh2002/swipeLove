package app.dating.appsuccessor.ui.activity.auth

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Email
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import app.dating.appsuccessor.R
import app.dating.appsuccessor.databinding.ActivityEmailBinding
import app.dating.appsuccessor.ui.utils.Config
import app.dating.appsuccessor.ui.utils.clickTo
import app.dating.appsuccessor.ui.utils.setStatusBarColor
import app.dating.appsuccessor.ui.utils.showToast
import app.dating.appsuccessor.viewModel.EmailViewModel
import java.util.regex.Matcher
import java.util.regex.Pattern

class EmailActivity : AppCompatActivity() {
    lateinit var ui: ActivityEmailBinding
    private val viewModel: EmailViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = ActivityEmailBinding.inflate(layoutInflater)
        setContentView(ui.root)
        setStatusBarColor(Color.parseColor("#F7F9F7"))

        ui.apply {
            next.clickTo {
                if (email.text.toString()
                        .isNullOrEmpty() || !isEmailValid(email = email.text.toString())
                ) {
                    showToast(this@EmailActivity, "Please enter valid Email Address.")
                } else {
                    Config.showDialog(this@EmailActivity)
                    viewModel.updateUserEmail(userEmail = email.text.toString())
                    viewModel.updateEmailSuccess.observe(this@EmailActivity) { success ->
                        if (success) {
                            startActivity(Intent(this@EmailActivity, LocationActivity::class.java))
                            Config.hideDialog()
                        } else {
                            // error updating email
                            Toast.makeText(
                                this@EmailActivity,
                                "Something went wrong",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
            checkForActiveButtonEmail()
        }
    }

    private fun checkForActiveButtonEmail() {
        val editText = ui.email
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
                if (enteredText.length >= 3) {
                    // Enable the EditText and button for interaction
                    button.setTextColor(activeTextColor)
                    button.background = activeBackground
                    button.setTextAppearance(this@EmailActivity, activeButtonStyle)
                } else {
                    // Disable the EditText and button
                    button.setTextColor(inactiveTextColor)
                    button.background = inactiveBackground
                    button.setTextAppearance(this@EmailActivity, inactiveButtonStyle)
                }
            }
        })
    }

    fun isEmailValid(email: String): Boolean {
        var isValid = false
        val expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
        val inputStr: CharSequence = email
        val pattern: Pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher: Matcher = pattern.matcher(inputStr)
        if (matcher.matches()) {
            isValid = true
        }
        return isValid
    }
}