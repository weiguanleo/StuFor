package com.example.stufor

import android.app.Activity
import android.util.Log
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.stufor.auth.AuthenticationActivity
import com.example.stufor.databinding.FragmentMyaccountBinding
import com.example.stufor.model.User
import com.example.stufor.util.UserUtil
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class ProfileFragment: Fragment() {

    private var _binding: FragmentMyaccountBinding? = null
    private val binding get() = _binding!!

    private var imageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyaccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textViewUsername.setText(UserUtil.user?.username)
        binding.editTextBio.setText(UserUtil.user?.bio)

        binding.imageViewProfilePicture.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start()
        }

        Glide.with(requireContext())
            .load(UserUtil.user?.imageUrl)
            .placeholder(R.drawable.person_icon_black)
            .centerCrop()
            .into(binding.imageViewProfilePicture)

        binding.editTextBio.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val userDocument = FirebaseFirestore.getInstance().collection("Users")
                    .document(UserUtil.user?.id!!)

                val user = User(
                    id = UserUtil.user?.id!!,
                    email = UserUtil.user?.email!!,
                    username = UserUtil.user?.username!!,
                    following = UserUtil.user?.following!!,
                    bio = s.toString(),
                    imageUrl = UserUtil.user?.imageUrl!!
                )

                userDocument.set(user)
                UserUtil.getCurrentUser()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        binding.btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            context?.startActivity(Intent(activity, AuthenticationActivity::class.java))
            activity?.finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            val fileUri = data?.data
            binding.imageViewProfilePicture.setImageURI(fileUri)
            imageUri = fileUri
            addUserImage()
        } else if (requestCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(context, ImagePicker.getError(data), Toast.LENGTH_LONG)
                .show()
        } else {
            Toast.makeText(context, "Something went wrong", Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun addUserImage() {
        val firestore = FirebaseFirestore.getInstance()

        firestore.collection("Users")
            .document(UserUtil.user?.id!!)
            .get().addOnCompleteListener {
                val storage = FirebaseStorage.getInstance().reference.child("Images")
                    .child(UserUtil.user?.email.toString() + "_" + System.currentTimeMillis() + ".jpg")

                val uploadTask = storage.putFile(imageUri!!)

                uploadTask.continueWithTask { task ->
                    if (!task.isSuccessful) {
                        Log.d("Upload Task", task.exception.toString())
                    }
                    storage.downloadUrl
                }.addOnCompleteListener { urlTaskCompleted ->
                    if (urlTaskCompleted.isSuccessful) {
                        val downloadUri = urlTaskCompleted.result

                        val newUser = User(
                            id = UserUtil.user?.id!!,
                            username = UserUtil.user?.username!!,
                            email = UserUtil.user?.email!!,
                            following = UserUtil.user?.following!!,
                            bio = UserUtil.user?.bio!!,
                            imageUrl = downloadUri.toString()
                        )

                        firestore.collection("Users").document(UserUtil.user?.id!!).set(newUser)
                            .addOnCompleteListener { saved ->
                                if (saved.isSuccessful) {
                                    UserUtil.getCurrentUser()
                                    Toast.makeText(context, "Image Uploaded", Toast.LENGTH_LONG)
                                        .show()
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Something went wrong. Please try again.",
                                        Toast.LENGTH_LONG
                                    )
                                        .show()
                                }
                            }
                    } else {
                        Toast.makeText(context, "Something went wrong.", Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
    }
}