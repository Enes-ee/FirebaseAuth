package com.enesproje.firebaseauth.login_screen_components

import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController
import com.enesproje.firebaseauth.LoginScreen
import com.enesproje.firebaseauth.LoginScreenDirections
import com.enesproje.firebaseauth.databinding.FragmentLoginScreenBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AnonymousLogin (val fragment : LoginScreen, val binding : FragmentLoginScreenBinding) {

    val auth = Firebase.auth
    val TAG = "InfoEE_AnonymousLogin"
    val isAnonymous = Firebase.auth.currentUser?.isAnonymous

    init {

        binding.anonymousLoginButton.setOnClickListener {

            if (isAnonymous == true) {

                fragment.findNavController().navigate(LoginScreenDirections.actionLoginScreenToTempMainScreen())

            }else {

                initAnonymousLogin()

            }

        }

    }

    private fun initAnonymousLogin() {

        auth.signInAnonymously()
                .addOnCompleteListener(fragment.requireActivity()) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.e(TAG, "signInAnonymously:success")
                        val user = auth.currentUser
                        findNavController(fragment).navigate(LoginScreenDirections.actionLoginScreenToTempMainScreen())

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.e(TAG, "signInAnonymously:failure", task.exception)
                        Toast.makeText(fragment.context, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()

                    }
                }
    }


}