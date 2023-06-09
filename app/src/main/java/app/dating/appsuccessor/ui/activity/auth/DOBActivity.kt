package app.dating.appsuccessor.ui.activity.auth

import android.app.DatePickerDialog
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
import app.dating.appsuccessor.databinding.ActivityDobactivityBinding
import app.dating.appsuccessor.ui.utils.Config
import app.dating.appsuccessor.ui.utils.clickTo
import app.dating.appsuccessor.ui.utils.setStatusBarColor
import app.dating.appsuccessor.ui.utils.showToast
import app.dating.appsuccessor.viewModel.DobViewModel
import app.dating.appsuccessor.viewModel.NameViewModel
import java.util.Calendar
import javax.xml.datatype.DatatypeConstants.MONTHS

class DOBActivity : AppCompatActivity() {
    lateinit var ui: ActivityDobactivityBinding
    private val viewModel: DobViewModel by viewModels()
    private var userDateOfBirth: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = ActivityDobactivityBinding.inflate(layoutInflater)
        setContentView(ui.root)

        setStatusBarColor(Color.parseColor("#F7F9F7"))

        ui.apply {
            calender.clickTo {
                showDatePicker()
            }
            userDob.clickTo {
                showDatePicker()
            }
            next.clickTo {
                if (userDateOfBirth.isNullOrEmpty()) {
                    showToast(this@DOBActivity, "please select your DOB")
                } else {
                    Config.showDialog(this@DOBActivity)
                    viewModel.updateUserDob(dob = userDateOfBirth!!)
                    viewModel.updateDobSuccess.observe(this@DOBActivity) { success ->
                        if (success) {
                            startActivity(Intent(this@DOBActivity, GenderActivity::class.java))
                            Config.hideDialog()
                        } else {
                            Toast.makeText(
                                this@DOBActivity, "Something went wrong", Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
            back.clickTo {
                onBackPressed()
            }

            checkForActiveButtonDOB()
        }


    }

    private fun checkForActiveButtonDOB() {
        val editText = ui.userDob
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
                if (enteredText.length >= 8) {
                    // Enable the EditText and button for interaction
                    button.setTextColor(activeTextColor)
                    button.background = activeBackground
                    button.setTextAppearance(this@DOBActivity, activeButtonStyle)
                } else {
                    // Disable the EditText and button
                    button.setTextColor(inactiveTextColor)
                    button.background = inactiveBackground
                    button.setTextAppearance(this@DOBActivity, inactiveButtonStyle)
                }
            }
        })
    }

    private fun showDatePicker() {
        val c = Calendar.getInstance()
        c.add(Calendar.YEAR, -18) // Subtract 18 years from the current date

        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(
            this@DOBActivity,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val dob = "$dayOfMonth/${monthOfYear + 1}/$year"
                ui.userDob.setText(dob)
                userDateOfBirth = dob
            },
            year,
            month,
            day
        )
        dpd.datePicker.maxDate = c.timeInMillis // Set the maximum date to 18 years ago

        dpd.show()
    }

}