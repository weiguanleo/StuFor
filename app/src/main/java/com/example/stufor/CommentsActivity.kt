package com.example.stufor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stufor.adapters.CommentAdapter
import com.example.stufor.databinding.ActivityCommentsBinding
import com.example.stufor.model.Comment
import com.example.stufor.util.UserUtil
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore

class CommentsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCommentsBinding

    private var postId: String? = null
    private var commentAdapter: CommentAdapter? = null
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommentsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        postId = intent.getStringExtra("postId")
        recyclerView = binding.recyclerViewComment

        setUpRecyclerView()

        binding.imageViewCommentSend.setOnClickListener {
            val commentText = binding.editTextComment.editableText.toString()
            val firestore = FirebaseFirestore.getInstance()
            val comment = Comment(commentText, UserUtil.user!!, System.currentTimeMillis())
            firestore.collection("Posts").document(postId!!)
                .collection("Comments")
                .document().set(comment)

            binding.editTextComment.editableText.clear()
        }
    }

    private fun setUpRecyclerView() {
        val firestore = FirebaseFirestore.getInstance()
        val query = postId?.let{
            firestore.collection("Posts").document(it).collection("Comments").orderBy("time")
        }
        val recyclerViewOptions = query?.let {
            FirestoreRecyclerOptions.Builder<Comment>().setQuery(it, Comment::class.java).build()
        }
        commentAdapter = recyclerViewOptions?.let {
            CommentAdapter(it, this)
        }
        recyclerView.adapter = commentAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.itemAnimator = null
    }

    override fun onStart() {
        super.onStart()
        commentAdapter?.startListening()
    }

    override fun onStop() {
        super.onStop()
        commentAdapter?.stopListening()
    }
}