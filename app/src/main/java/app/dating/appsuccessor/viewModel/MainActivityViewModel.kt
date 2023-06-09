package app.dating.appsuccessor.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.dating.appsuccessor.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivityViewModel : ViewModel() {

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val database by lazy { FirebaseDatabase.getInstance() }

    private val _updateFcmTokenSuccess = MutableLiveData<Boolean>()
    val updateFcmTokenSuccess: LiveData<Boolean>
        get() = _updateFcmTokenSuccess

    fun updateUserFCMToken(fcmToken: String) {
        val currentUserNumber = auth.currentUser!!.phoneNumber!!
        val currentUserRef = database.getReference("users").child(currentUserNumber)

        currentUserRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val currentUserModel = snapshot.getValue(UserModel::class.java)
                val updatedUserModel = currentUserModel?.copy(fcmToken = fcmToken)
                currentUserRef.setValue(updatedUserModel).addOnCompleteListener {
                    if (it.isSuccessful) {
                        //  updated successfully
                        _updateFcmTokenSuccess.value = true
                    } else {
                        // Error updating
                        _updateFcmTokenSuccess.value = false
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Error getting user model from database
            }
        })
    }
}