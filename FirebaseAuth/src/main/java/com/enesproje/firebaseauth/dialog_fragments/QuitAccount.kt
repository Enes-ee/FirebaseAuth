package com.enesproje.firebaseauth.dialog_fragments

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.enesproje.firebaseauth.R
import com.enesproje.firebaseauth.TempMainScreenDirections
import com.enesproje.firebaseauth.databinding.FragmentQuitAccountBinding
import com.facebook.login.LoginManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class QuitAccount : DialogFragment() {

    var _binding : FragmentQuitAccountBinding? = null
    val binding get() = _binding!!

    val auth = Firebase.auth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        _binding = FragmentQuitAccountBinding.inflate(inflater,container,false)

        setButtons()

        roundCornersofDialog()

        return binding.root
    }

    private fun setButtons() {

        val buttonConfirmQuit = binding.buttonConfirmQuit
        val buttonCancelAction = binding.buttonCancelAction

        buttonConfirmQuit.setOnClickListener {

            auth.signOut()
            LoginManager.getInstance().logOut()
            dialog!!.dismiss()
            findNavController().navigate(QuitAccountDirections.actionQuitAccountToLoginScreen())

        }

        buttonCancelAction.setOnClickListener {

            dialog!!.dismiss()

        }

    }

    private fun roundCornersofDialog(){

        if (dialog != null && dialog!!.window != null) {

            dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog!!.window!!.requestFeature(Window.FEATURE_NO_TITLE)

        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}