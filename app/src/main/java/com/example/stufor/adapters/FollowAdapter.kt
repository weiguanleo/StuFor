package com.example.stufor.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.stufor.R
import com.example.stufor.model.User
import com.example.stufor.util.UserUtil
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore

class FollowAdapter(options: FirestoreRecyclerOptions<User>, val context: Context) :
    FirestoreRecyclerAdapter<User, FollowAdapter.FollowViewHolder>(options) {
    class FollowViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val username: TextView = itemView.findViewById(R.id.textViewFollowingUsername)
        val followButton: Button = itemView.findViewById(R.id.btnFollow)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_following, parent, false)
        return FollowViewHolder(view)
    }

    override fun onBindViewHolder(holder: FollowViewHolder, position: Int, model: User) {
        holder.username.text = model.username

        if (UserUtil.user?.following?.contains(snapshots.getSnapshot(holder.absoluteAdapterPosition).id)!!) {
            holder.followButton.text = context.getString(R.string.following)
        } else {
            holder.followButton.text = context.getString(R.string.follow)
        }

        holder.followButton.setOnClickListener {
            val firestore = FirebaseFirestore.getInstance()
            val userDocument = firestore.collection("Users").document(UserUtil.user?.id!!)

            userDocument.get().addOnCompleteListener {
                if (it.isSuccessful) {
                    val user = it.result?.toObject(User::class.java)
                    if (holder.followButton.text == "Following") {
                        user?.following?.remove(snapshots.getSnapshot(holder.absoluteAdapterPosition).id)
                        user?.let {_user ->
                            userDocument.set(_user)
                        }
                        holder.followButton.text = "Follow"
                    } else {
                        user?.following?.add(snapshots.getSnapshot(holder.absoluteAdapterPosition).id)
                        user?.let {_user ->
                            userDocument.set(_user)
                        }
                        holder.followButton.text = "Following"
                    }
                } else {
                    Toast.makeText(
                        context,
                        "Something went wrong",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}