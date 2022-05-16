package com.example.stufor

import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stufor.adapters.FollowAdapter
import com.example.stufor.databinding.FragmentFollowingBinding
import com.example.stufor.model.User
import com.example.stufor.util.UserUtil
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FollowFragment : Fragment() {

    lateinit var adapter: FollowAdapter
    lateinit var recyclerView: RecyclerView

    private var _binding: FragmentFollowingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFollowingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolBarSearch.title = "Search For Friends"
        (activity as? MainActivity)?.setSupportActionBar(binding.toolBarSearch)
        (activity as? MainActivity)?.supportActionBar?.show()

        setHasOptionsMenu(true)

        val firestore = FirebaseFirestore.getInstance()
        val query = firestore.collection("Users")
            .whereNotEqualTo("id", FirebaseAuth.getInstance().currentUser?.uid)

        val recyclerViewOptions =
            FirestoreRecyclerOptions.Builder<User>().setQuery(query, User::class.java).build()

        adapter = FollowAdapter(recyclerViewOptions, requireContext())

        recyclerView = binding.recyclerViewFollowing

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.itemAnimator = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.search_menu, menu)

        val searchView = SearchView(context)
        menu.findItem(R.id.action_search).actionView = searchView

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                recyclerView.visibility = View.VISIBLE
                val firestore = FirebaseFirestore.getInstance()
                val newQuery = firestore.collection("Users")
                    .whereEqualTo("name", query)
                    .whereNotEqualTo("id", UserUtil.user?.id)

                val newRecyclerViewOptions =
                    FirestoreRecyclerOptions.Builder<User>().setQuery(newQuery, User::class.java)
                        .build()

                adapter.updateOptions(newRecyclerViewOptions)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                recyclerView.visibility = View.INVISIBLE
                return false
            }
        })
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