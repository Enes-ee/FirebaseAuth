package com.enesproje.firebaseauth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.enesproje.firebaseauth.databinding.FragmentLoginScreenBinding
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.facebook.login.LoginManager
import java.util.*


class LoginScreen : Fragment() {

    //Firebase tarafından sağlanan hazır giriş ekranı için gerekiyor.
    val providers = arrayListOf(
        AuthUI.IdpConfig.EmailBuilder().build(),
        AuthUI.IdpConfig.GoogleBuilder().build(),
        AuthUI.IdpConfig.FacebookBuilder().build()
    )

    private var auth: FirebaseAuth = Firebase.auth
    private var _binding : FragmentLoginScreenBinding? = null
    private val binding get() = _binding!!

    //Facebook SDK
    private lateinit var callbackManager : CallbackManager
    private val FBTAG = "Facebook"


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentLoginScreenBinding.inflate(inflater,container,false)

        if (auth.currentUser != null){
            Toast.makeText(activity,"Hoşgeldin : ${LoginManager.getInstance()} ${auth.currentUser}",Toast.LENGTH_SHORT).show()
        }else {
            Toast.makeText(activity,"${LoginManager.getInstance()}",Toast.LENGTH_LONG).show()
        }

        initRegisterScreenListener()

        initFacebookLogin()

        initFacebookButtonListener()

        //Firebase tarafından sağlanan hazır giriş ekranı
        //loadPreBuiltLoginScreen()


        return binding.root
    }

    private fun initFacebookButtonListener() {
        binding.bgFacebook.setOnClickListener {

            binding.facebookButton.performClick()


        }
    }

    private fun initFacebookLogin() {

            callbackManager = CallbackManager.Factory.create()
            binding.facebookButton.setFragment(this)
            binding.facebookButton.setPermissions(listOf("email", "public_profile"))
            binding.facebookButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    Log.e(FBTAG, "facebook:onSuccess:$loginResult")
                    handleFacebookAccessToken(loginResult.accessToken)
                }

                override fun onCancel() {
                    Log.e(FBTAG, "facebook:onCancel")
                    // ...
                }

                override fun onError(error: FacebookException) {
                    Log.e(FBTAG, "facebook:onError", error)
                    // ...
                }
            })
        }

    private fun handleFacebookAccessToken(token: AccessToken?) {
        Log.e(FBTAG, "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token!!.token)
        auth.signInWithCredential(credential)
                .addOnCompleteListener() { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.e(FBTAG, "signInWithCredential:success")
                        val user = auth.currentUser

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.e(FBTAG, "signInWithCredential:failure", task.exception)
                        Toast.makeText(activity, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()

                    }

                    // ...
                }
    }

    private fun initRegisterScreenListener() {

        binding.tvCreateAccount.setOnClickListener {

            it.findNavController().navigate(LoginScreenDirections.actionLoginScreenToRegisterScreen())

            Log.e("NOT","register screen aktif")

        }

    }

    //Firebase tarafından sağlanan hazır giriş ekranı
    private fun loadPreBuiltLoginScreen(logo : Int? = null , theme : Int? = null) {

        if (logo != null && theme != null) {
            startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .setLogo(logo)
                    .setTheme(theme)
                    .build(), 1
            )
        }else if (theme != null){
            startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .setTheme(theme)
                    .build(), 1
            )
        }else if (logo != null){
            startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setLogo(logo)
                .build(),1
            )
        }else{
            startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(),1
            )
        }
    }

    //Firebase tarafından sağlanan hazır giriş ekranınından dönen sonuç
    private fun preBuiltLoginScreenResult(requestCode: Int, resultCode: Int, data: Intent?){
        if(requestCode == 1){
            val response = IdpResponse.fromResultIntent(data)
            if(resultCode == Activity.RESULT_OK){
                val user = FirebaseAuth.getInstance().currentUser
                Toast.makeText(activity,"Hoşgeldin ${user}",Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(
                        activity,
                        "Giriş başarısız. Hata kodu : ${response?.error?.errorCode}",
                        Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //Firebase tarafından sağlanan hazır giriş ekranınından dönen sonuç
        //preBuiltLoginScreenResult()

        callbackManager.onActivityResult(requestCode,resultCode,data)


    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}