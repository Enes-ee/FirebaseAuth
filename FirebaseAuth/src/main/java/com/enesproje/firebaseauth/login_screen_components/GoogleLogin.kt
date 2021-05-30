package com.enesproje.firebaseauth.login_screen_components

import android.content.Intent
import android.util.Log
import androidx.fragment.app.Fragment
import com.enesproje.firebaseauth.LoginScreen
import com.enesproje.firebaseauth.R
import com.enesproje.firebaseauth.databinding.FragmentLoginScreenBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth

import com.google.firebase.ktx.Firebase


class GoogleLogin(val fragment: Fragment, val binding: FragmentLoginScreenBinding){

    val auth = Firebase.auth
    val gso : GoogleSignInOptions
    val mGoogleSignInClient : GoogleSignInClient
    private val TAG = "GoogleLogin"


    init {

        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(fragment.getString(R.string.googleApiClientId))
                .requestEmail()
                .build()

        mGoogleSignInClient = GoogleSignIn.getClient(fragment.requireActivity(), gso)

        initGoogleLogin()

    }

    fun initGoogleLogin(){

        binding.googleLoginButton.setOnClickListener {

            signIn()

        }
    }

    private fun signIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        fragment.startActivityForResult(signInIntent, 100)
    }


    fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
                .addOnCompleteListener(fragment.requireActivity()) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.e(TAG, "signInWithCredential:success")
                        //updateUI(user)
                        (fragment as LoginScreen).successfulLoginNavigation()

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.e(TAG, "signInWithCredential:failure", task.exception)
                        // ...
                        Snackbar.make(
                            fragment.requireView(),
                            "Authentication Failed.",
                            Snackbar.LENGTH_SHORT
                        ).show()
                        //updateUI(null)
                    }
                    // ...
                }
    }

    fun googleLoginCallBack(requestCode : Int, data : Intent?){

        if (requestCode == 100) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.e(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.e(TAG, "Google sign in failed", e)
                // ...
            }
        }

    }

}