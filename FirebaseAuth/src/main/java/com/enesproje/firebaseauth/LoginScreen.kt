package com.enesproje.firebaseauth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth


class LoginScreen : Fragment() {

    val providers = arrayListOf(
        AuthUI.IdpConfig.EmailBuilder().build(),
        AuthUI.IdpConfig.GoogleBuilder().build(),
        AuthUI.IdpConfig.FacebookBuilder().build()
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val binding = inflater.inflate(R.layout.fragment_login_screen, container, false)

        //Firebase tarafından sağlanan hazır giriş ekranı
        //loadPreBuiltLoginScreen()

        return binding
    }

    //Firebase tarafından sağlanan hazır giriş ekranı
    private fun loadPreBuiltLoginScreen(logo : Int? = null , theme : Int? = null) {

        if (logo != null && theme != null) {
            startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .setLogo(logo)
                    .setTheme(theme)
                    .build(), 1
            )
        }else if (theme != null){
            startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .setTheme(theme)
                    .build(), 1
            )
        }else if (logo != null){
            startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setLogo(logo)
                .build(),1
            )
        }else{
            startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(),1
            )
        }
    }

    //Firebase tarafından sağlanan hazır giriş ekranınından dönen sonuç
    private fun preBuiltLoginScreenResult(requestCode: Int, resultCode: Int, data: Intent?){
        if(requestCode == 1){
            val response = IdpResponse.fromResultIntent(data)
            if(resultCode == Activity.RESULT_OK){
                val user = FirebaseAuth.getInstance().currentUser
                Toast.makeText(activity,"Hoşgeldin ${user}",Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(
                        activity,
                        "Giriş başarısız. Hata kodu : ${response?.error?.errorCode}",
                        Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //Firebase tarafından sağlanan hazır giriş ekranınından dönen sonuç
        //preBuiltLoginScreenResult()


    }


}