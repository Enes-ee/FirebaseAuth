package com.enesproje.firebaseauth.login_screen_components

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController
import com.enesproje.firebaseauth.LoginScreen
import com.enesproje.firebaseauth.LoginScreenDirections
import com.enesproje.firebaseauth.R
import com.enesproje.firebaseauth.databinding.FragmentLoginScreenBinding
import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth

import com.google.firebase.ktx.Firebase


class GoogleLogin(val fragment: LoginScreen, val binding: FragmentLoginScreenBinding){

    val auth = Firebase.auth
    val gso : GoogleSignInOptions
    val isAnonymous = Firebase.auth.currentUser?.isAnonymous

    private val mGoogleSignInClient : GoogleSignInClient
    private val TAG = "InfoEE_GoogleLogin"


    init {

        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(fragment.getString(R.string.googleApiClientId))
                .requestEmail()
                .build()

        mGoogleSignInClient = GoogleSignIn.getClient(fragment.requireActivity(), gso)

        initGoogleLogin()

    }

    private fun initGoogleLogin(){

        binding.googleLoginButton.setOnClickListener {

            signIn()

        }
    }

    private fun signIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        fragment.startActivityForResult(signInIntent, 100)
    }


    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
                .addOnCompleteListener(fragment.requireActivity()) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.e(TAG, "signInWithCredential:success")
                        //updateUI(user)
                        findNavController(fragment).navigate(LoginScreenDirections.actionLoginScreenToTempMainScreen())

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

                if (isAnonymous == true){
                    linkToAnonymous(account.idToken!!)
                }else{
                    firebaseAuthWithGoogle(account.idToken!!)
                }


            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.e(TAG, "Google sign in failed", e)
                // ...
            }
        }

    }

    private fun linkToAnonymous (idToken : String) {

        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.currentUser!!.linkWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.e(TAG, "linkWithCredential:success")
                    Toast.makeText(fragment.requireContext() , "You registered with your google account successfully.",
                        Toast.LENGTH_SHORT).show()
                    findNavController(fragment).navigate(LoginScreenDirections.actionLoginScreenToTempMainScreen())
                    val user = task.result?.user

                } else {
                    Log.e(TAG, "linkWithCredential:failure", task.exception)
                    Toast.makeText(fragment.requireContext(), """This google account is registered as different user, 
                        |You are still anonymous""".trimMargin(),
                        Toast.LENGTH_SHORT).show()

                }

            }
    }

}