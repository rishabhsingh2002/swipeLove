package app.dating.appsuccessor.ui.activity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import app.dating.appsuccessor.R
import app.dating.appsuccessor.databinding.ActivityEditProfileBinding
import app.dating.appsuccessor.model.UserModel
import app.dating.appsuccessor.ui.utils.Config
import app.dating.appsuccessor.ui.utils.clickTo
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage

class EditProfileActivity : AppCompatActivity() {
    lateinit var ui: ActivityEditProfileBinding

    private var imageUri: Uri? = null
    private var image: String? = null

    private val selectImage = registerForActivityResult(ActivityResultContracts.GetContent()) {
        imageUri = it
        ui.userImage.setImageURI(imageUri)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(ui.root)

        val data = intent.getSerializableExtra("userModel") as UserModel

        ui.apply {
            ui.userName.setText(data.name.toString())
            ui.userNumber.setText(data.number.toString())
            ui.userCity.setText(data.location.toString())
            ui.userEmail.setText(data.email.toString())

//            Glide.with(this@EditProfileActivity).load(data.image.get(0)).placeholder(R.drawable.ic_user)
//                .into(ui.userImage)
//            image = data.image

            userImage.clickTo {
                selectImage.launch("image/*")
            }
            save.clickTo {
                validateData(data)
            }
        }
    }

    private fun validateData(data: UserModel) {
        if (ui.userName.text.toString().isEmpty()) {
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show()
        } else if (ui.userEmail.text.toString().isEmpty()) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
        } else if (ui.userCity.text.toString().isEmpty()) {
            Toast.makeText(this, "Please enter your city", Toast.LENGTH_SHORT).show()
        } else {
            if (imageUri == null) {
                storeData(data.images.toString())
            } else {
                uploadImage(data)
            }
        }
    }

    private fun uploadImage(data: UserModel) {
        Config.showDialog(this)

        val storageRef = FirebaseStorage.getInstance().getReference("profile")
            .child(FirebaseAuth.getInstance().currentUser!!.uid).child("profile.jpg")

        val uploadTask = storageRef.putFile(imageUri!!)
        uploadTask.addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                storeData(uri.toString())
                Toast.makeText(this, "Profile image updated successfully.", Toast.LENGTH_SHORT)
                    .show()
            }.addOnFailureListener {
                Config.hideDialog()
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Config.hideDialog()
            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun storeData(imageUrl: String) {

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }

            val token = task.result

            val number = FirebaseAuth.getInstance().currentUser?.phoneNumber

            val data = HashMap<String, Any>()
            data["name"] = ui.userName.text.toString()
            data["email"] = ui.userEmail.text.toString()
            data["location"] = ui.userCity.text.toString()
            data["image"] = imageUrl
            data["number"] = number.toString()
            data["fcmToken"] = token

            FirebaseDatabase.getInstance().getReference("users")
                .child(FirebaseAuth.getInstance().currentUser!!.phoneNumber!!)
                .updateChildren(data)
                .addOnCompleteListener {
                    Config.hideDialog()
                    if (it.isSuccessful) {
                        Log.d("RISHDATAD", data.toString())
                        startActivity(Intent(this, MainActivity::class.java))
//                        Toast.makeText(this, "User Register Successful", Toast.LENGTH_SHORT).show()

                    }
                }
        })
    }
}