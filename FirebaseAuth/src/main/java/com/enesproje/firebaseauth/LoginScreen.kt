package com.enesproje.firebaseauth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
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
    private val isAnonymous = Firebase.auth.currentUser?.isAnonymous

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

        //Anonymous Login
        anonymousLogin = AnonymousLogin(this,binding)

        setComponents()

        inCaseOfAnonymousAccess()




//        Firebase tarafından sağlanan hazır giriş ekranı
//        PrebuiltLogin(this).loadPreBuiltLoginScreen()

                return binding.root
    }

    private fun setComponents() {

        binding.tvForgotPassword.setOnClickListener {

            findNavController().navigate(LoginScreenDirections.actionLoginScreenToForgottenPassword())

        }

        binding.emailLoginButton.setOnClickListener {

            findNavController().navigate(LoginScreenDirections.actionLoginScreenToEmailLogin())

        }

        binding.tvCreateAccount.setOnClickListener {

            findNavController().navigate(LoginScreenDirections.actionLoginScreenToEmailRegisterScreen())

        }

        requireActivity().onBackPressedDispatcher.addCallback(this){

            findNavController().navigate(LoginScreenDirections.actionLoginScreenToLeaveApp())

        }

    }

    private fun inCaseOfAnonymousAccess() {

        if (isAnonymous == true) {

            binding.anonymousLoginButton.text = getString(R.string.continue_as_anonymous)

            binding.statusWarning!!.visibility = View.VISIBLE

        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

//        Firebase tarafından sağlanan hazır giriş ekranınından dönen sonuç
//        prebuiltLogin.preBuiltLoginScreenResult(requestCode,resultCode,data)

        callbackManager.onActivityResult(requestCode,resultCode,data)

        googleLogin.googleLoginCallBack(requestCode,data)

    }


    override fun onStart() {
        super.onStart()

        ForceUpdateChecker.with(this.requireActivity(),this).checkAsynchronous(60)

        if (user != null && !isAnonymous!!) {

            findNavController().navigate(LoginScreenDirections.actionLoginScreenToTempMainScreen())

        }

    }



    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}


