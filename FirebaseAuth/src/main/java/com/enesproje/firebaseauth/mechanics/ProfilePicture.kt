package com.enesproje.firebaseauth.mechanics

import android.accounts.Account
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.widget.ImageButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.enesproje.firebaseauth.AccountSettingsScreen
import com.enesproje.firebaseauth.databinding.FragmentAccountSettingsScreenBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.*
import java.io.*

class ProfilePicture (val fragment : AccountSettingsScreen , val binding : FragmentAccountSettingsScreenBinding){

    val TAG = "ProfilePicture"

    var userId = Firebase.auth.currentUser!!.uid
    val storage = Firebase.storage
    var storageRef = storage.reference
    var firebaseProfilePictureRef = storageRef.child("Users/$userId/profilePictures/profilePicture_$userId.jpg")

    init {

        setProfilePicture()
        pickProfilePicture()

    }


    private fun setProfilePicture() {

        val imagePath = fragment.requireContext().getDir("profilePictures", Context.MODE_PRIVATE).absolutePath

        try {
            val f = File(imagePath, "profilePicture_$userId.jpg")
            val b = BitmapFactory.decodeStream(FileInputStream(f))
            val img = binding.buttonProfilePicture

            Glide.with(fragment)
                .asBitmap()
                .load(b)
                .transform(MultiTransformation(CenterCrop(), RoundedCorners(100)))
                .into(img!!)

        } catch (e: FileNotFoundException) {
            e.printStackTrace()

            downloadProfilePicture()

        }


    }

    private fun downloadProfilePicture() {


            val downloadUrl = firebaseProfilePictureRef.downloadUrl.addOnSuccessListener {

                MainScope().launch {

                    Log.e(TAG, "Url : " + it)

                    Glide.with(fragment.requireContext()).load(it)
                        .transform(MultiTransformation(CenterCrop(), RoundedCorners(100)))
                        .into(binding.buttonProfilePicture!!)


                    val asyncLoad = async(Dispatchers.IO) {

                        Glide.with(fragment.requireContext())
                            .asBitmap()
                            .load(it)
                            .submit()
                            .get()

                    }

                    val bitmap = asyncLoad.await()

                    saveProfilePictureToInternalStorage(bitmap)

                }

            }

    }

    private fun pickProfilePicture() {

        val getContent = fragment.registerForActivityResult(ActivityResultContracts.GetContent()) {

            if (it != null) {

                Glide.with(fragment)
                    .load(it)
                    .transform(MultiTransformation(CenterCrop(), RoundedCorners(100)))
                    .into(binding.buttonProfilePicture!!)

                fragment.anyChanges["profilePicture"] = true

                fragment.makeSaveButtonVisible()

            }

        }

        binding.buttonProfilePicture!!.setOnClickListener {

            getContent.launch("image/*")

        }

    }

    fun saveProfilePictureBackground() {

        val bitmap = binding.buttonProfilePicture!!.drawable.toBitmap()

        fragment.makeSaveButtonGone()

        MainScope().launch(Dispatchers.IO) {

                val bitmapImage =
                    Glide.with(fragment.requireContext()).asBitmap().load(bitmap).submit().get()

                saveProfilePictureToInternalStorage(bitmapImage)
                saveProfilePictureToFirebase(bitmapImage)

        }

    }




    private fun saveProfilePictureToInternalStorage(bitmapImage: Bitmap) : String {


        // path to /data/data/yourapp/app_data/imageDir
        val directory = fragment.requireContext().getDir("profilePictures", Context.MODE_PRIVATE)
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

    private fun saveProfilePictureToFirebase(bitmap : Bitmap) {

        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos)
        val data = baos.toByteArray()

        var uploadTask = firebaseProfilePictureRef.putBytes(data)
            .addOnSuccessListener {

                Log.e(TAG,"Loading of profile picture to firebase is successful")

            }
            .addOnFailureListener {

                Log.e(TAG,"Loading of profile picture to firebase has failed")

            }

    }

}