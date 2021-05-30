package com.enesproje.firebaseauth.dialog_fragments

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
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

        roundCornersofDialog()

        binding.fpSendButton.setOnClickListener {

            val email = binding.fpEmail.text.toString()

            if (emailConditions(email)) {
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

        }

        return binding.root
    }

    fun roundCornersofDialog(){

        if (dialog != null && dialog!!.window != null) {

            dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog!!.window!!.requestFeature(Window.FEATURE_NO_TITLE)

        }

    }

    private fun emailConditions(email: String) : Boolean{

        if (!email.isNullOrEmpty()) {

            val regex = Regex(""".+@\w+\.\w+""")
            val sonuc = regex.matchEntire(email)
            if (!sonuc?.value.isNullOrEmpty()) {
                return true
            } else {
                Toast.makeText(this.context, "You have entered an unvalid e-mail adress.", Toast.LENGTH_SHORT).show()
                return false
            }
        }

        Toast.makeText(this.context, "You have to enter an e-mail adress.", Toast.LENGTH_SHORT).show()
        return false

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}