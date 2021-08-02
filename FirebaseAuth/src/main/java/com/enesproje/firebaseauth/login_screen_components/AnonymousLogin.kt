package com.enesproje.firebaseauth.login_screen_components

import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.enesproje.firebaseauth.LoginScreen
import com.enesproje.firebaseauth.databinding.FragmentLoginScreenBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AnonymousLogin (val fragment:Fragment, val binding : FragmentLoginScreenBinding) {

    val auth = Firebase.auth
    val TAG = "InfoEE_AnonymousLogin"

    init {

        binding.anonymousLoginButton.setOnClickListener {

            initAnonymousLogin()

        }

    }

    private fun initAnonymousLogin() {

        auth.signInAnonymously()
                .addOnCompleteListener(fragment.requireActivity()) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.e(TAG, "signInAnonymously:success")
                        val user = auth.currentUser
                        (fragment as LoginScreen).successfulLoginNavigation()

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.e(TAG, "signInAnonymously:failure", task.exception)
                        Toast.makeText(fragment.context, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()

                    }
                }
    }


}