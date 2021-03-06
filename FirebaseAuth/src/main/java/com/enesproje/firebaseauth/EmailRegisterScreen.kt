package com.enesproje.firebaseauth

import android.os.Bundle
import android.text.format.DateUtils.LENGTH_MEDIUM
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.enesproje.firebaseauth.databinding.FragmentEmailRegisterScreenBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class EmailRegisterScreen : Fragment() {

    private var _binding : FragmentEmailRegisterScreenBinding? = null
    private val binding get() = _binding!!
    private var auth = Firebase.auth
    private var TAG = "InfoEE_EmailRegisterScreen"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentEmailRegisterScreenBinding.inflate(inflater,container,false)

        binding.submit.setOnClickListener{

            val newAccountEmail = binding.registerEmail.text.toString()
            val newAccountPassword = binding.registerPassword.text.toString()
            val newAccountPassword2 = binding.registerPassword2.text.toString()

            val validity = registeringConditions(newAccountEmail,newAccountPassword,newAccountPassword2)

            if(validity) registerToFirebase(newAccountEmail,newAccountPassword)

        }

        return binding.root
    }

    private fun registeringConditions(newAccountEmail: String, newAccountPassword: String, newAccountPassword2: String): Boolean {
        var stage: Int = 1

        while(stage < 4){

            when(stage){

                1 -> if (newAccountPassword.length >= 6) stage++ else {
                    Toast.makeText(this.context, "Password length is less than 6 characters", Toast.LENGTH_SHORT).show()
                    break
                }
                2 -> if (newAccountPassword == newAccountPassword2) stage++ else {
                    Toast.makeText(this.context, "Entered passwords do not match", Toast.LENGTH_SHORT).show()
                    break
                }
                3 -> {
                    val regex = Regex(""".+@\w+\.\w+""")
                    val sonuc = regex.matchEntire(newAccountEmail)
                    if (!sonuc?.value.isNullOrEmpty()) stage++ else {
                        Toast.makeText(this.context, "Entered e-mail address does not meet requirements", Toast.LENGTH_SHORT).show()
                        break
                    }
                }
            }
        }

        return stage == 4
    }




    private fun registerToFirebase(email : String , password : String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this.requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.e(TAG, "createUserWithEmail:success")
                    Toast.makeText(this.context,"You have registered successfully",Toast.LENGTH_SHORT).show()
                    this.findNavController().navigate(EmailRegisterScreenDirections.actionEmailRegisterScreenToTempMainScreen())
                    //updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.e(TAG, "createUserWithEmail:failure :", task.exception)

                    Toast.makeText(this.context, task.exception?.message,
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