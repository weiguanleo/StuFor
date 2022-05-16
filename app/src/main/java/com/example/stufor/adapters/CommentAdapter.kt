package com.example.stufor.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.stufor.R
import com.example.stufor.model.Comment
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class CommentAdapter (options: FirestoreRecyclerOptions<Comment>, val context: Context):
    FirestoreRecyclerAdapter<Comment, CommentAdapter.CommentViewHolder>(options) {

    class CommentViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val commentText: TextView = itemView.findViewById(R.id.textViewComment)
        val commentAuthor: TextView = itemView.findViewById(R.id.textViewCommentAuthor)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int, model: Comment) {
        holder.commentText.text = model.text
        holder.commentAuthor.text = model.author.username
    }
}

