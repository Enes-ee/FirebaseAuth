package com.enesproje.firebaseauth.dialog_fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.enesproje.firebaseauth.databinding.FragmentCreateAccountBinding
import com.enesproje.firebaseauth.create_account_components.FacebookLinkLogin
import com.enesproje.firebaseauth.create_account_components.GoogleLinkLogin
import com.facebook.CallbackManager


class CreateAccount : Fragment(){

    var _binding : FragmentCreateAccountBinding? = null
    val binding get() = _binding!!

    val TAG = "CreateAccount"

    private lateinit var callbackManager : CallbackManager
    private lateinit var googleLinkLogin : GoogleLinkLogin
    private lateinit var facebookLinkLogin : FacebookLinkLogin

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCreateAccountBinding.inflate(inflater,container,false)

        setComponents()

        callbackManager = CallbackManager.Factory.create()

        facebookLinkLogin = FacebookLinkLogin(this, binding)

        facebookLinkLogin.build(callbackManager)

        //Google Login
        googleLinkLogin = GoogleLinkLogin(this, binding)


        return binding.root
    }

    private fun setComponents() {

        binding.emailLoginButton2.setOnClickListener {

            findNavController().navigate(CreateAccountDirections.actionCreateAccountToEmailRegisterScreen())

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        googleLinkLogin.googleLoginCallBack(requestCode,data)

        callbackManager.onActivityResult(requestCode,resultCode,data)

    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}