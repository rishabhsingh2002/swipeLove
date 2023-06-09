package app.dating.appsuccessor.ui.activity.auth

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.MutableLiveData
import app.dating.appsuccessor.R
import app.dating.appsuccessor.databinding.ActivityLocationRangeBinding
import app.dating.appsuccessor.ui.utils.Config
import app.dating.appsuccessor.ui.utils.clickTo
import app.dating.appsuccessor.ui.utils.setStatusBarColor
import app.dating.appsuccessor.viewModel.LikeToDateViewModel
import app.dating.appsuccessor.viewModel.LocationRangeViewModel
import com.xw.repo.BubbleSeekBar

class LocationRangeActivity : AppCompatActivity() {
    lateinit var ui: ActivityLocationRangeBinding
    lateinit var locationRange: MutableLiveData<Int>
    private val viewModel: LocationRangeViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = ActivityLocationRangeBinding.inflate(layoutInflater)
        setContentView(ui.root)

        setStatusBarColor(Color.parseColor("#F7F9F7"))

        locationRange = MutableLiveData(2)

        ui.apply {
            seekBar.onProgressChangedListener = object : BubbleSeekBar.OnProgressChangedListener {
                override fun onProgressChanged(
                    bubbleSeekBar: BubbleSeekBar?,
                    progress: Int,
                    progressFloat: Float,
                    fromUser: Boolean
                ) {
                    locationRange.postValue(progress)
                    ui.progress.text = locationRange.value.toString() + " Kms"
                }

                override fun getProgressOnActionUp(
                    bubbleSeekBar: BubbleSeekBar?,
                    progress: Int,
                    progressFloat: Float
                ) {
                }

                override fun getProgressOnFinally(
                    bubbleSeekBar: BubbleSeekBar?,
                    progress: Int,
                    progressFloat: Float,
                    fromUser: Boolean
                ) {
                }
            }
            next.clickTo {
                Config.showDialog(this@LocationRangeActivity)
                viewModel.updateUserLocationRange(locationRange = locationRange.value.toString())
                viewModel.updateLocationRangeSuccess.observe(this@LocationRangeActivity) { success ->
                    if (success) {
                        startActivity(
                            Intent(
                                this@LocationRangeActivity, InfoThreeActivity::class.java
                            )
                        )
                        Config.hideDialog()
                    } else {
                        Toast.makeText(
                            this@LocationRangeActivity, "Something went wrong", Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            back.clickTo {
                onBackPressed()
            }

        }

    }
}