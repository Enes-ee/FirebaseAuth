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

        setInformation(editTextUsername, editTextEmail)

        val componentList = mutableListOf<EditText>(
            editTextEmail,
            editTextUsername,
            editTextPassword,
            editTextPassword2
        )

        saveButtonVisibility(componentList)

        pickProfilePicture()

        setProfilePicture()

        saveChanges(editTextUsername, editTextEmail, editTextPassword, editTextPassword2)


        return binding.root
    }

    private fun setProfilePicture() {

        val imagePath = requireContext().getDir("profilePictures",Context.MODE_PRIVATE).absolutePath

        loadProfilePictureFromStorage(imagePath)


    }

    private fun pickProfilePicture() {

        val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) {

            if (it != null) {

                Glide.with(this)
                    .load(it)
                    .transform(MultiTransformation(CenterCrop(), RoundedCorners(100)))
                    .into(binding.buttonProfilePicture!!)


                MainScope().launch {

                    withContext(Dispatchers.IO) {

                        val bitmapImage =
                            Glide.with(requireContext()).asBitmap().load(it).submit().get()

                        saveProfilePictureToInternalStorage(bitmapImage)

                    }

                }

            }

        }

        binding.buttonProfilePicture!!.setOnClickListener {

            getContent.launch("image/*")

        }

    }




    private fun saveProfilePictureToInternalStorage(bitmapImage: Bitmap) : String{
        // path to /data/data/yourapp/app_data/imageDir
        val directory = requireContext().getDir("profilePictures", Context.MODE_PRIVATE)
        // Create imageDir
        val mypath = File(directory, "profilePicture_$userId.jpg")
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(mypath)
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos)

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                fos!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        return directory.absolutePath

    }

    private fun loadProfilePictureFromStorage(path: String) {
        try {
            val f = File(path, "profilePicture_$userId.jpg")
            val b = BitmapFactory.decodeStream(FileInputStream(f))
            val img = binding.buttonProfilePicture as ImageButton

            Glide.with(this)
                .asBitmap()
                .load(b)
                .transform(MultiTransformation(CenterCrop(),RoundedCorners(100)))
                .into(img)

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
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

                if (eachItem.value) {

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


