package app.dating.appsuccessor.ui.activity.auth

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import app.dating.appsuccessor.R
import app.dating.appsuccessor.databinding.ActivityLikeToDateBinding
import app.dating.appsuccessor.ui.utils.Config
import app.dating.appsuccessor.ui.utils.clickTo
import app.dating.appsuccessor.ui.utils.setStatusBarColor
import app.dating.appsuccessor.viewModel.LikeToDateViewModel
import app.dating.appsuccessor.viewModel.LookingForViewModel

class LikeToDateActivity : AppCompatActivity() {
    lateinit var ui: ActivityLikeToDateBinding
    var likeToDate = MutableLiveData<String?>(null)
    private val viewModel: LikeToDateViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = ActivityLikeToDateBinding.inflate(layoutInflater)
        setContentView(ui.root)

        setStatusBarColor(Color.parseColor("#F7F9F7"))

        checkBoxSetUp()

        likeToDate.observe(this, {
            if (it.isNullOrEmpty()) {

            } else {
                enableButton()
            }
        })

        ui.apply {
            next.clickTo {
                if (likeToDate.value.isNullOrEmpty()) {
                    Toast.makeText(
                        this@LikeToDateActivity,
                        "Please select you like to date",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Config.showDialog(this@LikeToDateActivity)
                    viewModel.updateUserLikeToDate(likeToDate = likeToDate.value.toString())
                    viewModel.updateLikeToDateSuccess.observe(this@LikeToDateActivity) { success ->
                        if (success) {
                            startActivity(
                                Intent(this@LikeToDateActivity, LocationRangeActivity::class.java)
                            )
                            Config.hideDialog()
                        } else {
                            Toast.makeText(
                                this@LikeToDateActivity, "Something went wrong", Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

            }
            back.clickTo {
                onBackPressed()
            }
        }
    }

    private fun checkBoxSetUp() {
        ui.apply {
            man.clickTo {
                enableButton()
                woman.isChecked = false
                transgenderWoman.isChecked = false
                transgenderMan.isChecked = false
                nonBinary.isChecked = false
                other.isChecked = false
                likeToDate.value = "man"
            }
            woman.clickTo {
                enableButton()
                man.isChecked = false
                transgenderWoman.isChecked = false
                transgenderMan.isChecked = false
                nonBinary.isChecked = false
                other.isChecked = false
                likeToDate.value = "woman"
            }
            transgenderWoman.clickTo {
                enableButton()
                woman.isChecked = false
                man.isChecked = false
                transgenderMan.isChecked = false
                nonBinary.isChecked = false
                other.isChecked = false
                likeToDate.value = "transgenderWoman"
            }
            transgenderMan.clickTo {
                enableButton()
                woman.isChecked = false
                transgenderWoman.isChecked = false
                man.isChecked = false
                nonBinary.isChecked = false
                other.isChecked = false
                likeToDate.value = "transgenderMan"
            }
            nonBinary.clickTo {
                enableButton()
                woman.isChecked = false
                transgenderWoman.isChecked = false
                transgenderMan.isChecked = false
                man.isChecked = false
                other.isChecked = false
                likeToDate.value = "nonBinary"
            }
            other.clickTo {
                enableButton()
                woman.isChecked = false
                transgenderWoman.isChecked = false
                transgenderMan.isChecked = false
                nonBinary.isChecked = false
                man.isChecked = false
            }
            if (man.isChecked) {
                enableButton()
                woman.isChecked = false
                transgenderWoman.isChecked = false
                transgenderMan.isChecked = false
                nonBinary.isChecked = false
                other.isChecked = false
                likeToDate.value = "man"

            } else if (woman.isChecked) {
                enableButton()
                man.isChecked = false
                transgenderWoman.isChecked = false
                transgenderMan.isChecked = false
                nonBinary.isChecked = false
                other.isChecked = false
                likeToDate.value = "woman"

            } else if (transgenderMan.isChecked) {
                enableButton()
                woman.isChecked = false
                transgenderWoman.isChecked = false
                man.isChecked = false
                nonBinary.isChecked = false
                other.isChecked = false
                likeToDate.value = "transgenderMan"

            } else if (transgenderWoman.isChecked) {
                enableButton()
                woman.isChecked = false
                man.isChecked = false
                transgenderMan.isChecked = false
                nonBinary.isChecked = false
                other.isChecked = false
                likeToDate.value = "transgenderWoman"

            } else if (nonBinary.isChecked) {
                enableButton()
                woman.isChecked = false
                transgenderWoman.isChecked = false
                transgenderMan.isChecked = false
                man.isChecked = false
                other.isChecked = false
                likeToDate.value = "nonBinary"

            } else if (other.isChecked) {
                enableButton()
                woman.isChecked = false
                transgenderWoman.isChecked = false
                transgenderMan.isChecked = false
                nonBinary.isChecked = false
                man.isChecked = false
                if (enteredGender.text.isNullOrEmpty()) {
                    Toast.makeText(
                        this@LikeToDateActivity,
                        "Please enter valid gender",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    likeToDate.value = enteredGender.text.toString()
                }

            }
        }
    }

    private fun enableButton() {
        val activeButtonStyle = R.style.ButtonActive
        val attrs = intArrayOf(android.R.attr.textColor, android.R.attr.background)
        val typedArray = obtainStyledAttributes(activeButtonStyle, attrs)
        val activeTextColor = typedArray.getColor(0, 0)
        val activeBackground = typedArray.getDrawable(1)
        val button = ui.next
        button.setTextColor(activeTextColor)
        button.background = activeBackground
        button.setTextAppearance(this@LikeToDateActivity, activeButtonStyle)
    }
}