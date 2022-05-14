package com.example.stufor.util

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.stufor.databinding.ActivityCreatePostBinding
import com.example.stufor.model.Post
import com.example.stufor.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CreatePostActivity : AppCompatActivity() {

    companion object {
        const val TAG = "CreatePostActivity"
    }

    private lateinit var binding: ActivityCreatePostBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreatePostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnPost.setOnClickListener {
            val content = binding.postContent.text.toString()

            if (TextUtils.isEmpty(content)) {
                Toast.makeText (
                    this,
                    "Content cannot be empty.",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }
            addPost(content)
        }
    }

    private fun addPost(content: String) {
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("Users")
            .document(FirebaseAuth.getInstance().currentUser?.uid!!).get()
            .addOnCompleteListener {
                val user = it.result?.toObject(User::class.java)
                val post =
                    Post(content, user!!, System.currentTimeMillis())
                firestore.collection("Posts")
                    .document()
                    .set(post)
                    .addOnCompleteListener { posted ->
                        if (posted.isSuccessful) {
                            Toast.makeText(
                                this,
                                "Posted Successfully",
                                Toast.LENGTH_LONG
                            ).show()
                            finish()
                        } else {
                            Toast.makeText(
                                this,
                                "An error have occurred! Please try again.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
            }
        }
}