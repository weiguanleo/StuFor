package com.example.stufor

import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.stufor.R
import com.example.stufor.auth.AuthenticationActivity
import com.example.stufor.databinding.ActivityMainBinding
import com.example.stufor.FeedFragment
import com.example.stufor.util.UserUtil
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*
        If the user is not logged in, take to AuthenticationActivity
         */

        if (FirebaseAuth.getInstance().currentUser == null) {
            startActivity(Intent(this, AuthenticationActivity::class.java))
        }

        UserUtil.getCurrentUser()

        /*
        Default fragment
         */
        setFragment(FeedFragment())

        /*
        Bottom navigation view
         */
        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.feed_item -> {
                    setFragment(FeedFragment())
                }
                R.id.following_item -> {
                    setFragment(FollowFragment())
                }
                R.id.my_account_item -> {
                    setFragment(ProfileFragment())
                }
            }
            true
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    private fun setFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}