package com.enesproje.firebaseauth.mechanics

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.ImageButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.enesproje.firebaseauth.databinding.FragmentAccountSettingsScreenBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.*

class ProfilePicture (val fragment : Fragment , val binding : FragmentAccountSettingsScreenBinding){

    val TAG = "ProfilePicture"

    var userId = Firebase.auth.currentUser!!.uid
    val storage = Firebase.storage
    var storageRef = storage.reference
    var firebaseProfilPictureRef = storageRef.child("Users/$userId/profilePictures/profilPicture_$userId.jpg")

    init {

        setProfilePicture()
        pickProfilePicture()

    }


    private fun setProfilePicture() {

        val imagePath = fragment.requireContext().getDir("profilePictures", Context.MODE_PRIVATE).absolutePath

        loadProfilePictureFromStorage(imagePath)

    }

    private fun pickProfilePicture() {

        val getContent = fragment.registerForActivityResult(ActivityResultContracts.GetContent()) {

            if (it != null) {

                Glide.with(fragment)
                    .load(it)
                    .transform(MultiTransformation(CenterCrop(), RoundedCorners(100)))
                    .into(binding.buttonProfilePicture!!)


                MainScope().launch {

                    withContext(Dispatchers.IO) {

                        val bitmapImage =
                            Glide.with(fragment.requireContext()).asBitmap().load(it).submit().get()

                        saveProfilePictureToInternalStorage(bitmapImage)
                        saveProfilePictureToFirebase(bitmapImage)

                    }

                }

            }

        }

        binding.buttonProfilePicture!!.setOnClickListener {

            getContent.launch("image/*")

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

        var uploadTask = firebaseProfilPictureRef.putBytes(data)
            .addOnSuccessListener {

                Log.e(TAG,"Loading of profile picture to firebase is successful")

            }
            .addOnFailureListener {

                Log.e(TAG,"Loading of profile picture to firebase has failed")

            }

    }

    private fun loadProfilePictureFromStorage(path: String) {
        try {
            val f = File(path, "profilePicture_$userId.jpg")
            val b = BitmapFactory.decodeStream(FileInputStream(f))
            val img = binding.buttonProfilePicture as ImageButton

            Glide.with(fragment)
                .asBitmap()
                .load(b)
                .transform(MultiTransformation(CenterCrop(), RoundedCorners(100)))
                .into(img)

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
    }

}