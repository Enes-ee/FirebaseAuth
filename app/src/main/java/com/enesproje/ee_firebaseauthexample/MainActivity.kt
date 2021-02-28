package com.enesproje.ee_firebaseauthexample


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.enesproje.firebaseauth.LoginScreen

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.commit {

            setReorderingAllowed(true)
            add<LoginScreen>(R.id.container01)
            addToBackStack("LoginScreen eklendi.")

        }

    }
}