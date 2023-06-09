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


class GenderViewModel : ViewModel() {

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val database by lazy { FirebaseDatabase.getInstance() }

    private val _updateGenderSuccess = MutableLiveData<Boolean>()
    val updateGenderSuccess: LiveData<Boolean>
        get() = _updateGenderSuccess

    fun updateUserGender(gender: String) {
        val currentUserNumber = auth.currentUser!!.phoneNumber!!
        val currentUserRef = database.getReference("users").child(currentUserNumber)

        currentUserRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val currentUserModel = snapshot.getValue(UserModel::class.java)
                val updatedUserModel = currentUserModel?.copy(gender = gender)

                currentUserRef.setValue(updatedUserModel).addOnCompleteListener {
                    if (it.isSuccessful) {
                        //  updated successfully
                        _updateGenderSuccess.value = true
                    } else {
                        // Error updating
                        _updateGenderSuccess.value = false
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Error getting user model from database
            }
        })
    }
}