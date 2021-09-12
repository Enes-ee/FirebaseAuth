package com.enesproje.firebaseauth.sheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.enesproje.firebaseauth.databinding.FragmentLoadProfilePictureBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class LoadProfilePicture : BottomSheetDialogFragment() {

    var _binding : FragmentLoadProfilePictureBinding? = null
    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentLoadProfilePictureBinding.inflate(layoutInflater)

        setComponents()


        return binding.root
    }

    private fun setComponents() {

        binding.lppLine1.setOnClickListener {

            findNavController().previousBackStackEntry?.savedStateHandle?.set("chosenItem","camera")
            dismiss()

        }

        binding.lppLine2.setOnClickListener {

            findNavController().previousBackStackEntry?.savedStateHandle?.set("chosenItem","gallery")
            dismiss()

        }

    }

}