package com.enesproje.firebaseauth.login_screen_components

import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController
import com.enesproje.firebaseauth.LoginScreen
import com.enesproje.firebaseauth.LoginScreenDirections
import com.enesproje.firebaseauth.databinding.FragmentLoginScreenBinding
import com.facebook.*
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.facebook.AccessToken




class FacebookLogin(var fragment : LoginScreen, var binding : FragmentLoginScreenBinding) {

    private val TAG = "InfoEE_FacebookLogin"
    private var auth = Firebase.auth
    private val isAnonymous = Firebase.auth.currentUser?.isAnonymous

        fun build(callbackManager: CallbackManager){

            initFacebookLogin(callbackManager)

            initFacebookButtonListener()

        }

        private fun initFacebookLogin(callbackManager: CallbackManager) {

            binding.facebookButton.fragment = fragment
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

            if (isAnonymous == true) {

                linkToAnonymous(token)

            } else {

                auth.signInWithCredential(credential)
                    .addOnCompleteListener() { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.e(TAG, "signInWithCredential:success")
                            findNavController(fragment).navigate(LoginScreenDirections.actionLoginScreenToTempMainScreen())

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

        private fun initFacebookButtonListener() {

            binding.facebookLoginButton.setOnClickListener {

                val accessToken = AccessToken.getCurrentAccessToken()
                val isLoggedIn = accessToken != null && !accessToken.isExpired

                val profile : Profile? = Profile.getCurrentProfile()

                Log.e(TAG,"Current Profile : $profile")

                if (isLoggedIn) {

                    handleFacebookAccessToken(accessToken)

                } else {

                    binding.facebookButton.performClick()

                }

            }
        }

    private fun linkToAnonymous (token: AccessToken?) {
        Log.e(TAG, "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token!!.token)
        auth.currentUser!!.linkWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.e(TAG, "linkWithCredential:success")
                    Toast.makeText(fragment.requireContext() , "You registered with your facebook account successfully.",Toast.LENGTH_SHORT).show()
                    findNavController(fragment).navigate(LoginScreenDirections.actionLoginScreenToTempMainScreen())
                    val user = task.result?.user

                } else {
                    Log.e(TAG, "linkWithCredential:failure", task.exception)
                    Toast.makeText(fragment.requireContext(), """This facebook account is registered as different user, 
                        |You are still anonymous""".trimMargin(),
                        Toast.LENGTH_SHORT).show()

                }

            }
    }

}