package com.enesproje.firebaseauth.LoginScreenParts

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.enesproje.firebaseauth.LoginScreen
import com.enesproje.firebaseauth.R
import com.enesproje.firebaseauth.databinding.FragmentEmailLoginBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class EmailLogin : DialogFragment() {

    private val TAG = "EmailLogin"
    private var auth = Firebase.auth
    private var _binding : FragmentEmailLoginBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        _binding = FragmentEmailLoginBinding.inflate(inflater,container,false)

        createNewDialog()

        roundCornersofDialog()

        return binding.root
    }

    fun createNewDialog(){

        binding.loginButton.setOnClickListener {

            val email = binding.loginEmail.text.toString()
            val password = binding.loginPassword.text.toString()

            Log.e(TAG,"email : $email , password : $password")

            val validate = loggingConditions(email,password)

            if(validate == true) {

                commenceEmailLogin(email,password)

            }

        }

    }

    fun commenceEmailLogin(email : String , password: String){

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this.requireActivity()) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.e(TAG, "signInWithEmail:success")
                        this.findNavController().navigate(EmailLoginDirections.actionEmailLoginToTempMainScreen())

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.e(TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(this.context, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                    }
                }

    }

    private fun loggingConditions(email : String , password : String): Boolean {
        var stage: Int = 1

        while(stage < 3){

            when(stage){

                1 -> if (password.length >= 6) stage++ else {
                    Toast.makeText(this.context, "You should check your password. It can not be less than 6 characters.", Toast.LENGTH_SHORT).show()
                    break
                }

                2 -> {
                    val regex = Regex(""".+@\w+\.\w+""")
                    val sonuc = regex.matchEntire(email)
                    if (!sonuc?.value.isNullOrEmpty()) stage++ else {
                        Toast.makeText(this.context, "You have entered an invalid e-mail adress", Toast.LENGTH_SHORT).show()
                        break
                    }
                }
            }
        }

        return stage == 3
    }

    fun roundCornersofDialog(){

        if (dialog != null && dialog!!.window != null) {

            dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog!!.window!!.requestFeature(Window.FEATURE_NO_TITLE)

        }

    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}