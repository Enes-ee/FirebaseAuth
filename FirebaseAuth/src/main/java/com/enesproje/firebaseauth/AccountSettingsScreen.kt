package com.enesproje.firebaseauth


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import com.enesproje.firebaseauth.databinding.FragmentAccountSettingsScreenBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthProvider
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AccountSettingsScreen : Fragment() {

    var _binding : FragmentAccountSettingsScreenBinding? = null
    val binding get() = _binding!!

    val TAG = "AccountSettings"

    val user = Firebase.auth.currentUser

    var anyChanges = mutableMapOf("email" to false , "username" to false, "password" to false)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        _binding = FragmentAccountSettingsScreenBinding.inflate(inflater,container,false)

        initChangePasswordFields()

        checkEmailVerification()

        setInformation()

        saveButtonVisibility()

        saveChanges()


        return binding.root
    }



    private fun setInformation() {

        binding.settingsEmail.hint = user.email


    }

    private fun saveChanges() {

        binding.buttonSave.setOnClickListener {

            anyChanges = mutableMapOf("email" to false , "username" to false, "password" to false)

            emailConditions(binding.settingsEmail.text.toString())
            usernameConditions(binding.settingsUsername.text.toString())
            passwordConditions(binding.settingsPassword.text.toString(), binding.settingsPassword2.text.toString())

            for (eachItem in anyChanges){

                if (eachItem.value == true){

                    when (eachItem.key){

                        "email" -> { user!!.updateEmail(binding.settingsEmail.text.toString())
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        setInformation()
                                        checkEmailVerification()
                                        binding.settingsEmail.text.clear()
                                        Log.e(TAG, "User email address updated.")
                                    }
                                }}

                        "username" -> {}

                        "password" -> {user!!.updatePassword(binding.settingsPassword.text.toString())
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Log.e(TAG, "User password updated.")
                                        binding.settingsPassword.visibility = View.GONE
                                        binding.settingsPassword2.visibility = View.GONE
                                        binding.buttonChangePassword.visibility = View.GONE
                                    }
                                }}

                    }

                    Toast.makeText(this.context,"Changes have been saved.",Toast.LENGTH_SHORT).show()

                }

            }

        }


    }

    private fun emailConditions(newEmail: String){

        if (!newEmail.isNullOrEmpty()) {

            val regex = Regex(""".+@\w+\.\w+""")
            val sonuc = regex.matchEntire(newEmail)
            if (!sonuc?.value.isNullOrEmpty()) {
                anyChanges["email"] = true
            } else {
                Toast.makeText(this.context, "Entered e-mail adress does not meet requirements", Toast.LENGTH_SHORT).show()
            }
        }

    }




    private fun usernameConditions(newUsername: String){

        if (newUsername.length >= 3)
            anyChanges["username"] = true

    }

    private fun passwordConditions(newPassword: String, newPassword2: String) {

        if (!newPassword.isNullOrEmpty()) {

            var stage: Int = 1

            while (stage < 3) {

                when (stage) {

                    1 -> if (newPassword.length >= 6) stage++ else {
                        Toast.makeText(this.context, "Password length is less than 6 characters", Toast.LENGTH_SHORT).show()
                        break
                    }
                    2 -> if (newPassword == newPassword2) stage++ else {
                        Toast.makeText(this.context, "Entered passwords do not match", Toast.LENGTH_SHORT).show()
                        break
                    }
                }
            }

            if (stage == 3) {
                anyChanges["password"] = true
            }
        }
    }


    private fun initChangePasswordFields() {

        binding.buttonChangePassword.setOnClickListener {

            it.visibility = View.GONE
            binding.settingsPassword.visibility = View.VISIBLE
            binding.settingsPassword2.visibility = View.VISIBLE

        }

    }

    private fun saveButtonVisibility(){

        val componentList = mutableListOf<EditText>(binding.settingsEmail , binding.settingsUsername , binding.settingsPassword , binding.settingsPassword2)

        for (eachComponent in componentList){

            eachComponent.addTextChangedListener {

                if (eachComponent.text.toString().length == 1) {

                    binding.buttonSave2.visibility = View.VISIBLE

                    val saveButtonAnimation = AnimationUtils.loadAnimation(this.context,R.anim.save_button)
                    binding.buttonSave2.startAnimation(saveButtonAnimation)



                } else if (eachComponent.text.isNullOrEmpty()) {

                    binding.buttonSave2.visibility = View.GONE

                }

            }

        }

    }

    private fun checkEmailVerification() {


        if (!user.email.isNullOrEmpty()) {

            val isEmailVerified = user.isEmailVerified

            if (!isEmailVerified) {

                binding.buttonVerifyEmail.visibility = View.VISIBLE
                binding.tvVerifyEmail.visibility = View.VISIBLE

                binding.buttonVerifyEmail.setOnClickListener {

                    verifyEmail()

                }

            }

        }

    }

    private fun verifyEmail() {

        user!!.sendEmailVerification()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.e(TAG, "Verification email sent.")
                        Toast.makeText(this.context,"A verification e-mail has been sent to your e-mail box.",Toast.LENGTH_SHORT).show()
                    }
                }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}