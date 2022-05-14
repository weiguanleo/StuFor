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
        val commentIcon: ImageView = itemView.findViewById(R.id.likePostButton)
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
        holder.likeCount.text = model.likeList.size.toString()

        val firestore = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        val postDocument =
            firestore.collection("Posts")
                .document(snapshots.getSnapshot(holder.absoluteAdapterPosition).id)

        postDocument.collection("Comments").get().addOnCompleteListener {
            if (it.isSuccessful) {
                holder.commentCount.text = it.result?.size().toString()
            }
        }

        postDocument.get().addOnCompleteListener {
            if (it.isSuccessful) {
                if (it.isSuccessful) {
                    val post = it.result?.toObject(Post::class.java)
                    post?.likeList?.let { list ->
                        if (list.contains(userId)) {
                            holder.likeIcon.setImageDrawable(
                                ContextCompat.getDrawable(
                                    context,
                                    R.drawable.icon_like_fill
                                )
                            )
                        } else {
                            holder.likeIcon.setImageDrawable(
                                ContextCompat.getDrawable(
                                    context,
                                    R.drawable.like_icon_outline
                                )
                            )
                        }
                        holder.likeIcon.setOnClickListener {
                            if (post.likeList.contains(userId)) {
                                post.likeList.remove(userId)
                                holder.likeIcon.setImageDrawable(
                                    ContextCompat.getDrawable(
                                        context,
                                        R.drawable.like_icon_outline
                                    )
                                )
                            } else {
                                userId?.let { userId ->
                                    post.likeList.add(userId)
                                }
                                holder.likeIcon.setImageDrawable(
                                    ContextCompat.getDrawable(
                                        context,
                                        R.drawable.icon_like_fill
                                    )
                                )
                            }
                        }
                        postDocument.set(post)
                    }
                } else {
                    Toast.makeText(
                        context,
                        "Something went wrong! Please try again.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            holder.commentIcon.setOnClickListener {
                val intent = Intent(context, CommentsActivity::class.java)
                intent.putExtra("postId", snapshots.getSnapshot(holder.absoluteAdapterPosition).id)
                context.startActivity(intent)
            }
        }
    }
}