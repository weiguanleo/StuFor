package com.example.stufor.auth

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.stufor.R
import com.example.stufor.databinding.FragmentLoginBinding
import com.example.stufor.util.MainActivity
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {

    companion object {
        const val TAG = "LoginFragment"
    }

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textViewSignUp.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.authFragmentContainer, RegisterFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.editTextEmail.editText?.text.toString()
            val password = binding.editTextPassword.editText?.text.toString()

            /*
            Check whether email field is empty or not?
            if not empty then valid or not?
             */
            if (TextUtils.isEmpty(email)) {
                binding.editTextEmail.error = "Email is required."
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.editTextEmail.error = "Enter a valid email address."
                return@setOnClickListener
            }

            /*
            Check whether password field is empty or not?
            if not empty then valid or not?
             */

            if (TextUtils.isEmpty(password)) {
                binding.editTextPassword.error = "Password is required."
                return@setOnClickListener
            }

            /*
            Authentication for Firebase
             */

            val auth = FirebaseAuth.getInstance()

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful)
                        startActivity(Intent(activity, MainActivity::class.java))
                    else {
                        Toast.makeText(
                            context,
                            "Something went wrong! Please try again.",
                            Toast.LENGTH_LONG
                        ).show()
                        // this line will send a debug message to the console
                        Log.d(TAG, task.exception.toString())
                    }
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}