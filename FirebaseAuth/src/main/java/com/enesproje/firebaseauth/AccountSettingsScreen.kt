package com.enesproje.firebaseauth

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.enesproje.firebaseauth.databinding.FragmentAccountSettingsScreenBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class AccountSettingsScreen : Fragment() {

    private var _binding: FragmentAccountSettingsScreenBinding? = null
    private val binding get() = _binding!!

    val TAG = "AccountSettings"

    val user = Firebase.auth.currentUser

    var anyChanges = mutableMapOf("email" to false, "username" to false, "password" to false)

    val database = Firebase.database.reference

    val userId = user!!.uid

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAccountSettingsScreenBinding.inflate(inflater, container, false)

        val editTextUsername = binding.settingsUsername
        val editTextEmail = binding.settingsEmail
        val editTextPassword = binding.settingsPassword
        val editTextPassword2 = binding.settingsPassword2

        initChangePasswordFields(editTextPassword, editTextPassword2)

        checkEmailVerification()

        setInformation(editTextUsername, editTextUsername)

        val componentList = mutableListOf<EditText>(
            editTextEmail,
            editTextUsername,
            editTextPassword,
            editTextPassword2
        )

        saveButtonVisibility(componentList)

        saveChanges(editTextUsername, editTextEmail, editTextPassword, editTextPassword2)


        return binding.root
    }


    private fun setInformation(editTextUsername: EditText, editTextEmail: EditText) {

        editTextEmail.hint = user?.email

        val usernameListener = object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                binding.settingsUsername.hint = snapshot.value.toString()

            }

            override fun onCancelled(error: DatabaseError) {

                Log.e(TAG, "Username catching failed.")

            }

        }

        database.child("Users").child(userId).child("Username")
            .addValueEventListener(usernameListener)

    }

    private fun saveChanges(
        editTextUsername: EditText,
        editTextEmail: EditText,
        editTextPassword: EditText,
        editTextPassword2: EditText
    ) {

        val buttonSave = binding.buttonSave

        buttonSave.setOnClickListener {

            anyChanges = mutableMapOf("email" to false, "username" to false, "password" to false)

            emailConditions(editTextEmail.text.toString())
            usernameConditions(editTextUsername.text.toString())
            passwordConditions(editTextPassword.text.toString(), editTextPassword2.text.toString())

            for (eachItem in anyChanges) {

                if (eachItem.value == true) {

                    when (eachItem.key) {

                        "email" -> {
                            user!!.updateEmail(editTextEmail.text.toString())
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        setInformation(editTextUsername, editTextEmail)
                                        checkEmailVerification()
                                        editTextEmail.text.clear()
                                        Log.e(TAG, "User email address updated.")
                                    }
                                }
                        }

                        "username" -> {
                            database.child("Users").child(userId).child("Username")
                                .setValue(editTextUsername.text.toString())
                            editTextUsername.text.clear()
                        }

                        "password" -> {
                            user!!.updatePassword(editTextPassword.text.toString())
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val buttonChangePassword = binding.buttonChangePassword
                                        Log.e(TAG, "User password updated.")
                                        editTextPassword.visibility = View.GONE
                                        editTextPassword2.visibility = View.GONE
                                        buttonChangePassword.visibility = View.GONE
                                    }
                                }
                        }

                    }

                    Toast.makeText(this.context, "Changes have been saved.", Toast.LENGTH_SHORT)
                        .show()

                }

            }

        }


    }

    private fun emailConditions(newEmail: String) {

        if (!newEmail.isEmpty()) {

            val regex = Regex(""".+@\w+\.\w+""")
            val sonuc = regex.matchEntire(newEmail)
            if (!sonuc?.value.isNullOrEmpty()) {
                anyChanges["email"] = true
            } else {
                Toast.makeText(
                    this.context,
                    "Entered e-mail adress does not meet requirements",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }


    private fun usernameConditions(newUsername: String) {

        if (newUsername.length >= 4) {
            anyChanges["username"] = true
        } else {
            Toast.makeText(
                this.context,
                "Entered username does not meet requirements",
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    private fun passwordConditions(newPassword: String, newPassword2: String) {

        if (!newPassword.isEmpty()) {

            var stage: Int = 1

            while (stage < 3) {

                when (stage) {

                    1 -> if (newPassword.length >= 6) stage++ else {
                        Toast.makeText(
                            this.context,
                            "Password length is less than 6 characters",
                            Toast.LENGTH_SHORT
                        ).show()
                        break
                    }
                    2 -> if (newPassword == newPassword2) stage++ else {
                        Toast.makeText(
                            this.context,
                            "Entered passwords do not match",
                            Toast.LENGTH_SHORT
                        ).show()
                        break
                    }
                }
            }

            if (stage == 3) {
                anyChanges["password"] = true
            }
        }
    }


    private fun initChangePasswordFields(editTextPassword: EditText, editTextPassword2: EditText) {

        val animPassword = AnimationUtils.loadAnimation(this.context, R.anim.fade_in_layout)

        val animButtonChangePassword =
            AnimationUtils.loadAnimation(this.context, R.anim.fade_out_layout)

        val buttonChangePassword = binding.buttonChangePassword

        buttonChangePassword.setOnClickListener {

            it.visibility = View.GONE

            it.startAnimation(animButtonChangePassword)

            editTextPassword.visibility = View.VISIBLE

            editTextPassword.startAnimation(animPassword)

            editTextPassword2.visibility = View.VISIBLE

            editTextPassword2.startAnimation(animPassword)

        }

    }

    private fun saveButtonVisibility(componentList: MutableList<EditText>) {


        val buttonSave = binding.buttonSave

        for (eachComponent in componentList) {

            eachComponent.addTextChangedListener {

                if (eachComponent.text.toString().length == 1) {

                    buttonSave.visibility = View.VISIBLE

                    val animation = AnimationUtils.loadAnimation(this.context, R.anim.fade_in)
                    buttonSave.startAnimation(animation)


                } else if (eachComponent.text.isNullOrEmpty()) {

                    buttonSave.visibility = View.GONE

                }

            }

        }

    }

    private fun checkEmailVerification() {


        if (!user!!.email.isNullOrEmpty()) {

            val isEmailVerified = user.isEmailVerified

            if (!isEmailVerified) {

                val buttonVerifyEmail = binding.buttonVerifyEmail
                val tvVerifyEmail = binding.tvVerifyEmail

                buttonVerifyEmail.visibility = View.VISIBLE
                tvVerifyEmail.visibility = View.VISIBLE

                tvVerifyEmail.setOnClickListener {

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
                    Toast.makeText(
                        this.context,
                        "A verification e-mail has been sent to your e-mail box.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}