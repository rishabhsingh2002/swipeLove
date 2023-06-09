package app.dating.appsuccessor.ui.activity.auth

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import app.dating.appsuccessor.R
import app.dating.appsuccessor.databinding.ActivityNameBinding
import app.dating.appsuccessor.ui.utils.Config
import app.dating.appsuccessor.ui.utils.clickTo
import app.dating.appsuccessor.ui.utils.setStatusBarColor
import app.dating.appsuccessor.ui.utils.showToast
import app.dating.appsuccessor.viewModel.EmailViewModel
import app.dating.appsuccessor.viewModel.NameViewModel

class NameActivity : AppCompatActivity() {
    lateinit var ui: ActivityNameBinding
    private val viewModel: NameViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = ActivityNameBinding.inflate(layoutInflater)
        setContentView(ui.root)

        setStatusBarColor(Color.parseColor("#F7F9F7"))

        ui.apply {
            next.clickTo {
                if (username.text.toString().isNullOrEmpty()) {
                    showToast(this@NameActivity, "Please enter your name ")
                } else {
                    if (hideName.isChecked) {
                        Config.showDialog(this@NameActivity)
                        viewModel.updateUserNameHideStatus(true)
                        viewModel.updateShowNameSuccess.observe(this@NameActivity) { success ->
                            if (success) {
                                viewModel.updateUserName(name = username.text.toString())
                            } else {
                                Toast.makeText(
                                    this@NameActivity,
                                    "Something went wrong",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        Config.showDialog(this@NameActivity)
                        viewModel.updateUserNameHideStatus(false)
                        viewModel.updateShowNameSuccess.observe(this@NameActivity) { success ->
                            if (success) {
                                viewModel.updateUserName(name = username.text.toString())
                            } else {
                                Toast.makeText(
                                    this@NameActivity,
                                    "Something went wrong",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                    viewModel.updateNameSuccess.observe(this@NameActivity) { success ->
                        if (success) {
                            startActivity(Intent(this@NameActivity, DOBActivity::class.java))
                            Config.hideDialog()
                        } else {
                            Toast.makeText(
                                this@NameActivity,
                                "Something went wrong",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
            checkForActiveButtonName()
        }
    }

    private fun checkForActiveButtonName() {
        val editText = ui.username
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
                if (enteredText.length >= 2) {
                    // Enable the EditText and button for interaction
                    button.setTextColor(activeTextColor)
                    button.background = activeBackground
                    button.setTextAppearance(this@NameActivity, activeButtonStyle)
                } else {
                    // Disable the EditText and button
                    button.setTextColor(inactiveTextColor)
                    button.background = inactiveBackground
                    button.setTextAppearance(this@NameActivity, inactiveButtonStyle)
                }
            }
        })
    }
}