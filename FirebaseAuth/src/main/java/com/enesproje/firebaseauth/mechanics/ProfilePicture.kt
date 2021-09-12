package com.enesproje.firebaseauth.mechanics

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import androidx.navigation.fragment.findNavController
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

    val TAG = "InfoEE_ProfilePicture"

    var userId = Firebase.auth.currentUser!!.uid
    val storage = Firebase.storage
    var storageRef = storage.reference
    var firebaseProfilePictureRef = storageRef.child("Users/$userId/profilePictures/profilePicture_$userId.jpg")
    lateinit var getContentLauncher : ActivityResultLauncher<String>
    lateinit var takePictureLauncher : ActivityResultLauncher<Uri>
    private var uri: Uri? = null

    init {

        setProfilePicture()
        initRegisterForActivityResult()

        binding.buttonProfilePicture!!.setOnClickListener {

            fragment.findNavController()
                .navigate(com.enesproje.firebaseauth.AccountSettingsScreenDirections.actionAccountSettingsScreenToLoadProfilePicture())

        }

        fragment.findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>("chosenItem")?.observe(fragment.viewLifecycleOwner) {

            when (it) {

                "gallery" -> getContentLauncher.launch("image/*")

                "camera" -> takePicture()

            }

        }

    }

    private fun takePicture() {

        val directory = fragment.requireContext().cacheDir
        val file = File(directory,"profilePicture_$userId.jpg")


        if (!file.exists()){

            file.parentFile.mkdirs()
            file.createNewFile()

        }

        uri = FileProvider.getUriForFile(fragment.requireContext(),"com.enesproje.firebaseauth",file)
        takePictureLauncher.launch(uri)


    }


    private fun setProfilePicture() {

        //val imagePath2 = fragment.requireContext().getDir("profilePictures", Context.MODE_PRIVATE).absolutePath
        val imagePath = fragment.requireContext().filesDir.absolutePath + File.separator + "profilePictures"

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

    private fun temporarilySetProfilePicture(){

        val imagePath = fragment.requireContext().cacheDir.absolutePath
        val f = File(imagePath, "profilePicture_$userId.jpg")
        val b = BitmapFactory.decodeStream(FileInputStream(f))
        val img = binding.buttonProfilePicture
        Glide.with(fragment)
            .asBitmap()
            .load(b)
            .transform(MultiTransformation(CenterCrop(), RoundedCorners(100)))
            .into(img!!)

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

    private fun initRegisterForActivityResult() {

        getContentLauncher = fragment.registerForActivityResult(ActivityResultContracts.GetContent()) {

            if (it != null) {

                Glide.with(fragment)
                    .load(it)
                    .transform(MultiTransformation(CenterCrop(), RoundedCorners(100)))
                    .into(binding.buttonProfilePicture!!)

                fragment.anyChanges["profilePicture"] = true

                fragment.setSaveButtonVisible()

            }

        }

        takePictureLauncher = fragment.registerForActivityResult(ActivityResultContracts.TakePicture()) {

            Log.e(TAG,"takePictureLauncher result : $it")

            if (it == true) {

                temporarilySetProfilePicture()

                fragment.anyChanges["profilePicture"] = true

                fragment.setSaveButtonVisible()

            }

//            MainScope().launch(Dispatchers.IO) {
//
//                val file = File(fragment.requireContext().cacheDir.absolutePath + File.separator + "profilePicture_$userId.jpg")
//
//                var uploadTask = firebaseProfilePictureRef.putBytes(file.readBytes())
//                    .addOnSuccessListener {
//
//                        Log.e(TAG,"Loading of profile picture to firebase is successful")
//
//                    }
//                    .addOnFailureListener { exception ->
//
//                        Log.e(TAG,"Loading of profile picture to firebase has failed. Exception : ${exception.message}")
//
//                    }
//
//            }

        }

    }

    fun saveProfilePictureBackground() {

        val bitmap = binding.buttonProfilePicture!!.drawable.toBitmap()

        fragment.setSaveButtonGone()

        MainScope().launch(Dispatchers.IO) {

                val bitmapImage =
                    Glide.with(fragment.requireContext()).asBitmap().load(bitmap).submit().get()

                saveProfilePictureToInternalStorage(bitmapImage)
                saveProfilePictureToFirebase(bitmapImage)

        }

    }




    private fun saveProfilePictureToInternalStorage(bitmapImage: Bitmap) : String {


        val directory = fragment.requireContext().filesDir.absolutePath + File.separator + "profilePictures"
        // Create imageDir
        val file = File(directory, "profilePicture_$userId.jpg")

        file.parentFile.mkdirs()
        file.createNewFile()

        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(file)
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

        return directory

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

        baos.close()

    }

}