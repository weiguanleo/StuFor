package com.example.stufor

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.stufor.auth.AuthenticationActivity
import com.example.stufor.databinding.FragmentMyaccountBinding
import com.example.stufor.model.User
import com.example.stufor.util.UserUtil
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

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
                    username = UserUtil.user?.username!!,
                    email = UserUtil.user?.email!!,
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
}