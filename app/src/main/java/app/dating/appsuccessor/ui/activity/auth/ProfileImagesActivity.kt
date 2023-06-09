package app.dating.appsuccessor.ui.activity.auth

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import app.dating.appsuccessor.R
import app.dating.appsuccessor.databinding.ActivityProfileImagesBinding
import app.dating.appsuccessor.ui.utils.Config
import app.dating.appsuccessor.ui.utils.clickTo
import app.dating.appsuccessor.ui.utils.setStatusBarColor
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.default
import id.zelory.compressor.constraint.quality
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File

class ProfileImagesActivity : AppCompatActivity() {
    private lateinit var ui: ActivityProfileImagesBinding
    private val selectedImageUris = mutableListOf<Uri>()
    private lateinit var database: FirebaseDatabase
    private lateinit var imagesRef: DatabaseReference
    private val selectedImageCount = MutableLiveData<Int>(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = ActivityProfileImagesBinding.inflate(layoutInflater)
        setContentView(ui.root)

        setStatusBarColor(Color.parseColor("#F7F9F7"))

        database = FirebaseDatabase.getInstance()
        imagesRef =
            database.reference.child("images") // Change "images" to your desired child node name

        selectedImageCount.observe(this, Observer { count ->
            if (count >= 2) {
                enableButton()
            } else {
                disableButton()
            }
        })

        ui.apply {
            profileOne.addProfile.clickTo {
                showImagePickerOptions(REQUEST_CODE_IMAGE_ONE)
            }
            profileTwo.addProfile.clickTo {
                showImagePickerOptions(REQUEST_CODE_IMAGE_TWO)
            }
            profileThree.addProfile.clickTo {
                showImagePickerOptions(REQUEST_CODE_IMAGE_THREE)
            }
            profileFour.addProfile.clickTo {
                showImagePickerOptions(REQUEST_CODE_IMAGE_FOUR)
            }
            next.clickTo {
                if (selectedImageUris.size >= 2) {
                    uploadProfileImages()
                } else {
                    Toast.makeText(
                        this@ProfileImagesActivity,
                        "Please select at least two images",
                        Toast.LENGTH_SHORT
                    ).show()
                    // Show an error message to the user indicating that at least two images are required
                }
            }
        }
    }

