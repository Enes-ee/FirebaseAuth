package com.enesproje.firebaseauth.create_account_components

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.enesproje.firebaseauth.R
import com.enesproje.firebaseauth.databinding.FragmentCreateAccountBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth

import com.google.firebase.ktx.Firebase


class GoogleLinkLogin(val fragment: Fragment, val binding: FragmentCreateAccountBinding){

    val auth = Firebase.auth
    val gso : GoogleSignInOptions
    val mGoogleSignInClient : GoogleSignInClient
    private val TAG = "InfoEE_GoogleLinkLogin"


    init {

        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(fragment.getString(R.string.googleApiClientId))
                .requestEmail()
                .build()

        mGoogleSignInClient = GoogleSignIn.getClient(fragment.requireActivity(), gso)

        initGoogleLogin()

    }

    fun initGoogleLogin(){

        binding.googleLoginButton2.setOnClickListener {

            signIn()

        }
    }

    private fun signIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        fragment.startActivityForResult(signInIntent, 100)
    }


    fun firebaseLinkToAccount(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        auth.currentUser!!.linkWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.e(TAG, "linkWithCredential:success")
                    val user = task.result?.user

                } else {
                    Log.e(TAG, "linkWithCredential:failure", task.exception)
                    firebaseSignIn(credential)

                }

            }
    }

    fun firebaseSignIn(credential : AuthCredential){

        auth.signInWithCredential(credential)
            .addOnCompleteListener(fragment.requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.e(TAG, "signInWithCredential:success")
                    Toast.makeText(fragment.requireContext(), """This google account has already linked with a user , 
                        |Original account has been loaded""".trimMargin(),
                        Toast.LENGTH_SHORT).show()
                    //updateUI(user)

                } else {
                    // If sign in fails, display a message to the user.
                    Log.e(TAG, "signInWithCredential:failure", task.exception)
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
                firebaseLinkToAccount(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.e(TAG, "Google sign in failed", e)
                // ...
            }
        }

    }


}