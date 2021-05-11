package com.enesproje.firebaseauth.LoginScreenParts

import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.enesproje.firebaseauth.LoginScreen
import com.enesproje.firebaseauth.databinding.FragmentLoginScreenBinding
import com.facebook.*
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class FacebookLogin(var fragment : Fragment, var binding : FragmentLoginScreenBinding) {

    private val TAG = "FacebookLogin"
    private var auth = Firebase.auth

        fun build(callbackManager: CallbackManager){

            initFacebookLogin(callbackManager)

            initFacebookButtonListener()

        }

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
                        (fragment as LoginScreen).successfulLoginNavigation("Facebook")

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

        fun initFacebookButtonListener() {

            binding.bgFacebook.setOnClickListener {

                var profile : Profile? = Profile.getCurrentProfile()

                Log.e("Current Profile","$profile")

                if (profile != null){

                    val accessToken = AccessToken.getCurrentAccessToken()

                    handleFacebookAccessToken(accessToken)

                }else{

                    binding.facebookButton.performClick()

                }

            }
        }

}