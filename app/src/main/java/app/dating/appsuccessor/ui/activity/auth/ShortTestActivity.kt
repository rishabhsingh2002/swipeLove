package app.dating.appsuccessor.ui.activity.auth

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import app.dating.appsuccessor.R
import app.dating.appsuccessor.databinding.ActivityShortTestBinding
import app.dating.appsuccessor.model.Question
import app.dating.appsuccessor.ui.adapter.QuestionAdapter
import app.dating.appsuccessor.ui.utils.setStatusBarColor

class ShortTestActivity : AppCompatActivity() {
    lateinit var ui: ActivityShortTestBinding
    private lateinit var questionAdapter: QuestionAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = ActivityShortTestBinding.inflate(layoutInflater)
        setContentView(ui.root)
        setStatusBarColor(Color.parseColor("#F7F9F7"))


        // Create a list of questions (replace with your actual questions)
        val questions = listOf(
            Question(
                "Which emojis describe your ideal Sunday?",
                "\uD83D\uDD7A \uD83D\uDC83 \uD83C\uDF89",
                "\uD83D\uDE0C \uD83D\uDECC \uD83C\uDFE1"
            ),
            Question(
                "What kind of business ideas \uD83D\uDCBC would you prefer?",
                "Practical and sensible.",
                "Futuristic and innovative."
            ),
            Question(
                "You get a bonus of ₹10,000, what would you do?",
                "Put it in savings for future? \uD83D\uDCB8",
                "Use it to treat yourself, because you deserve it!\uD83D\uDE0A"
            ),
            Question(
                "While grocery shopping \uD83D\uDED2 , do you...",
                "Stick to your list?",
                "Grab whatever you like."
            ),
            Question(
                "How would you plan a party\uD83C\uDF89?",
                "Plan, book and schedule everything. ",
                "Text some friends and YOLO."
            ),
            Question(
                "While watching a movie \uD83C\uDFA5, do you...",
                "Find plot holes.",
                "Enjoy the movie"
            ),
            Question(
                "Your ideal partner would be...\uD83D\uDC91",
                "Intelligent, rational, and logical thinker. ",
                "Kind, empathetic, and a good listener."
            ),
            Question(
                "While meeting \uD83E\uDD1D someone for the first time, are you...",
                "Excited by the opportunity to network.",
                "Find it draining and prefer to avoid it."
            )
        )

        // Create an instance of the QuestionAdapter and set it with the ViewPager
        questionAdapter = QuestionAdapter(questions, ui.viewPager)
        ui.viewPager.adapter = questionAdapter
    }
}