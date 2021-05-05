package com.enesproje.firebaseauth.LoginScreenParts

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.enesproje.firebaseauth.databinding.FragmentEmailLoginBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class EmailLoginFragment : Fragment() {

    private val TAG = "EmailLogin"
    private var auth = Firebase.auth
    private var _binding : FragmentEmailLoginBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        _binding = FragmentEmailLoginBinding.inflate(inflater,container,false)

        binding.loginButton.setOnClickListener {

            var email = binding.loginEmail.text?.toString()
            var password = binding.loginPassword.text?.toString()

            var validate = loggingConditions(email,password)

            if(validate == true) {

                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this.requireActivity()) { task ->
                            if (task.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success")
                                val user = auth.currentUser
                                this.findNavController().navigate(EmailLoginFragmentDirections.actionEmailLoginFragmentToTempMainScreen())

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.exception)
                                Toast.makeText(this.context, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show()
                            }
                        }
            }

        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun loggingConditions(email : String? , password : String?): Boolean {

        if( password.isNullOrEmpty() == false && password.length >= 6 ){

            //email için regex kullanmak gerekecek

            return true
        }else{
            Toast.makeText(this.context,"Conditions aren't met",Toast.LENGTH_SHORT).show()
            return false
        }
    }









}