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
import com.enesproje.firebaseauth.databinding.FragmentWarningCreateAccountBinding


class WarningCreateAccount : DialogFragment() {

    var _binding : FragmentWarningCreateAccountBinding? = null
    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentWarningCreateAccountBinding.inflate(inflater,container,false)

        roundCornersofDialog()

        setComponents()

        return binding.root
    }

    private fun setComponents() {

        binding.buttonBack.setOnClickListener {

            dialog!!.dismiss()

        }

        binding.buttonMove.setOnClickListener {

            findNavController().navigate(WarningCreateAccountDirections.actionWarningCreateAccountToLoginScreen())

        }

    }

    private fun roundCornersofDialog(){

        if (dialog != null && dialog!!.window != null) {

            dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog!!.window!!.requestFeature(Window.FEATURE_NO_TITLE)

        }

    }

}