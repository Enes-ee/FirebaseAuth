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
import com.enesproje.firebaseauth.LoginScreenParts.FacebookLogin
import com.enesproje.firebaseauth.LoginScreenParts.PrebuiltLogin
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
import com.facebook.login.widget.LoginButton
import java.util.*


class LoginScreen : Fragment() {

    private var _binding : FragmentLoginScreenBinding? = null
    private val binding get() = _binding!!

    private lateinit var callbackManager : CallbackManager
//    private lateinit var prebuiltLogin : PrebuiltLogin
    private val auth = Firebase.auth



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentLoginScreenBinding.inflate(inflater,container,false)

        initRegisterScreenListener()

        val facebookLogin = FacebookLogin(this,binding)

        callbackManager = CallbackManager.Factory.create()

        facebookLogin.initFacebookLogin(callbackManager)

        initFacebookButtonListener()

//        Firebase tarafından sağlanan hazır giriş ekranı
//        prebuiltLogin = PrebuiltLogin(this)
//
//        prebuiltLogin.loadPreBuiltLoginScreen()

        return binding.root
    }

    private fun initFacebookButtonListener() {

        binding.bgFacebook.setOnClickListener {

            binding.facebookButton.performClick()


        }
    }

    private fun initRegisterScreenListener() {

        binding.tvCreateAccount.setOnClickListener {

            it.findNavController().navigate(LoginScreenDirections.actionLoginScreenToRegisterScreen())

            Log.e("NOT","register screen aktif")

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

//        Firebase tarafından sağlanan hazır giriş ekranınından dönen sonuç
//        prebuiltLogin.preBuiltLoginScreenResult(requestCode,resultCode,data)

        callbackManager.onActivityResult(requestCode,resultCode,data)

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}