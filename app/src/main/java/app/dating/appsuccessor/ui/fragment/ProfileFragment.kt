package app.dating.appsuccessor.ui.fragment

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.dating.appsuccessor.R
import app.dating.appsuccessor.databinding.FragmentProfileBinding
import app.dating.appsuccessor.model.UserModel
import app.dating.appsuccessor.ui.activity.EditProfileActivity
import app.dating.appsuccessor.ui.activity.auth.LoginActivity
import app.dating.appsuccessor.ui.utils.Config
import app.dating.appsuccessor.ui.utils.clickTo
import app.dating.appsuccessor.ui.utils.setStatusBarColor
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ProfileFragment : Fragment() {

    private lateinit var ui: FragmentProfileBinding
    var data: UserModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        activity?.setStatusBarColor(Color.parseColor("#24D294FF"))

        Config.showDialog(requireContext())

        ui = FragmentProfileBinding.inflate(layoutInflater)

        ui.progressBar.progress = 90

        FirebaseDatabase.getInstance().getReference("users")
            .child(FirebaseAuth.getInstance().currentUser!!.phoneNumber!!).get()
            .addOnSuccessListener {
                if (it.exists()) {
                    data = it.getValue(UserModel::class.java)

                    val fullName = data!!.name
                    val div = fullName?.split(' ')
                    val firstName = div?.get(0)
                    ui.userName.text = "$firstName,"
                    val ageIneYears = calculateAge(data!!.dob.toString())
                    ui.userAge.text = ageIneYears.toString()

//                    ui.userNumber.setText(data!!.number.toString())
//                    ui.userCity.setText(data!!.location.toString())
//                    ui.userEmail.setText(data!!.email.toString())
                    Log.d("PROFIEL", "$data")

//                    Glide.with(requireContext()).load(data!!.images).placeholder(R.drawable.ic_user)
//                        .into(ui.userImage)

                    Config.hideDialog()
                }
            }

//        ui.Logout.clickTo {
//            FirebaseAuth.getInstance().signOut()
//            startActivity(Intent(requireContext(), LoginActivity::class.java))
//            requireActivity().finish()
//        }
//        ui.editProfile.clickTo {
//            val intent = Intent(requireContext(), EditProfileActivity::class.java)
//            intent.putExtra("userModel", data as Serializable)
//            startActivity(intent)
//        }


        return ui.root
    }

    fun calculateAge(dob: String): Int {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        val currentDate = Calendar.getInstance().time
        val dateOfBirth = dateFormat.parse(dob)

        val currentCalendar = Calendar.getInstance()
        currentCalendar.time = currentDate

        val dobCalendar = Calendar.getInstance()
        dobCalendar.time = dateOfBirth

        var age = currentCalendar.get(Calendar.YEAR) - dobCalendar.get(Calendar.YEAR)

        if (currentCalendar.get(Calendar.DAY_OF_YEAR) < dobCalendar.get(Calendar.DAY_OF_YEAR)) {
            age--
        }

        return age
    }
}