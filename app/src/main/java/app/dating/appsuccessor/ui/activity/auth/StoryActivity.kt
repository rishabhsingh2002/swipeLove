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
import app.dating.appsuccessor.databinding.ActivityStoryBinding
import app.dating.appsuccessor.ui.utils.Config
import app.dating.appsuccessor.ui.utils.clickTo
import app.dating.appsuccessor.ui.utils.setStatusBarColor
import app.dating.appsuccessor.viewModel.GenderViewModel
import app.dating.appsuccessor.viewModel.StoryViewModel

class StoryActivity : AppCompatActivity() {
    lateinit var ui: ActivityStoryBinding
    private val viewModel: StoryViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = ActivityStoryBinding.inflate(layoutInflater)
        setContentView(ui.root)

        setStatusBarColor(Color.parseColor("#F7F9F7"))

        ui.apply {
            back.clickTo {
                onBackPressed()
            }
            next.clickTo {
                if (story.text.length < 2) {
                    Toast.makeText(
                        this@StoryActivity,
                        "Please enter your story",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Config.showDialog(this@StoryActivity)
                    viewModel.updateUserStory(story = story.text.toString())
                    viewModel.updateStorySuccess.observe(this@StoryActivity) { success ->
                        if (success) {
                            startActivity(Intent(this@StoryActivity, InfoActivity::class.java))
                            Config.hideDialog()
                        } else {
                            Toast.makeText(
                                this@StoryActivity, "Something went wrong", Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }

        checkForActiveButtonStory()
    }

    private fun checkForActiveButtonStory() {
        val editText = ui.story
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
                    button.setTextAppearance(this@StoryActivity, activeButtonStyle)
                } else {
                    // Disable the EditText and button
                    button.setTextColor(inactiveTextColor)
                    button.background = inactiveBackground
                    button.setTextAppearance(this@StoryActivity, inactiveButtonStyle)
                }
            }
        })
    }

}