    private fun showImagePickerOptions(requestCode: Int) {
        ImagePicker.with(this)
            .galleryMimeTypes(
                mimeTypes = arrayOf(
                    "image/png",
                    "image/jpg",
                    "image/jpeg"
                )
            )
            .cropSquare()
            .compress(2048)
            .maxResultSize(1080, 1080)
            .start(requestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_IMAGE_ONE,
                REQUEST_CODE_IMAGE_TWO,
                REQUEST_CODE_IMAGE_THREE,
                REQUEST_CODE_IMAGE_FOUR -> handleImageSelection(data)
            }
        }
    }


    private fun handleImageSelection(data: Intent?) {
        val uri: Uri? = data?.data
        uri?.let {
            val imageView: ImageView = getCorrespondingImageView(selectedImageUris.size + 1)
            val deleteButton: ImageView = getCorrespondingDeleteButton(selectedImageUris.size + 1)
            val addImageButton: ImageView =
                getCorrespondingAddImageButton(selectedImageUris.size + 1)

            if (selectedImageUris.contains(uri)) {
                // Deselect the image if it was already selected
                selectedImageUris.remove(uri)
                imageView.setImageResource(R.drawable.ic_profile_image_bg)
                deleteButton.visibility = View.GONE
                selectedImageCount.value = selectedImageUris.size
            } else {
                // Select the image if it was not already selected
                selectedImageUris.add(uri)
                imageView.setImageURI(uri)
                deleteButton.visibility = View.VISIBLE
                selectedImageCount.value = selectedImageUris.size
            }

            addImageButton.visibility = if (selectedImageUris.size < 4) {
                View.VISIBLE
            } else {
                View.GONE
            }

            deleteButton.setOnClickListener {
                selectedImageUris.remove(uri)
                imageView.setImageResource(R.drawable.ic_profile_image_bg)
                deleteButton.visibility = View.GONE
                addImageButton.visibility = View.VISIBLE
                selectedImageCount.value = selectedImageUris.size // Update the selected image count
            }

            if (selectedImageUris.size >= 2) {
                enableButton()
            } else {
                disableButton()
            }
        }
    }


    private fun getCorrespondingAddImageButton(index: Int): ImageView {
        return when (index) {
            1 -> ui.profileOne.addProfile
            2 -> ui.profileTwo.addProfile
            3 -> ui.profileThree.addProfile
            4 -> ui.profileFour.addProfile
            else -> throw IllegalArgumentException("Invalid index for ImageView")
        }
    }

    private fun getCorrespondingImageView(index: Int): ImageView {
        return when (index) {
            1 -> ui.profileOne.profile
            2 -> ui.profileTwo.profile
            3 -> ui.profileThree.profile
            4 -> ui.profileFour.profile
            else -> throw IllegalArgumentException("Invalid index for ImageView")
        }
    }

    private fun getCorrespondingDeleteButton(index: Int): ImageView {
        return when (index) {
            1 -> ui.profileOne.deleteProfile
            2 -> ui.profileTwo.deleteProfile
            3 -> ui.profileThree.deleteProfile
            4 -> ui.profileFour.deleteProfile
            else -> throw IllegalArgumentException("Invalid index for DeleteButton")
        }
    }

    private fun uploadProfileImages() {
        Config.showDialog(this@ProfileImagesActivity)
        lifecycleScope.launch {
            val uploadTasks = mutableListOf<Deferred<String>>()
            val imageUrls = mutableListOf<String>()

            selectedImageUris.forEach { uri ->
                val compressedImageFile = compressImage(uri)
                val uploadTask = async { uploadImageToFirebaseStorage(compressedImageFile) }
                uploadTasks.add(uploadTask)
            }

            uploadTasks.forEach { task ->
                val imageUrl = task.await()
                imageUrl?.let {
                    imageUrls.add(it)
                }
            }

            if (imageUrls.size >= 2) {
                enableButton()
                saveImageUrlsToDatabase(imageUrls) // Pass the imageUrls list
            } else {
                Toast.makeText(
                    this@ProfileImagesActivity,
                    "Error uploading images",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    private suspend fun compressImage(uri: Uri): File = withContext(Dispatchers.IO) {
        val compressedImageFile = Compressor.compress(applicationContext, File(uri.path)) {
            default(format = Bitmap.CompressFormat.WEBP)
            quality(80) // Adjust the compression quality as per your requirements
        }
        return@withContext compressedImageFile
    }

    private suspend fun uploadImageToFirebaseStorage(file: File): String =
        withContext(Dispatchers.IO) {
            val imageFileName = "${System.currentTimeMillis()}_${file.name}"
            val user = FirebaseAuth.getInstance().currentUser?.phoneNumber
            val storageRef = FirebaseStorage.getInstance().reference
                .child(user!!)
                .child("profile_images/$imageFileName")
            val uploadTask = storageRef.putFile(Uri.fromFile(file)).await()

            return@withContext storageRef.downloadUrl.await().toString()
        }


    private fun saveImageUrlsToDatabase(imageUrls: List<String>) {
        val user = FirebaseAuth.getInstance().currentUser?.phoneNumber
        user?.let { phoneNumber ->
            val userImagesRef = FirebaseDatabase.getInstance().reference
                .child("users")
                .child(phoneNumber)
                .child("images")
            userImagesRef.setValue(imageUrls)
                .addOnSuccessListener {
                    Config.hideDialog()
                    startActivity(Intent(this@ProfileImagesActivity, StoryActivity::class.java))
                    Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener { exception ->
                    Toast.makeText(
                        this@ProfileImagesActivity,
                        "Error uploading image: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }


    private fun enableButton() {
        val activeButtonStyle = R.style.ButtonActive
        val attrs = intArrayOf(android.R.attr.textColor, android.R.attr.background)
        val typedArray = obtainStyledAttributes(activeButtonStyle, attrs)
        val activeTextColor = typedArray.getColor(0, 0)
        val activeBackground = typedArray.getDrawable(1)
        val button = ui.next
        button.setTextColor(activeTextColor)
        button.background = activeBackground
        button.setTextAppearance(this@ProfileImagesActivity, activeButtonStyle)
    }

    private fun disableButton() {
        val inactiveButtonStyle = R.style.ButtonInactive
        val attrs = intArrayOf(android.R.attr.textColor, android.R.attr.background)
        val typedArray = obtainStyledAttributes(inactiveButtonStyle, attrs)
        val inactiveTextColor = typedArray.getColor(0, 0)
        val inactiveBackground = typedArray.getDrawable(1)
        val button = ui.next
        button.setTextColor(inactiveTextColor)
        button.background = inactiveBackground
        button.setTextAppearance(this@ProfileImagesActivity, inactiveButtonStyle)
        button.isEnabled = true
    }

    companion object {
        private const val REQUEST_CODE_IMAGE_ONE = 1001
        private const val REQUEST_CODE_IMAGE_TWO = 1002
        private const val REQUEST_CODE_IMAGE_THREE = 1003
        private const val REQUEST_CODE_IMAGE_FOUR = 1004
    }
}
