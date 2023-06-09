package app.dating.appsuccessor.ui.activity.auth

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import app.dating.appsuccessor.R
import app.dating.appsuccessor.databinding.ActivityAccurateTestBinding
import app.dating.appsuccessor.model.Question
import app.dating.appsuccessor.ui.adapter.QuestionAdapter
import app.dating.appsuccessor.ui.utils.setStatusBarColor

class AccurateTestActivity : AppCompatActivity() {
    lateinit var ui: ActivityAccurateTestBinding
    private lateinit var questionAdapter: QuestionAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = ActivityAccurateTestBinding.inflate(layoutInflater)
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
            ),
            Question(
                "How would you detect if someone is lying?\uD83D\uDC40",
                "Observe their eye movements and body language.",
                "You’ll KNOW if they are lying."
            ),
            Question(
                "When buying a new phone \uD83D\uDCF1, do you...",
                "Research extensively and get most practical one.",
                "Buy the one you want, even if it is out of budget."
            ),
            Question(
                "How do you ⚡‘Recharge’ yourself?",
                "Spend time alone with yourself.",
                "Chill out with family or friends."
            ),
            Question(
                "What is your view on rules?",
                "To be followed.\uD83D\uDE07",
                "To be ignored. \uD83D\uDE08"
            ),
            Question(
                "Someone ate your sandwich at work. Find the culprit.\uD83D\uDD75",
                "You already know it’s THAT colleague.",
                "Check CCTV, contact admin, track movements"
            ),
            Question(
                "How do you share your opinion \uD83D\uDCAC?",
                "Be direct, honest, and blunt.",
                "Be thoughtful and mindful of your words."
            ),
            Question(
                "What is the state of your room \uD83C\uDFE1?",
                "It's clean and organized.",
                "It's messy, but I can still find everything I need."
            ),
            Question(
                "If forced to pick, which one would you choose\uD83D\uDE03?",
                "Many ‘Good’ friends ",
                "One ‘Great’ friend "
            ),
            Question(
                "How will you piece together a puzzle\uD83E\uDDE9?",
                "Start at the corner and build your way in.",
                "Start wherever."
            ),
            Question(
                "In a social setting,  do you\uD83D\uDDE3...",
                "Initiate conversation. ",
                "Keep to your self"
            ),
            Question(
                "How do you \uD83D\uDDA5 organize your work?",
                " Start early and plan meticulously.",
                "Wait until the last possible moment and rely on adrenaline."
            ),
            Question("Which role do you prefer \uD83D\uDE0C?", "The Strategist ", "The therapist"),
        )

        // Create an instance of the QuestionAdapter and set it with the ViewPager
        questionAdapter = QuestionAdapter(questions, ui.viewPager)
        ui.viewPager.adapter = questionAdapter
    }
}