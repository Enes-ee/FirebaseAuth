package com.enesproje.firebaseauth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.enesproje.firebaseauth.LoginScreenParts.FacebookLogin
import com.enesproje.firebaseauth.LoginScreenParts.GoogleLogin
import com.enesproje.firebaseauth.databinding.FragmentLoginScreenBinding
import com.facebook.CallbackManager
import com.facebook.login.LoginManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class LoginScreen : Fragment() {

    private var _binding : FragmentLoginScreenBinding? = null
    private val binding get() = _binding!!

    private lateinit var callbackManager : CallbackManager
//    private lateinit var prebuiltLogin : PrebuiltLogin
    private val auth = Firebase.auth

    private lateinit var googleLogin : GoogleLogin
    private lateinit var facebookLogin : FacebookLogin

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentLoginScreenBinding.inflate(inflater,container,false)

        initRegisterScreenListener()

        callbackManager = CallbackManager.Factory.create()

        facebookLogin = FacebookLogin(this, binding)

        facebookLogin.build(callbackManager)

        //Google Login
        googleLogin = GoogleLogin(this, binding)

        //Email and password Login
        binding.bgEmail.setOnClickListener {

            this.findNavController().navigate(LoginScreenDirections.actionLoginScreenToEmailLoginFragment())

        }

//        Firebase tarafından sağlanan hazır giriş ekranı
//        PrebuiltLogin(this).loadPreBuiltLoginScreen()

                return binding.root
    }


    private fun initRegisterScreenListener() {

        binding.tvCreateAccount.setOnClickListener {

            it.findNavController().navigate(LoginScreenDirections.actionLoginScreenToEmailRegisterScreen())

            Log.e("NOT","register screen aktif")

        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

//        Firebase tarafından sağlanan hazır giriş ekranınından dönen sonuç
//        prebuiltLogin.preBuiltLoginScreenResult(requestCode,resultCode,data)

        callbackManager.onActivityResult(requestCode,resultCode,data)

        googleLogin.googleLoginCallBack(requestCode,data)

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


    fun successfulLoginNavigation() {

        this.findNavController().navigate(LoginScreenDirections.actionLoginScreenToTempMainScreen())

    }




}