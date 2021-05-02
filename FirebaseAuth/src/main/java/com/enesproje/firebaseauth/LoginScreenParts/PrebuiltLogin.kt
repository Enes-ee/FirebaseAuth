package com.enesproje.firebaseauth.LoginScreenParts

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth

class PrebuiltLogin (val fragment : Fragment){

    //Firebase tarafından sağlanan hazır giriş ekranı için gerekiyor.
    val providers = arrayListOf(
        AuthUI.IdpConfig.EmailBuilder().build(),
        AuthUI.IdpConfig.GoogleBuilder().build(),
        AuthUI.IdpConfig.FacebookBuilder().build()
    )


    //Firebase tarafından sağlanan hazır giriş ekranı
    fun loadPreBuiltLoginScreen(logo : Int? = null , theme : Int? = null) {

        if (logo != null && theme != null) {
            fragment.startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .setLogo(logo)
                    .setTheme(theme)
                    .build(), 1
            )
        }else if (theme != null){
            fragment.startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .setTheme(theme)
                    .build(), 1
            )
        }else if (logo != null){
            fragment.startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .setLogo(logo)
                    .build(),1
            )
        }else{
            fragment.startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .build(),1
            )
        }
    }

    //Firebase tarafından sağlanan hazır giriş ekranınından dönen sonuç
    fun preBuiltLoginScreenResult(requestCode: Int, resultCode: Int, data: Intent?){
        if(requestCode == 1){
            val response = IdpResponse.fromResultIntent(data)
            if(resultCode == Activity.RESULT_OK){
                val user = FirebaseAuth.getInstance().currentUser
                Toast.makeText(fragment.activity,"Hoşgeldin ${user}", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(
                    fragment.activity,
                    "Giriş başarısız. Hata kodu : ${response?.error?.errorCode}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

}