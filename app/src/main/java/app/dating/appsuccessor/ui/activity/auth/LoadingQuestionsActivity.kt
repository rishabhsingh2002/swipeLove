package app.dating.appsuccessor.ui.activity.auth

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import app.dating.appsuccessor.databinding.ActivityLoadingQuestionsBinding


class LoadingQuestionsActivity : AppCompatActivity() {
    lateinit var ui: ActivityLoadingQuestionsBinding
    private var timer: CountDownTimer? = null
    private var counter = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = ActivityLoadingQuestionsBinding.inflate(layoutInflater)
        setContentView(ui.root)

        val selectedAnswers = intent.getStringArrayListExtra("selectedAnswers")
        if (selectedAnswers != null) {
            Log.d("Datalkdfjaldf", selectedAnswers.toString())
        }


        // Set initial text and image
        ui.apply {
            title.text = "Analysing your\nResponses..."
            progressBar.visibility = View.VISIBLE
            checkImage.visibility = View.GONE
        }

        // Start the timer
        startTimer()
    }

    private fun startTimer() {
        timer = object : CountDownTimer(2000, 1000) {
            override fun onTick(millisUntilFinished: Long) {}

            override fun onFinish() {
                // Update text and image after 2 seconds
                if (counter == 0) {
                    // Change UI after 2 seconds (first iteration)
                    ui.apply {
                        title.text = "Analysing your\nResponses..."
                        progressBar.visibility = View.VISIBLE
                        checkImage.visibility = View.GONE
                    }
                    counter++
                    startTimer()
                } else if (counter == 1) {
                    // Change UI after 2 seconds (second iteration)
                    ui.apply {
                        title.text = "Compiling Results..."
                        checkImage.visibility = View.GONE
                        progressBar.visibility = View.VISIBLE
                    }
                    counter++
                    startTimer()
                } else {
                    // Change UI after 1 second and navigate to the next activity
                    ui.apply {
                        title.text = "Quiz Complete !"
                        progressBar.visibility = View.GONE
                        checkImage.visibility = View.VISIBLE
                    }
                    Handler(Looper.getMainLooper()).postDelayed({
                        navigateToNextActivity()
                    }, 1000)
                }
            }
        }.start()
    }


    private fun navigateToNextActivity() {
        // Navigate to the next activity
        val intent = Intent(this, InfoFourActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Cancel the timer to avoid memory leaks
        timer?.cancel()
    }
}