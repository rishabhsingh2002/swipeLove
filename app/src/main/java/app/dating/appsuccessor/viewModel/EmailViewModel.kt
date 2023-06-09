package app.dating.appsuccessor.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.dating.appsuccessor.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EmailViewModel : ViewModel() {

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val database by lazy { FirebaseDatabase.getInstance() }
    private val _updateEmailSuccess = MutableLiveData<Boolean>()
    val updateEmailSuccess: LiveData<Boolean>
        get() = _updateEmailSuccess

    fun updateUserEmail(userEmail: String) {
        val currentUserNumber = auth.currentUser!!.phoneNumber!!
        val currentUserRef = database.getReference("users").child(currentUserNumber)

        currentUserRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                currentUserRef.child("email").setValue(userEmail).addOnCompleteListener {
                    if (it.isSuccessful) {
//                        _updateEmailSuccess.value = true // set success value
                        updatePhone()
                    } else {
//                        _updateEmailSuccess.value = false // set failure value
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Error getting user model from database
            }
        })
    }

    private fun updatePhone() {
        val currentUserNumber = auth.currentUser!!.phoneNumber!!
        val currentUserRef = database.getReference("users").child(currentUserNumber)
        currentUserRef.child("number").setValue(currentUserNumber).addOnCompleteListener {
            if (it.isSuccessful) {
                _updateEmailSuccess.value = true // set success value
            } else {
                _updateEmailSuccess.value = false // set failure value
            }
        }
    }
}
