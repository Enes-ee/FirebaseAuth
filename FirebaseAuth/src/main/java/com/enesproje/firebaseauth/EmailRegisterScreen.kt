package com.enesproje.firebaseauth

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.enesproje.firebaseauth.databinding.FragmentEmailRegisterScreenBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class EmailRegisterScreen : Fragment() {

    private var _binding : FragmentEmailRegisterScreenBinding? = null
    private val binding get() = _binding!!
    private var auth = Firebase.auth
    private var TAG = "REGISTER"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentEmailRegisterScreenBinding.inflate(inflater,container,false)

        var newAccountEmail = binding.registerEmail.text.toString()
        var newAccountPassword = binding.registerPassword.text.toString()
        var newAccountPassword2 = binding.registerPassword2.text.toString()

        binding.submit.setOnClickListener{

            val validity = registeringConditions(newAccountEmail,newAccountPassword,newAccountPassword2)

            if(validity == true) {
                registerToFirebase(newAccountEmail,newAccountPassword)
            }

        }

        return binding.root
    }

    private fun registeringConditions(newAccountEmail: String, newAccountPassword: String, newAccountPassword2: String): Boolean {

        if( newAccountPassword == newAccountPassword2 && newAccountPassword.length > 6 ){

            //email için regex kullanmak gerekecek

            return true
        }else{
            Toast.makeText(this.context,"Conditions aren't met",Toast.LENGTH_SHORT).show()
            return false
        }
    }


    private fun registerToFirebase(email : String , password : String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this.requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.e(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    //updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.e(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(this.context, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    //updateUI(null)
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}