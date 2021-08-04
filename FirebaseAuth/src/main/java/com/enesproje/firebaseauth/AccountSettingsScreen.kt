package com.enesproje.firebaseauth

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.enesproje.firebaseauth.databinding.FragmentAccountSettingsScreenBinding
import com.enesproje.firebaseauth.mechanics.ProfilePicture
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import java.io.*

class AccountSettingsScreen : Fragment() {

    private var _binding: FragmentAccountSettingsScreenBinding? = null
    val binding get() = _binding!!

    val TAG = "InfoEE_AccountSettings"

    val user = Firebase.auth.currentUser

    var anyChanges : MutableMap<String,Any> = mutableMapOf("email" to false, "username" to false, "password" to false, "profilePicture" to false)
    lateinit var profilePicture : ProfilePicture

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

        setInformation(editTextUsername, editTextEmail)

        val componentList = mutableListOf<EditText>(
            editTextEmail,
            editTextUsername,
            editTextPassword,
            editTextPassword2
        )

        saveButtonVisibility(componentList)

        profilePicture = ProfilePicture(this,binding)

        saveChanges(editTextUsername, editTextEmail, editTextPassword, editTextPassword2)

        return binding.root

    }




    private fun setInformation(editTextUsername: EditText, editTextEmail: EditText) {

        editTextEmail.hint = user?.email

        database.child("Users").child(userId).child("Username")
            .addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                val username = snapshot.value.toString()

                binding.settingsUsername.hint = username

                if (username == "null") binding.settingsUsername.hint = "Has not been created"


            }

            override fun onCancelled(error: DatabaseError) {

                Log.e(TAG, "Username catching failed.")

            }

        })

    }

    private fun saveChanges(
        editTextUsername: EditText,
        editTextEmail: EditText,
        editTextPassword: EditText,
        editTextPassword2: EditText
    ) {

        val buttonSave = binding.buttonSave

        buttonSave.setOnClickListener {

            if (anyChanges["profilePicture"] == true) profilePicture.saveProfilePictureBackground()

            anyChanges = mutableMapOf("email" to false, "username" to false, "password" to false, "profilePicture" to false)

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
                            Log.e(TAG, "Username updated.")
                        }

                        "password" -> {
                            user!!.updatePassword(editTextPassword.text.toString())
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val buttonChangePassword = binding.buttonChangePassword
                                        Log.e(TAG, "User password updated.")
                                        editTextPassword.visibility = View.GONE
                                        editTextPassword2.visibility = View.GONE
                                        buttonChangePassword.visibility = View.VISIBLE
                                    }
                                }
                                .addOnFailureListener {

                                    Log.e(TAG,it.toString())

                                }
                        }

                    }

                    Log.e(TAG,"Changes : " + anyChanges.toString())

                    Toast.makeText(this.context, "Changes have been saved.", Toast.LENGTH_SHORT)
                        .show()

                }

                else if (eachItem.value == "error") {

                    break

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
                anyChanges["email"] = "error"
                Toast.makeText(
                    this.context,
                    "Entered e-mail adress does not meet requirements",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }


    private fun usernameConditions(newUsername: String) {

        if (!newUsername.isEmpty()) {

            if (newUsername.length >= 4) {
                anyChanges["username"] = true
            } else {
                anyChanges["username"] = "error"
                Toast.makeText(
                    this.context,
                    "Entered username does not meet requirements",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

    }

    private fun passwordConditions(newPassword: String, newPassword2: String) {

        if (!newPassword.isEmpty()) {

            var stage: Int = 1

            loop@ while (stage < 3) {

                when (stage) {

                    1 -> if (newPassword.length >= 6) stage++ else {
                        Toast.makeText(
                            this.context,
                            "Password length is less than 6 characters",
                            Toast.LENGTH_SHORT
                        ).show()
                        break@loop
                    }
                    2 -> if (newPassword == newPassword2) stage++ else {
                        Toast.makeText(
                            this.context,
                            "Entered passwords do not match",
                            Toast.LENGTH_SHORT
                        ).show()
                        break@loop
                    }
                }
            }

            if (stage == 3) {
                anyChanges["password"] = true
            }else{
                anyChanges["password"] = "error"
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


        for (eachComponent in componentList) {

            eachComponent.addTextChangedListener {

                if (eachComponent.text.toString().length == 1) {

                    makeSaveButtonVisible()

                } else if (eachComponent.text.isNullOrEmpty() && anyChanges["profilePicture"] == false) {

                    makeSaveButtonGone()

                }

            }

        }

    }

    fun makeSaveButtonVisible() {

        val buttonSave = binding.buttonSave

        buttonSave.visibility = View.VISIBLE

        val animation = AnimationUtils.loadAnimation(this.context, R.anim.fade_in)
        buttonSave.startAnimation(animation)

    }

    fun makeSaveButtonGone() {

        val buttonSave = binding.buttonSave

        buttonSave.visibility = View.GONE

        val animation = AnimationUtils.loadAnimation(this.context, R.anim.fade_out)
        buttonSave.startAnimation(animation)

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
        Log.e(TAG,"DESTROY")
    }

    override fun onStop() {
        super.onStop()
        Log.e(TAG,"STOP")
    }


}


