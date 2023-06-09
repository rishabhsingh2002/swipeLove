package app.dating.appsuccessor.viewModel

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.dating.appsuccessor.ui.utils.showToast
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit

class LoginViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private lateinit var storedVerificationId: String
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken

    private val _showLoading = MutableLiveData<Boolean>()
    val showLoading: LiveData<Boolean>
        get() = _showLoading

    private val _showVerification = MutableLiveData<Boolean>()
    val showVerification: LiveData<Boolean>
        get() = _showVerification

    private val _navigateToHome = MutableLiveData<Boolean>()
    val navigateToHome: LiveData<Boolean>
        get() = _navigateToHome

    private val _showOTPError = MutableLiveData<Boolean>()
    val showOTPError: LiveData<Boolean>
        get() = _showOTPError

    fun sendOtp(phoneNumber: String, activity: Activity) {
        _showLoading.value = true
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    signInWithPhoneAuthCredential(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
//                    Toast.makeText(getApplication(), "Verification Failed", Toast.LENGTH_SHORT)
//                        .show()
                    _showLoading.value = false
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    storedVerificationId = verificationId
                    resendToken = token
                    _showLoading.value = false
                    _showVerification.value = true
                }
            })
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun resendOtp(activity: Activity, phoneNumber: String) {
        _showLoading.value = true
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber,
            60,
            TimeUnit.SECONDS,
            activity,
            object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                    _showLoading.value = false
                }

                override fun onVerificationFailed(p0: FirebaseException) {
                    _showLoading.value = false
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    storedVerificationId = verificationId
                    resendToken = token
                    _showLoading.value = false
                    _showVerification.value = true
                }
            },
            resendToken
        )
    }


    fun verifyOtp(otp: String) {
        _showLoading.value = true
        val credential = PhoneAuthProvider.getCredential(storedVerificationId, otp)
        signInWithPhoneAuthCredential(credential)
    }

    fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _navigateToHome.value = true
                } else {
                    _showOTPError.value = true
//                    Toast.makeText(getApplication(), "Verification Failed", Toast.LENGTH_SHORT)
//                        .show()
                    _showLoading.value = false
                }
            }
    }
}

