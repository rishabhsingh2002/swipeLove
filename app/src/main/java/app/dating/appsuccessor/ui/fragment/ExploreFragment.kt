package app.dating.appsuccessor.ui.fragment

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import app.dating.appsuccessor.R
import app.dating.appsuccessor.databinding.FragmentExploreBinding
import app.dating.appsuccessor.model.UserModel
import app.dating.appsuccessor.notification.api.ApiUtilities
import app.dating.appsuccessor.notification.model.NotificationData
import app.dating.appsuccessor.notification.model.PushNotification
import app.dating.appsuccessor.ui.adapter.DatingAdapter
import app.dating.appsuccessor.ui.utils.Config
import app.dating.appsuccessor.ui.utils.setStatusBarColor
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.Direction
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.ArrayList

class ExploreFragment : Fragment() {

    private lateinit var ui: FragmentExploreBinding
    var data: UserModel? = null
    private lateinit var manager: CardStackLayoutManager
    private var senderId: String? = null
    private var chatId: String? = null
    private var receiverId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        ui = FragmentExploreBinding.inflate(layoutInflater)

        activity?.setStatusBarColor(Color.parseColor("#F7F9F7"))

        getData()

        return ui.root
    }

    private fun init() {
        manager = CardStackLayoutManager(requireContext(), object : CardStackListener {
            override fun onCardDragging(direction: Direction?, ratio: Float) {

            }

            override fun onCardSwiped(direction: Direction?) {
                if (direction == Direction.Right) {
                    val phoneNumber = list?.get(manager.topPosition - 1)?.number
                    if (phoneNumber != null) {
                        // get reference to the current user's Firebase Database node
                        val userReference = FirebaseDatabase.getInstance().getReference("users")
                            .child(FirebaseAuth.getInstance().currentUser!!.phoneNumber!!)

                        // add the phone number to the current user's rightSwiped list
                        userReference.child("rightSwiped")
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val rightSwipedNumbers =
                                        snapshot.children.mapNotNull { it.value as? String }
                                    if (!rightSwipedNumbers.contains(phoneNumber)) {
                                        rightSwipedNumbers.toMutableList().apply {
                                            add(phoneNumber)
                                            userReference.child("rightSwiped").setValue(this)
                                        }
                                    }
                                    // remove the right-swiped item from the list
                                    // list?.removeAt(manager.topPosition - 1)
                                    //checking the both user's right swiped each other
                                    checkIfCurrentUserIsRightSwiped()
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Toast.makeText(
                                        requireContext(), error.message, Toast.LENGTH_SHORT
                                    ).show()
                                }
                            })
                    }
                }
                if (manager.topPosition == list?.size) {
                    Toast.makeText(requireContext(), "This is last card", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCardRewound() {
            }

            override fun onCardCanceled() {
            }

            override fun onCardAppeared(view: View?, position: Int) {
            }

            override fun onCardDisappeared(view: View?, position: Int) {
            }

        })
        manager.setVisibleCount(3)
        manager.setTranslationInterval(0.6f)
        manager.setScaleInterval(0.8f)
        manager.setMaxDegree(20.0f)
        manager.setDirections(Direction.HORIZONTAL)
    }

    companion object {
        var list: ArrayList<UserModel>? = null
    }

    private fun getData() {
        FirebaseDatabase.getInstance().getReference("users")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d("RISH", "onDataChanged : ${snapshot.toString()}")
                    if (snapshot.exists()) {
                        list = arrayListOf()
                        for (data in snapshot.children) {
                            try {
                                val model = data.getValue(UserModel::class.java)
                                if (model!!.number != FirebaseAuth.getInstance().currentUser?.phoneNumber) {
                                    list?.add(model)
                                }
                            } catch (e: Exception) {
                            }

                        }
                        list?.shuffle()
                        init()

                        ui.cardStackView.layoutManager = manager
                        ui.cardStackView.itemAnimator = DefaultItemAnimator()
                        ui.cardStackView.adapter = DatingAdapter(requireContext(), list!!)

                    } else {
                        Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
                }

            })
    }

    private fun checkIfCurrentUserIsRightSwiped() {
        val currentUserPhoneNumber = FirebaseAuth.getInstance().currentUser?.phoneNumber
        val currentUserReference =
            FirebaseDatabase.getInstance().getReference("users").child(currentUserPhoneNumber!!)

        currentUserReference.child("rightSwiped")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val rightSwipedNumbers = snapshot.children.mapNotNull { it.value as? String }
                    val otherUsersReference = FirebaseDatabase.getInstance().getReference("users")

                    for (phoneNumber in rightSwipedNumbers) {
                        otherUsersReference.child(phoneNumber).child("rightSwiped")
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(otherSnapshot: DataSnapshot) {
                                    val otherRightSwipedNumbers =
                                        otherSnapshot.children.mapNotNull { it.value as? String }
                                    if (otherRightSwipedNumbers.contains(currentUserPhoneNumber)) {
                                        // The current user is right-swiped by this user
                                        // Do something here
                                        verifyChatId(phoneNumber)
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Toast.makeText(
                                        requireContext(), error.message, Toast.LENGTH_SHORT
                                    ).show()
                                }
                            })
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        requireContext(), error.message, Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun verifyChatId(number: String) {
        receiverId = number
        senderId = FirebaseAuth.getInstance().currentUser?.phoneNumber

        chatId = senderId + receiverId
        val reverseChatId = receiverId + senderId

        val reference = FirebaseDatabase.getInstance().getReference("chats")

        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!(snapshot.hasChild(chatId!!) || snapshot.hasChild(reverseChatId))) {
                    storeData()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()
            }
        })

        // for showing the matched dialog
        val currentUserReference = FirebaseDatabase.getInstance().getReference("users")
            .child(senderId!!)

        currentUserReference.child("rightSwiped")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val rightSwipedNumbers = snapshot.children.mapNotNull { it.value as? String }
                    val otherUsersReference = FirebaseDatabase.getInstance().getReference("users")

                    if (rightSwipedNumbers.contains(receiverId)) {
                        // The current user has right-swiped the other user
                        otherUsersReference.child(receiverId!!)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(otherSnapshot: DataSnapshot) {
                                    val otherUserData =
                                        otherSnapshot.getValue(UserModel::class.java)
                                    val otherUserImageUrl = otherUserData?.images?.get(0)
                                    val otherUserName = otherUserData?.name

                                    //for current user
                                    FirebaseDatabase.getInstance().getReference("users")
                                        .child(FirebaseAuth.getInstance().currentUser!!.phoneNumber!!)
                                        .get()
                                        .addOnSuccessListener {
                                            if (it.exists()) {
                                                data = it.getValue(UserModel::class.java)
                                                val currentUserImageUrl = data?.images?.get(0)
                                                showMatchedDialog(
                                                    currentUserImageUrl.toString(),
                                                    otherUserImageUrl.toString(),
                                                    otherUserName.toString()
                                                )
                                            }
                                        }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Toast.makeText(
                                        requireContext(), error.message, Toast.LENGTH_SHORT
                                    ).show()
                                }
                            })
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        requireContext(), error.message, Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun storeData() {
        val reference = FirebaseDatabase.getInstance().getReference("chats").child(chatId!!)
        reference.setValue("")
        sendNewMatchNotification()
    }

    private fun sendNewMatchNotification() {
        // for received User
        FirebaseDatabase.getInstance().getReference("users").child(receiverId!!)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val data = snapshot.getValue(UserModel::class.java)

                        val notificationData = PushNotification(
                            NotificationData("New Match", "New Match Found"), data?.fcmToken
                        )
                        ApiUtilities.getInstance().sendNotification(notificationData)
                            .enqueue(object : Callback<PushNotification> {
                                override fun onResponse(
                                    call: Call<PushNotification>,
                                    response: Response<PushNotification>
                                ) {
                                    // Handle success
                                }

                                override fun onFailure(call: Call<PushNotification>, t: Throwable) {
                                    // Handle failure
                                }
                            })
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun showMatchedDialog(
        currentUserImageUrl: String,
        otherUserImageUrl: String,
        otherUserName: String
    ) {
        val dialog = Dialog(requireContext(), R.style.Base_Theme_Appsuccessor)
        dialog.setContentView(R.layout.dialog_matched)
        // Get references to the ImageView elements in the dialog layout
        val user1ImageView = dialog.findViewById<ImageView>(R.id.userImageOne)
        val user2ImageView = dialog.findViewById<ImageView>(R.id.userImageTwo)
        val matchedUser = dialog.findViewById<TextView>(R.id.matchedUser)

        val fullName = otherUserName
        val div = fullName?.split(' ')
        val firstName = div?.get(0)

        matchedUser.text = "What are you waiting for? Text $firstName now"

        Glide.with(this)
            .load(currentUserImageUrl)
            .into(user1ImageView)
        Glide.with(this)
            .load(otherUserImageUrl)
            .into(user2ImageView)
        dialog.show()
    }
}