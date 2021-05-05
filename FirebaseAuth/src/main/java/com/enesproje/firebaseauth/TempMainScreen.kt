package com.enesproje.firebaseauth

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.facebook.login.LoginManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class TempMainScreen : Fragment() {

    val auth = Firebase.auth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        setHasOptionsMenu(true)

        return inflater.inflate(R.layout.fragment_temp_main_screen, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.temp_main_screen_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logOut -> {

                exitDialog()
                return true

            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun exitDialog(){

        val builder = activity.let {
            AlertDialog.Builder(it)
        }
        builder.setMessage("You are quitting from your account. Are you sure?")
                .setTitle("Exit")

        builder.apply {

            setPositiveButton("Yes",DialogInterface.OnClickListener { dialog, id ->
                auth.signOut()
                LoginManager.getInstance().logOut()
                dialog.dismiss()
                this@TempMainScreen.findNavController().navigate(TempMainScreenDirections.actionTempMainScreenToLoginScreen())
            })

            setNegativeButton("Return", DialogInterface.OnClickListener { dialog, id ->
                dialog.dismiss()
            })

        }

        val dialog = builder.create()
        dialog.show()

    }

}