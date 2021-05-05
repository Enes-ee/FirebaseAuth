package com.enesproje.firebaseauth.LoginScreenParts

import android.app.AlertDialog
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.enesproje.firebaseauth.LoginScreenDirections
import com.enesproje.firebaseauth.R
import com.enesproje.firebaseauth.databinding.FragmentLoginScreenBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class EmailLogin(var fragment : Fragment,var binding : FragmentLoginScreenBinding) {

    private val TAG = "EmailLogin"
    private var auth = Firebase.auth
    private lateinit var dialogBuilder : AlertDialog.Builder
    private lateinit var dialog : AlertDialog
    private lateinit var tv_email : EditText
    private lateinit var email : String
    private lateinit var tv_password : EditText
    private lateinit var password : String
    private lateinit var loginButton : Button


    fun createNewDialog(){

        dialogBuilder = AlertDialog.Builder(fragment.context)
        var popupView = fragment.layoutInflater.inflate(R.layout.fragment_email_login,null)
        tv_email = popupView.findViewById(R.id.loginEmail)
        tv_password = popupView.findViewById(R.id.loginPassword)
        loginButton = popupView.findViewById(R.id.login_button)

        loginButton.setOnClickListener {

            email = tv_email.text.toString()
            password = tv_password.text.toString()

            Log.e(TAG,"email : $email , password : $password")

            var validate = loggingConditions(email,password)

            if(validate == true) {

                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(fragment.requireActivity()) { task ->
                            if (task.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.e(TAG, "signInWithEmail:success")
                                val user = auth.currentUser
                                dialog.dismiss()
                                fragment.findNavController().navigate(LoginScreenDirections.actionLoginScreenToTempMainScreen())

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.e(TAG, "signInWithEmail:failure", task.exception)
                                Toast.makeText(fragment.context, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show()
                            }
                        }
            }

        }

        dialogBuilder.setView(popupView)
        dialog = dialogBuilder.create()
        dialog.show()

    }

    private fun loggingConditions(email : String? , password : String?): Boolean {

        if( password.isNullOrEmpty() == false && password.length >= 6 ){

            //email için regex kullanmak gerekecek

            return true
        }else{
            Toast.makeText(fragment.context,"Conditions aren't met",Toast.LENGTH_SHORT).show()
            return false
        }
    }



}