package com.enesproje.firebaseauth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.enesproje.firebaseauth.login_screen_components.FacebookLogin
import com.enesproje.firebaseauth.login_screen_components.GoogleLogin
import com.enesproje.firebaseauth.databinding.FragmentLoginScreenBinding
import com.enesproje.firebaseauth.login_screen_components.AnonymousLogin
import com.facebook.CallbackManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class LoginScreen : Fragment() {

    private var _binding : FragmentLoginScreenBinding? = null
    private val binding get() = _binding!!

    private lateinit var callbackManager : CallbackManager
//    private lateinit var prebuiltLogin : PrebuiltLogin

    private val user = Firebase.auth.currentUser

    private lateinit var googleLogin : GoogleLogin
    private lateinit var facebookLogin : FacebookLogin
    private lateinit var anonymousLogin: AnonymousLogin


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentLoginScreenBinding.inflate(inflater,container,false)

        callbackManager = CallbackManager.Factory.create()

        facebookLogin = FacebookLogin(this, binding)

        facebookLogin.build(callbackManager)

        //Google Login
        googleLogin = GoogleLogin(this, binding)

        //Email and password Login
        initEmailLogin()

        //Anonymous Login
        anonymousLogin = AnonymousLogin(this,binding)

        initForgottenPassword()

        initRegisterScreenListener()


//        Firebase tarafından sağlanan hazır giriş ekranı
//        PrebuiltLogin(this).loadPreBuiltLoginScreen()

                return binding.root
    }

    private fun initForgottenPassword() {

        binding.tvForgotPassword.setOnClickListener {

            it.findNavController().navigate(LoginScreenDirections.actionLoginScreenToForgottenPassword())

        }
    }

    private fun initEmailLogin() {

        binding.emailLoginButton.setOnClickListener {

            it.findNavController().navigate(LoginScreenDirections.actionLoginScreenToEmailLogin())

        }

    }


    private fun initRegisterScreenListener() {

        binding.tvCreateAccount.setOnClickListener {

            it.findNavController().navigate(LoginScreenDirections.actionLoginScreenToEmailRegisterScreen())

        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

//        Firebase tarafından sağlanan hazır giriş ekranınından dönen sonuç
//        prebuiltLogin.preBuiltLoginScreenResult(requestCode,resultCode,data)

        callbackManager.onActivityResult(requestCode,resultCode,data)

        googleLogin.googleLoginCallBack(requestCode,data)

    }




    fun successfulLoginNavigation() {

        this.findNavController().navigate(LoginScreenDirections.actionLoginScreenToTempMainScreen())

    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onStart() {
        super.onStart()

        ForceUpdateChecker.with(this.requireActivity(),this).checkAsynchronous(60)

        if (user != null){

            successfulLoginNavigation()

        }


    }


}


