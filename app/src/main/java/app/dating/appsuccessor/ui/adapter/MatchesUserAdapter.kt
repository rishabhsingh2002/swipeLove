package app.dating.appsuccessor.ui.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import app.dating.appsuccessor.databinding.UserItemMatchesBinding
import app.dating.appsuccessor.databinding.UsersItemChatBinding
import app.dating.appsuccessor.model.UserModel
import app.dating.appsuccessor.ui.activity.ChatActivity
import app.dating.appsuccessor.ui.utils.clickTo
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class MatchesUserAdapter(
    val context: Context,
    val list: ArrayList<String>,
    val chatKey: ArrayList<String>
) :
    RecyclerView.Adapter<MatchesUserAdapter.MatchesUserViewHolder>() {
    inner class MatchesUserViewHolder(val binding: UserItemMatchesBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchesUserViewHolder {
        return MatchesUserViewHolder(
            UserItemMatchesBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MatchesUserViewHolder, position: Int) {

        FirebaseDatabase.getInstance().getReference("users")
            .child(list[position]).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val data = snapshot.getValue(UserModel::class.java)
                        holder.binding.apply {
                            Glide.with(context).load(data?.images?.get(0)).into(userImage)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
                }

            })

        holder.itemView.clickTo {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("chat_id", chatKey[position])
            intent.putExtra("userId", list[position])
            context.startActivity(intent)
        }
    }
}