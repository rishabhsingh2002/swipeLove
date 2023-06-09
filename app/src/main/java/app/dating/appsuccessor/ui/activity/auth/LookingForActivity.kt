package app.dating.appsuccessor.ui.activity.auth

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.res.ResourcesCompat
import app.dating.appsuccessor.R
import app.dating.appsuccessor.databinding.ActivityLookingForBinding
import app.dating.appsuccessor.ui.utils.Config
import app.dating.appsuccessor.ui.utils.clickTo
import app.dating.appsuccessor.ui.utils.setStatusBarColor
import app.dating.appsuccessor.viewModel.LookingForViewModel
import app.dating.appsuccessor.viewModel.StoryViewModel

class LookingForActivity : AppCompatActivity() {
    lateinit var ui: ActivityLookingForBinding
    private val viewModel: LookingForViewModel by viewModels()
    var lookingFor: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = ActivityLookingForBinding.inflate(layoutInflater)
        setContentView(ui.root)

        setStatusBarColor(Color.parseColor("#F7F9F7"))

        ui.apply {
            casual.clickTo {
                enableButton()
                casual.background = resources.getDrawable(R.drawable.button_bg)
                casual.setTextColor(resources.getColor(R.color.primary_white))
                seriousRelationship.background =
                    resources.getDrawable(R.drawable.button_looking_for_bg)
                seriousRelationship.setTextColor(resources.getColor(R.color.color_primary))
                goodConversation.background =
                    resources.getDrawable(R.drawable.button_looking_for_bg)
                goodConversation.setTextColor(resources.getColor(R.color.color_primary))
                iDontKnowYet.background = resources.getDrawable(R.drawable.button_looking_for_bg)
                iDontKnowYet.setTextColor(resources.getColor(R.color.color_primary))
                lookingFor = "casual dating"
            }
            seriousRelationship.clickTo {
                enableButton()
                seriousRelationship.background = resources.getDrawable(R.drawable.button_bg)
                seriousRelationship.setTextColor(resources.getColor(R.color.primary_white))
                casual.background =
                    resources.getDrawable(R.drawable.button_looking_for_bg)
                casual.setTextColor(resources.getColor(R.color.color_primary))
                goodConversation.background =
                    resources.getDrawable(R.drawable.button_looking_for_bg)
                goodConversation.setTextColor(resources.getColor(R.color.color_primary))
                iDontKnowYet.background = resources.getDrawable(R.drawable.button_looking_for_bg)
                iDontKnowYet.setTextColor(resources.getColor(R.color.color_primary))
                lookingFor = "serious relationship"
            }
            goodConversation.clickTo {
                enableButton()
                goodConversation.background = resources.getDrawable(R.drawable.button_bg)
                goodConversation.setTextColor(resources.getColor(R.color.primary_white))
                casual.background =
                    resources.getDrawable(R.drawable.button_looking_for_bg)
                casual.setTextColor(resources.getColor(R.color.color_primary))
                seriousRelationship.background =
                    resources.getDrawable(R.drawable.button_looking_for_bg)
                seriousRelationship.setTextColor(resources.getColor(R.color.color_primary))
                iDontKnowYet.background = resources.getDrawable(R.drawable.button_looking_for_bg)
                iDontKnowYet.setTextColor(resources.getColor(R.color.color_primary))
                lookingFor = "good conversation"
            }
            iDontKnowYet.clickTo {
                enableButton()
                iDontKnowYet.background =
                    resources.getDrawable(R.drawable.button_bg)
                iDontKnowYet.setTextColor(resources.getColor(R.color.primary_white))
                casual.background =
                    resources.getDrawable(R.drawable.button_looking_for_bg)
                casual.setTextColor(resources.getColor(R.color.color_primary))
                seriousRelationship.background =
                    resources.getDrawable(R.drawable.button_looking_for_bg)
                seriousRelationship.setTextColor(resources.getColor(R.color.color_primary))
                goodConversation.background =
                    resources.getDrawable(R.drawable.button_looking_for_bg)
                goodConversation.setTextColor(resources.getColor(R.color.color_primary))
                lookingFor = "i don't know"
            }
            next.clickTo {
                if (lookingFor.isNullOrEmpty()) {
                    Toast.makeText(
                        this@LookingForActivity,
                        "Please select the choice",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Config.showDialog(this@LookingForActivity)
                    viewModel.updateUserLookingFor(lookingFor = lookingFor.toString())
                    viewModel.updateLookingForSuccess.observe(this@LookingForActivity) { success ->
                        if (success) {
                            startActivity(
                                Intent(this@LookingForActivity, LikeToDateActivity::class.java)
                            )
                            Config.hideDialog()
                        } else {
                            Toast.makeText(
                                this@LookingForActivity, "Something went wrong", Toast.LENGTH_SHORT
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

    private fun enableButton() {
        val activeButtonStyle = R.style.ButtonActive
        val attrs = intArrayOf(android.R.attr.textColor, android.R.attr.background)
        val typedArray = obtainStyledAttributes(activeButtonStyle, attrs)
        val activeTextColor = typedArray.getColor(0, 0)
        val activeBackground = typedArray.getDrawable(1)
        val button = ui.next
        button.setTextColor(activeTextColor)
        button.background = activeBackground
        button.setTextAppearance(this@LookingForActivity, activeButtonStyle)
    }
}