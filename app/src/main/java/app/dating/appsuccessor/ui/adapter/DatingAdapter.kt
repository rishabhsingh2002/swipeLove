package app.dating.appsuccessor.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import app.dating.appsuccessor.databinding.ItemUserBinding
import app.dating.appsuccessor.model.UserModel
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.lang.Math.abs
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class DatingAdapter(val context: Context, val list: ArrayList<UserModel>) :
    RecyclerView.Adapter<DatingAdapter.DatingViewHolder>() {

    inner class DatingViewHolder(val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DatingViewHolder {
        return DatingViewHolder(
            ItemUserBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: DatingViewHolder, position: Int) {
        holder.binding.apply {
            val fullName = list[position].name
            val div = fullName?.split(' ')
            val firstName = div?.get(0)
            userName.text = "$firstName,"
            val firstImage = list[position].images?.get(0)
            val secondImage = list[position].images?.get(1)
            Glide.with(context).load(firstImage).into(userImage)
//            Glide.with(context).load(secondImage).into(userSecondImage)
            val ageInYears = calculateAge(list[position].dob.toString())
            age.text = ageInYears.toString()


            // calculate distance
            val currentUser = FirebaseAuth.getInstance().currentUser?.phoneNumber
            FirebaseDatabase.getInstance().getReference("users")
                .child(currentUser.toString())
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val data = snapshot.getValue(UserModel::class.java)
                            // for the user in card
                            val currentUserLocation = data?.location
                            val listUserLocation = list[holder.adapterPosition].location
                            val distance =
                                calculateDistanceInKm(listUserLocation, currentUserLocation)
//                            holder.binding.userDistance.text = "$distance Km away"
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
                    }

                })

//            chat.clickTo {
//                val intent = Intent(context, ChatActivity::class.java)
//                intent.putExtra("userId", list[holder.adapterPosition].number)
//                context.startActivity(intent)
//            }
        }
    }

    fun calculateDistanceInKm(location1: String?, location2: String?): Int {
        val earthRadius = 6371 // in kilometers
        val (lat1, lon1) = location1?.split(",")!!.map { it.toDouble() }
        val (lat2, lon2) = location2?.split(",")!!.map { it.toDouble() }
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return (earthRadius * c).toInt()
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