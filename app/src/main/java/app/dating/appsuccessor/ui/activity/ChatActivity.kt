package app.dating.appsuccessor.ui.activity

import android.graphics.Color
import android.icu.text.SimpleDateFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import app.dating.appsuccessor.R
import app.dating.appsuccessor.databinding.ActivityChatBinding
import app.dating.appsuccessor.model.MessageModel
import app.dating.appsuccessor.model.UserModel
import app.dating.appsuccessor.notification.api.ApiUtilities
import app.dating.appsuccessor.notification.model.NotificationData
import app.dating.appsuccessor.notification.model.PushNotification
import app.dating.appsuccessor.ui.adapter.MessageAdapter
import app.dating.appsuccessor.ui.utils.Config
import app.dating.appsuccessor.ui.utils.clickTo
import app.dating.appsuccessor.ui.utils.setStatusBarColor
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class ChatActivity : AppCompatActivity() {
    lateinit var ui: ActivityChatBinding
    var data: UserModel? = null

    private var senderId: String? = null
    private var chatId: String? = null
    private var receiverId: String? = null
    private lateinit var list: ArrayList<MessageModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = ActivityChatBinding.inflate(layoutInflater)
        setContentView(ui.root)

        setStatusBarColor(Color.parseColor("#F7F9F7"))

        verifyChatId()
        getData(chatId)

        list = ArrayList()

        showToolBarDetails()

        ui.apply {
            sendMessage.clickTo {
                if (message.text!!.isEmpty()) {
                    Toast.makeText(
                        this@ChatActivity, "Please enter your message", Toast.LENGTH_SHORT
                    ).show()
                } else {
                    storeData(message.text.toString())
                    getData(chatId)
                }
            }
            backButton.clickTo {
                onBackPressed()
            }
        }
    }

    private fun showToolBarDetails() {
        val recieverUserId = intent.getStringExtra("userId")
        FirebaseDatabase.getInstance().getReference("users")
            .child(recieverUserId.toString()).get()
            .addOnSuccessListener {
                if (it.exists()) {
                    data = it.getValue(UserModel::class.java)
                    ui.userName.setText("${data!!.name.toString()}")
                    Glide.with(this@ChatActivity).load(data!!.images?.get(0))
                        .placeholder(R.drawable.ic_loading_user_image)
                        .into(ui.userImage)
                }
            }
    }

    private fun verifyChatId() {
        receiverId = intent.getStringExtra("userId")
        senderId = FirebaseAuth.getInstance().currentUser?.phoneNumber

        chatId = senderId + receiverId
        val reverseChatId = receiverId + senderId

        val reference = FirebaseDatabase.getInstance().getReference("chats")

        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChild(chatId!!)) {
                    getData(chatId)
                } else if (snapshot.hasChild(reverseChatId)) {
                    chatId = reverseChatId
                    getData(chatId)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ChatActivity, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getData(chatId: String?) {
        val reference = FirebaseDatabase.getInstance().getReference("chats").child(chatId!!)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()
                // Fetch data for the first message separately
                var firstMessageSnapshot: DataSnapshot? = null
                try {
                    firstMessageSnapshot = snapshot.children.iterator().next()
                    val firstMessageData = firstMessageSnapshot.getValue(MessageModel::class.java)
                    list.add(firstMessageData!!)
                } catch (e: java.lang.Exception) {
                }
                // Fetch data for the remaining messages
                for (show in snapshot.children) {
                    if (show.key != firstMessageSnapshot?.key) {
                        val data = show.getValue(MessageModel::class.java)
                        list.add(data!!)
                    }
                }

                val llm = LinearLayoutManager(this@ChatActivity)
                llm.stackFromEnd = true     // items gravity sticks to bottom
                llm.reverseLayout = false
                ui.recyclerView.layoutManager = llm
                ui.recyclerView.adapter = MessageAdapter(this@ChatActivity, list)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ChatActivity, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun storeData(message: String) {
        val reference = FirebaseDatabase.getInstance().getReference("chats").child(chatId!!)
        //current Time and date
        val currentTime = Calendar.getInstance().time
        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val formattedDate = dateFormat.format(currentDate)
        val formattedTime = timeFormat.format(currentTime)

        val map = hashMapOf<String, String>()
        map["message"] = message
        map["senderId"] = senderId!!
        map["currentTime"] = formattedTime
        map["currentDate"] = formattedDate

        reference.child(reference.push().key!!).setValue(map).addOnCompleteListener {
            if (it.isSuccessful) {
                ui.message.text = null
                sendNotification(message)
//                    Toast.makeText(this, "Message send", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendNotification(message: String) {
        FirebaseDatabase.getInstance().getReference("users").child(receiverId!!)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val data = snapshot.getValue(UserModel::class.java)
                        Log.d("DATANOT", data.toString())

                        //name from current user
                        FirebaseDatabase.getInstance().getReference("users")
                            .child(FirebaseAuth.getInstance().currentUser!!.phoneNumber!!).get()
                            .addOnSuccessListener {
                                if (it.exists()) {
                                    var user: UserModel? = null
                                    user = it.getValue(UserModel::class.java)

                                    //send the notification
                                    val notificationData = PushNotification(
                                        NotificationData(
                                            "New Message from ${user?.name}", message
                                        ),
                                        data!!.fcmToken
                                    )
                                    ApiUtilities.getInstance().sendNotification(
                                        notificationData
                                    ).enqueue(object : Callback<PushNotification> {
                                        override fun onResponse(
                                            call: Call<PushNotification>,
                                            response: Response<PushNotification>
                                        ) {
//                                Toast.makeText(
//                                    this@ChatActivity,
//                                    "Notification sent",
//                                    Toast.LENGTH_SHORT
//                                ).show()
                                        }

                                        override fun onFailure(
                                            call: Call<PushNotification>,
                                            t: Throwable
                                        ) {
                                            Toast.makeText(
                                                this@ChatActivity,
                                                "Something went wrong ${t.message}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }

                                    })
                                }
                            }


                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@ChatActivity, error.message, Toast.LENGTH_SHORT).show()
                }
            })
    }
}