package com.enesproje.firebaseauth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.enesproje.firebaseauth.databinding.FragmentForgottenPasswordBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ForgottenPassword : DialogFragment() {

    var _binding : FragmentForgottenPasswordBinding? = null
    val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        _binding = FragmentForgottenPasswordBinding.inflate(inflater,container,false)

        binding.fpSendButton.setOnClickListener {

            val email = binding.fpEmail.text.toString()
            Firebase.auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            this.context,
                            "A reset mail has been sent to you e-mail box",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}