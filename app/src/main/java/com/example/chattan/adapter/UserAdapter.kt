package com.example.chattan.adapter

import android.view.Display.Mode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chattan.R
import com.example.chattan.model.User
import com.google.firebase.auth.FirebaseAuth
import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter(
    private var userList: List<User>,
    private val mode: Mode,
    private val onAddClick: ((User) -> Unit)? = null
) :
    RecyclerView.Adapter<UserAdapter.ViewHolder>() {
    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val imgAvatar = view.findViewById<CircleImageView>(R.id.civAvatar)
        val tvUser = view.findViewById<TextView>(R.id.tvUser)
        val tvDesk = view.findViewById<TextView>(R.id.tvDesk)
        val ivAddFriend = view.findViewById<ImageView>(R.id.ivAddFriend)
    }

    enum class Mode{
        EXPLORE,
        FRIENDS
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = userList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = userList[position]
        val context = holder.itemView.context

        val resId = context.resources.getIdentifier(user.avatar ?: "", "drawable", context.packageName)
        holder.imgAvatar.setImageResource(resId)
        holder.tvUser.text = user.username

        if (mode == Mode.EXPLORE) {
            holder.tvDesk.visibility = View.GONE
            val myUid = FirebaseAuth.getInstance().currentUser?.uid
            if (user.friends.contains(myUid)) {
                holder.ivAddFriend.setImageResource(R.drawable.ic_friends)
                holder.ivAddFriend.setOnClickListener(null)
            } else {
                holder.ivAddFriend.setImageResource(R.drawable.ic_addfriend)
                holder.ivAddFriend.setOnClickListener {
                    onAddClick?.invoke(user)
                }
            }
        } else if (mode == Mode.FRIENDS) {
            holder.tvDesk.visibility = View.VISIBLE
            holder.tvDesk.text = "Lorem Ipsum"
            holder.ivAddFriend.visibility = View.GONE
        }
    }
    fun updateList(newList: List<User>) {
        userList = newList
        notifyDataSetChanged()
    }
}