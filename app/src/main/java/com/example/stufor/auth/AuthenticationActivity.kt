package com.example.stufor.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.stufor.R
import com.example.stufor.databinding.ActivityAuthenticationBinding

class AuthenticationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityAuthenticationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*
        as soon as user started, take to login fragment
         */

        supportFragmentManager.beginTransaction()
            .replace(R.id.authFragmentContainer, LoginFragment())
            .commit()
    }
}