package com.enesproje.firebaseauth.dialog_fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.enesproje.firebaseauth.LoginScreenDirections
import com.enesproje.firebaseauth.R
import com.enesproje.firebaseauth.TempMainScreenDirections
import com.enesproje.firebaseauth.databinding.FragmentLeaveAppBinding


class LeaveApp : DialogFragment() {

    val TAG = "InfoEE_LeaveApp"
    lateinit var _binding : FragmentLeaveAppBinding
    val binding get() = _binding!!

override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

    _binding = FragmentLeaveAppBinding.inflate(inflater,container,false)

    roundCornersofDialog()

    setComponents()

    return binding.root

    }

    private fun setComponents() {

        val callback = requireActivity().onBackPressedDispatcher.addCallback(this){

            this@LeaveApp.requireActivity().finish()

        }

        binding.buttonCancel.setOnClickListener {

            findNavController().navigate(LeaveAppDirections.actionLeaveAppToLoginScreen())

        }

        binding.buttonLeaveApp.setOnClickListener {

            this@LeaveApp.requireActivity().finish()

        }


    }

    private fun roundCornersofDialog(){

        if (dialog != null && dialog!!.window != null) {

            dialog!!.window!!.setBackgroundDrawable(android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT))
            dialog!!.window!!.requestFeature(android.view.Window.FEATURE_NO_TITLE)

        }

    }

}