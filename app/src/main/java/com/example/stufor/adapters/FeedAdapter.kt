package com.example.stufor.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.stufor.R
import com.example.stufor.model.Post
import com.example.stufor.CommentsActivity
import com.example.stufor.model.Like
import com.example.stufor.util.UserUtil
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


/*
Firestore Recycler Adapter for FeedFragment
 */
class FeedAdapter(options: FirestoreRecyclerOptions<Post>, val context: Context):
    FirestoreRecyclerAdapter<Post, FeedAdapter.FeedViewHolder>(options) {

    class FeedViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val content: TextView = itemView.findViewById(R.id.textViewContent)
        val likeIcon: ImageView = itemView.findViewById(R.id.likePostButton)
        val commentIcon: ImageView = itemView.findViewById(R.id.commentPostButton)
        val likeCount: TextView = itemView.findViewById(R.id.likePostCount)
        val commentCount: TextView = itemView.findViewById(R.id.commentPostCount)
        val authorText: TextView = itemView.findViewById(R.id.textViewAuthor)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return FeedViewHolder(view)
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int, model: Post) {

        holder.content.text = model.content
        holder.authorText.text = model.author.username

        val firestore = FirebaseFirestore.getInstance()

        val postDocument =
            firestore.collection("Posts")
                .document(snapshots.getSnapshot(holder.absoluteAdapterPosition).id)

        postDocument.collection("Comments").get().addOnCompleteListener {
            if (it.isSuccessful) {
                holder.commentCount.text = it.result?.size().toString()
            }
        }

        postDocument.collection("Likes").get().addOnCompleteListener {
            if (it.isSuccessful) {
                holder.likeCount.text = it.result?.size().toString()
            }
        }

        postDocument.get().addOnCompleteListener {
            //comment feature
            holder.commentIcon.setOnClickListener {
                val intent = Intent(context, CommentsActivity::class.java)
                intent.putExtra("postId", snapshots.getSnapshot(holder.absoluteAdapterPosition).id)
                context.startActivity(intent)
            }
            holder.likeIcon.setOnClickListener {
                val like = Like(UserUtil.user!!)
                postDocument.collection("Likes")
                    .document().set(like)

                holder.likeIcon.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.icon_like_fill
                    )
                )
                postDocument.collection("Likes").get().addOnCompleteListener {
                    if (it.isSuccessful) {
                        var likeCount = it.result?.size()?.minus(1)
                        if (likeCount != null) {
                            holder.likeCount.text = (likeCount + 1).toString()
                        }
                    }
                }
            }
        }
    }
}