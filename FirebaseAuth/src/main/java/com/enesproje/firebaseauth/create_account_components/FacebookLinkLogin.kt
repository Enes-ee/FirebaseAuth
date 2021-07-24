package com.enesproje.firebaseauth.create_account_components

import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.enesproje.firebaseauth.LoginScreen
import com.enesproje.firebaseauth.databinding.FragmentCreateAccountBinding
import com.enesproje.firebaseauth.databinding.FragmentLoginScreenBinding
import com.facebook.*
import com.facebook.login.LoginResult
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class FacebookLinkLogin(var fragment : Fragment, var binding : FragmentCreateAccountBinding) {

    private val TAG = "FacebookLinkLogin"
    private var auth = Firebase.auth

        fun build(callbackManager: CallbackManager){

            initFacebookLogin(callbackManager)

            initFacebookButtonListener()

        }

        fun initFacebookLogin(callbackManager: CallbackManager) {

            binding.facebookButton2.fragment = fragment
            binding.facebookButton2.setPermissions(listOf("email", "public_profile"))
            binding.facebookButton2.registerCallback(callbackManager, object :
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

        fun initFacebookButtonListener() {

            binding.facebookLoginButton2.setOnClickListener {

                var profile : Profile? = Profile.getCurrentProfile()

                Log.e("Current Profile","$profile")

                if (profile != null){

                    val accessToken = AccessToken.getCurrentAccessToken()

                    handleFacebookAccessToken(accessToken)

                }else{

                    binding.facebookButton2.performClick()

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

}