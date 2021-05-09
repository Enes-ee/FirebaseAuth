package com.enesproje.firebaseauth.LoginScreenParts

import android.app.AlertDialog
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.enesproje.firebaseauth.LoginScreen
import com.enesproje.firebaseauth.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class EmailLogin(var fragment : Fragment) {

    private val TAG = "EmailLogin"
    private var auth = Firebase.auth


    fun createNewDialog(){

        val dialogBuilder = AlertDialog.Builder(fragment.context)
        val popupView = fragment.layoutInflater.inflate(R.layout.fragment_email_login,null)
        val tv_email : EditText = popupView.findViewById(R.id.loginEmail)
        val tv_password : EditText = popupView.findViewById(R.id.loginPassword)
        val loginButton : Button = popupView.findViewById(R.id.login_button)
        var dialog : AlertDialog? = null

        loginButton.setOnClickListener {

            val email = tv_email.text.toString()
            val password = tv_password.text.toString()

            Log.e(TAG,"email : $email , password : $password")

            val validate = loggingConditions(email,password)

            if(validate == true) {

                commenceEmailLogin(email,password)
                dialog!!.dismiss()

            }

        }

        dialogBuilder.setView(popupView)
        dialog = dialogBuilder.create()
        dialog.show()

    }

    private fun loggingConditions(email : String , password : String): Boolean {
        var stage: Int = 1

        while(stage < 3){

            when(stage){

                1 -> if (password.length >= 6) stage++ else {
                    Toast.makeText(fragment.context, "You should check your password. It can not be less than 6 characters.", Toast.LENGTH_SHORT).show()
                    break
                }

                2 -> {
                    val regex = Regex(""".+@\w+\.\w+""")
                    val sonuc = regex.matchEntire(email!!)
                    if (!sonuc?.value.isNullOrEmpty()) stage++ else {
                        Toast.makeText(fragment.context, "You have entered an invalid e-mail adress", Toast.LENGTH_SHORT).show()
                        break
                    }
                }
            }
        }

        return stage == 3
    }



    fun commenceEmailLogin(email : String , password: String){

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(fragment.requireActivity()) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.e(TAG, "signInWithEmail:success")
                        (fragment as LoginScreen).successfulLoginNavigation("Email")

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.e(TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(fragment.context, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                    }
                }

    }

}