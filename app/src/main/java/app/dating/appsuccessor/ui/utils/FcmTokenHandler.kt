import android.content.Context
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging

class FcmTokenHandler(private val context: Context) {

    fun updateFcmToken() {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }

                val token = task.result
                val userPhoneNumber = FirebaseAuth.getInstance().currentUser!!.phoneNumber!!

                val userRef = FirebaseDatabase.getInstance().getReference("users")
                    .child(userPhoneNumber)

                userRef.child("fcmToken").setValue(token)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "FCM registration token updated successfully")
                        } else {
                            Log.e(TAG, "Error updating FCM registration token", task.exception)
                        }
                    }
            })
    }

    companion object {
        private const val TAG = "FcmTokenHandler"
    }
}
