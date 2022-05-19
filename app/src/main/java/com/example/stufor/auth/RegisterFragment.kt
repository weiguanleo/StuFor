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
import com.example.stufor.databinding.FragmentRegisterBinding
import com.example.stufor.model.User
import com.example.stufor.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterFragment : Fragment() {

    companion object {
        const val TAG = "RegisterFragment"
    }

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val passwordRegex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textViewLogin.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.authFragmentContainer, LoginFragment())
                .commit()
        }

        binding.btnSignUp.setOnClickListener {
            val email = binding.editTextEmail.editText?.text.toString()
            val username = binding.editTextUsername.editText?.text.toString()
            val password = binding.editTextPassword.editText?.text.toString()
            val confirmPassword = binding.editTextConfirmPassword.editText?.text.toString()

            /*
            Setting all input fields error to null
             */
            binding.editTextEmail.error = null
            binding.editTextUsername.error = null
            binding.editTextPassword.error = null
            binding.editTextConfirmPassword.error = null


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
            Check whether username field is empty or not?
             */

            if (TextUtils.isEmpty(username)) {
                binding.editTextUsername.error = "Username is required."
                return@setOnClickListener
            }

            /*
            Check whether password field is empty or not?
            if not then valid or not?
             */

            if (TextUtils.isEmpty(password)) {
                binding.editTextPassword.error = "Password is required."
                return@setOnClickListener
            }

            if (!password.matches(passwordRegex)) {
                binding.editTextPassword.error = "Password should contains minimum 8 character, at least one uppercase letter, one lowercase letter and one number."
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(confirmPassword)) {
                binding.editTextConfirmPassword.error = "Confirm Password is required."
                return@setOnClickListener
            }

            /*
            Check if password is not equal to confirmPassword
             */

            if (password != confirmPassword) {
                binding.editTextConfirmPassword.error = "Password do not match."
                return@setOnClickListener
            }

            /*
            Authentication for Firebase
             */

            val auth = FirebaseAuth.getInstance()

            /*
            Creating an user on firebase using email and password field
             */
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // creating object of User data class and passing the data to cloud firestore
                        val user = User(auth.currentUser?.uid!!, email, username)
                        // instance of firestore cloud storage
                        val firestore = FirebaseFirestore.getInstance().collection("Users")
                        // setting user's details in new collection "Users"
                        firestore.document(auth.currentUser?.uid!!).set(user)
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    startActivity(Intent(activity, MainActivity::class.java))
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Something went wrong, Please try again.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    Log.d(TAG, it.exception.toString())
                                }
                            }
                    } else {
                        Toast.makeText(
                            context,
                            "Something went wrong, Please try again.",
                            Toast.LENGTH_LONG
                        ).show()
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