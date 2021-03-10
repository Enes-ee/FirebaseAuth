package com.enesproje.firebaseauth.LoginScreenParts

import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.enesproje.firebaseauth.databinding.FragmentLoginScreenBinding
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class FacebookLogin(var fragment : Fragment, var binding : FragmentLoginScreenBinding) {

    private val TAG = "FacebookLogin"
    private var auth = Firebase.auth

        fun initFacebookLogin(callbackManager: CallbackManager) {

            binding.facebookButton.setFragment(fragment)
            binding.facebookButton.setPermissions(listOf("email", "public_profile"))
            binding.facebookButton.registerCallback(callbackManager, object :
                FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    Log.e(TAG, "facebook:onSuccess:$loginResult")
                    handleFacebookAccessToken(loginResult.accessToken)
                }

                override fun onCancel() {
                    Log.e(TAG, "facebook:onCancel")
                    // ...
                }

                override fun onError(error: FacebookException) {
                    Log.e(TAG, "facebook:onError", error)
                    // ...
                }
            })
        }

        fun handleFacebookAccessToken(token: AccessToken?) {
            Log.e(TAG, "handleFacebookAccessToken:$token")

            val credential = FacebookAuthProvider.getCredential(token!!.token)
            auth.signInWithCredential(credential)
                .addOnCompleteListener() { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.e(TAG, "signInWithCredential:success")
                        val user = auth.currentUser

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.e(TAG, "signInWithCredential:failure", task.exception)
                        Toast.makeText(
                            fragment.activity, "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    // ...
                }
        }

}