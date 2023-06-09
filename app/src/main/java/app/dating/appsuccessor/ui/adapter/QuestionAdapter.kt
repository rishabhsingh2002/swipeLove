package app.dating.appsuccessor.ui.adapter

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.os.Handler
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Switch
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import app.dating.appsuccessor.R
import app.dating.appsuccessor.model.Question
import app.dating.appsuccessor.ui.activity.auth.LoadingQuestionsActivity
import app.dating.appsuccessor.ui.utils.clickTo
import kotlin.coroutines.coroutineContext

class QuestionAdapter(private val questions: List<Question>, private val viewPager: ViewPager) :
    PagerAdapter() {

    private val selectedAnswers = HashMap<Int, String>()

    override fun getCount(): Int {
        return questions.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val question = questions[position]
        val view = LayoutInflater.from(container.context)
            .inflate(R.layout.item_test_questions, container, false)

        val questionTextView = view.findViewById<TextView>(R.id.question)
        val answerSwitch1 = view.findViewById<RadioButton>(R.id.optionOne)
        val answerSwitch2 = view.findViewById<RadioButton>(R.id.optionTwo)
        val progress = view.findViewById<TextView>(R.id.progressQuestions)
        val back = view.findViewById<TextView>(R.id.backFromQuestions)
        val tvQuestionOne = view.findViewById<TextView>(R.id.optionOneText)
        val tvQuestionTwo = view.findViewById<TextView>(R.id.optionTwoText)

        questionTextView.text = question.question
        tvQuestionOne.text = question.answer1
        tvQuestionTwo.text = question.answer2

        progress.text = "(${position + 1}/${questions.size})"

        answerSwitch1.isChecked = selectedAnswers.containsValue(question.answer1)
        answerSwitch2.isChecked = selectedAnswers.containsValue(question.answer2)

        if (position >= 0) {
            answerSwitch1.isChecked = false
            answerSwitch2.isChecked = false
        }

        answerSwitch1.setOnClickListener {
            val answer = question.answer1
            if (answerSwitch1.isChecked) {
                answerSwitch2.isChecked = false
                selectedAnswers[position] = answer
                navigateToNextPosition(position)
            } else {
                selectedAnswers.remove(position)
            }
        }

        answerSwitch2.setOnClickListener {
            val answer = question.answer2
            if (answerSwitch2.isChecked) {
                answerSwitch1.isChecked = false
                selectedAnswers[position] = answer
                navigateToNextPosition(position)
            } else {
                selectedAnswers.remove(position)
            }
        }

        back.clickTo {
            if (position > 0) {
                viewPager.currentItem = position - 1
            }
        }

        container.addView(view)
        return view
    }


    private fun navigateToNextPosition(currentPosition: Int) {
        val delayMillis = 1000L // Delay in milliseconds (2 seconds)

        if (currentPosition < questions.size - 1) {
            Handler().postDelayed({
                viewPager.currentItem = currentPosition + 1
            }, delayMillis)
        } else {
            val context = viewPager.context
            if (context is Activity) {
                val selectedAnswersList = ArrayList<String>(selectedAnswers.values)
                val intent = Intent(context, LoadingQuestionsActivity::class.java)
                intent.putStringArrayListExtra("selectedAnswers", selectedAnswersList)
                context.startActivity(intent)
                context.finish() // Optional: Finish the current activity if needed
            }
        }
    }


    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as View)
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view == obj
    }

    fun getSelectedAnswers(): HashMap<Int, String> {
        return selectedAnswers
    }
}


