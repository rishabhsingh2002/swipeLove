package app.dating.appsuccessor.ui.fragment

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import app.dating.appsuccessor.databinding.FragmentChatBinding
import app.dating.appsuccessor.ui.adapter.MatchesUserAdapter
import app.dating.appsuccessor.ui.adapter.MessageUserAdapter
import app.dating.appsuccessor.ui.utils.Config
import app.dating.appsuccessor.ui.utils.setStatusBarColor
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatFragment : Fragment() {
    private lateinit var ui: FragmentChatBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        ui = FragmentChatBinding.inflate(layoutInflater)

        activity?.setStatusBarColor(Color.parseColor("#F7F9F7"))


        return ui.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getData()

    }

    private fun getData() {
        Config.showDialog(requireContext())
        val currentId = FirebaseAuth.getInstance().currentUser?.phoneNumber
        FirebaseDatabase.getInstance().getReference("chats")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    val list = arrayListOf<String>()
                    val newList = arrayListOf<String>()

                    for (data in snapshot.children) {
                        if (data.key!!.contains(currentId!!)) {
                            list.add(data.key!!.replace(currentId, ""))
                            newList.add(data.key!!)
                        }
                    }
                    try {
                        ui.rvUsers.adapter = MessageUserAdapter(requireContext(), list, newList)
                        ui.rvTop.adapter = MatchesUserAdapter(requireContext(), list, newList)
                    } catch (e: Exception) {
                    }
                    Config.hideDialog()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
                }

            })
    }

}