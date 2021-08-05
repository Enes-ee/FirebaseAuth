package com.enesproje.firebaseauth

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.enesproje.firebaseauth.databinding.FragmentTempMainScreenBinding
import com.facebook.login.LoginManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class TempMainScreen : Fragment() {

    var _binding : FragmentTempMainScreenBinding? = null
    val binding : FragmentTempMainScreenBinding get() = _binding!!

    val auth = Firebase.auth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentTempMainScreenBinding.inflate(inflater,container,false)

        setComponents()

        onBackPressed()

        return binding.root
    }

    private fun setComponents() {

        val isAnonymous = Firebase.auth.currentUser!!.isAnonymous

        binding.buttonAccountSettings.setOnClickListener {

            if (!isAnonymous) {

                this.findNavController()
                    .navigate(TempMainScreenDirections.actionTempMainScreenToAccountSettingsScreen())

            }else{

                this.findNavController().navigate(TempMainScreenDirections.actionTempMainScreenToWarningCreateAccount())

            }

        }

        binding.buttonQuit.setOnClickListener {

            this.findNavController().navigate(TempMainScreenDirections.actionTempMainScreenToQuitAccount())

        }

        if (auth.currentUser!!.isAnonymous) {

            binding.tvLoginType.text = getString(R.string.anonymous_authentication)

        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.temp_main_screen_menu,menu)
    }


    private fun onBackPressed() {

        val callback = requireActivity().onBackPressedDispatcher.addCallback(this){

            this@TempMainScreen.findNavController().navigate(TempMainScreenDirections.actionTempMainScreenToQuitAccount())

        }

    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}