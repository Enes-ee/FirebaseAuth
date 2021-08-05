package com.enesproje.firebaseauth.dialog_fragments

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.enesproje.firebaseauth.databinding.FragmentForgottenPasswordBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ForgottenPassword : DialogFragment() {

    var _binding : FragmentForgottenPasswordBinding? = null
    val binding get() = _binding!!

    val TAG = "InfoEE_Password"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        _binding = FragmentForgottenPasswordBinding.inflate(inflater,container,false)

        roundCornersofDialog()

        setComponents()

        return binding.root
    }

    private fun setComponents() {

        binding.fpSendButton.setOnClickListener {

            val email = binding.fpEmail.text.toString()

            if (emailConditions(email)) {
                Firebase.auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->

                        if (task.isSuccessful) {
                            Toast.makeText(
                                this.context,
                                "A reset mail has been sent to your e-mail box",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else{

                            Toast.makeText(
                                this.context,
                                task.exception?.message,
                                Toast.LENGTH_SHORT
                            ).show()

                        }
                    }
            }

        }
    }

    private fun roundCornersofDialog(){

        if (dialog != null && dialog!!.window != null) {

            dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog!!.window!!.requestFeature(Window.FEATURE_NO_TITLE)

        }

    }

    private fun emailConditions(email: String) : Boolean {

        if (email.isNotEmpty()) {

            val regex = Regex(""".+@\w+\.\w+""")
            val sonuc = regex.matchEntire(email)
            return if (!sonuc?.value.isNullOrEmpty()) {
                true
            } else {
                Toast.makeText(this.context, "You have entered an invalid e-mail adress.", Toast.LENGTH_SHORT).show()
                false
            }
        }

        Toast.makeText(this.context, "You have to enter an e-mail address.", Toast.LENGTH_SHORT).show()
        return false

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}