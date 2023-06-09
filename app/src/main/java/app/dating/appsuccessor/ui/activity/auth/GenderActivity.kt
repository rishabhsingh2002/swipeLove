package app.dating.appsuccessor.ui.activity.auth

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import app.dating.appsuccessor.R
import app.dating.appsuccessor.databinding.ActivityGenderBinding
import app.dating.appsuccessor.databinding.GenderBottomDialogBinding
import app.dating.appsuccessor.ui.utils.Config
import app.dating.appsuccessor.ui.utils.clickTo
import app.dating.appsuccessor.ui.utils.setStatusBarColor
import app.dating.appsuccessor.ui.utils.showToast
import app.dating.appsuccessor.viewModel.GenderViewModel


class GenderActivity : AppCompatActivity() {
    private lateinit var ui: ActivityGenderBinding
    private val viewModel: GenderViewModel by viewModels()
    private var selectedGender: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = ActivityGenderBinding.inflate(layoutInflater)
        setContentView(ui.root)

        setStatusBarColor(Color.parseColor("#F7F9F7"))

        ui.apply {
            male.clickTo {
                male.background = resources.getDrawable(R.drawable.selected_gender_bg)
                female.background = resources.getDrawable(R.drawable.not_selected_gender_bg)
                selectedGender = "male"
                enableButton()
            }
            female.clickTo {
                female.background = resources.getDrawable(R.drawable.selected_gender_bg)
                male.background = resources.getDrawable(R.drawable.not_selected_gender_bg)
                selectedGender = "female"
                enableButton()
            }
            next.clickTo {
                if (selectedGender.isNullOrEmpty()) {
                    showToast(this@GenderActivity, "Please select your gender")
                } else {
                    Config.showDialog(this@GenderActivity)
                    viewModel.updateUserGender(gender = selectedGender!!)
                    viewModel.updateGenderSuccess.observe(this@GenderActivity) { success ->
                        if (success) {
                            startActivity(
                                Intent(this@GenderActivity, ProfileImagesActivity::class.java)
                            )
                            Config.hideDialog()
                        } else {
                            Toast.makeText(
                                this@GenderActivity, "Something went wrong", Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
            more.clickTo {
                showBottomSheetDialog()
            }
            back.clickTo {
                onBackPressed()
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
        button.setTextAppearance(this@GenderActivity, activeButtonStyle)
    }

    private fun showBottomSheetDialog() {
        val bottomSheet = Dialog(this@GenderActivity)
        bottomSheet.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val bindingSheet = GenderBottomDialogBinding.inflate(layoutInflater)
        bottomSheet.setContentView(bindingSheet.root)

        bindingSheet.exitDialog.clickTo {
            bottomSheet.dismiss()
        }
        bindingSheet.groupRadio.clearCheck()
        bindingSheet.groupRadio.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.male -> {
                    selectedGender = "male"
                    ui.genderSelectedOnBottomSheet.text = "Male"
                    enableButton()
                }

                R.id.female -> {
                    selectedGender = "female"
                    ui.genderSelectedOnBottomSheet.text = "Female"
                    enableButton()
                }

                R.id.transgenderMan -> {
                    selectedGender = "transgenderMan"
                    ui.genderSelectedOnBottomSheet.text = "Transgender Man"
                    enableButton()
                }

                R.id.transgenderWomen -> {
                    selectedGender = "transgenderWomen"
                    ui.genderSelectedOnBottomSheet.text = "Transgender Women"
                    enableButton()
                }

                R.id.nonBinary -> {
                    selectedGender = "nonBinary"
                    ui.genderSelectedOnBottomSheet.text = "Non-Binary"
                    enableButton()
                }

                R.id.others -> {
                    val gender = bindingSheet.genderInput.text.toString()
                    if (!gender.isNullOrEmpty()) {
                        selectedGender = gender
                        ui.genderSelectedOnBottomSheet.text = gender
                        enableButton()
                    } else {
                        Toast.makeText(
                            this@GenderActivity, "Please select your gender", Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            }
        })


        bottomSheet.setCanceledOnTouchOutside(true)
        bottomSheet.setCancelable(true)
        bottomSheet.show()
        bottomSheet.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        bottomSheet.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        bottomSheet.window?.attributes?.windowAnimations = R.style.DialogAnimation
        bottomSheet.window?.setGravity(Gravity.BOTTOM)
    }
}