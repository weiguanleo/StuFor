package com.example.stufor

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.LinearLayout
import android.widget.SearchView
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stufor.adapters.FeedAdapter
import com.example.stufor.databinding.FragmentFeedBinding
import com.example.stufor.model.Post
import com.example.stufor.model.User
import com.example.stufor.util.UserUtil
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class FeedFragment: Fragment() {

    lateinit var recyclerView: RecyclerView
    lateinit var adapter: FeedAdapter

    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView (
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonPost.setOnClickListener {
            startActivity(Intent(context, CreatePostActivity::class.java))
        }

        recyclerView = binding.recyclerViewFeed

        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        val firestore = FirebaseFirestore.getInstance()
        val query = firestore.collection("Posts").orderBy("time")

        val recyclerViewOptions =
            FirestoreRecyclerOptions.Builder<Post>().setQuery(query, Post::class.java).build()

        context?.let {
            adapter = FeedAdapter(recyclerViewOptions, it)
        }

        if (this::adapter.isInitialized) {
            recyclerView.adapter = adapter
        }

        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.itemAnimator = null
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}