package com.enesproje.firebaseauth

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.enesproje.firebaseauth.databinding.FragmentForceUpdateBinding

class ForceUpdate : DialogFragment() {


    var _binding : FragmentForceUpdateBinding? = null
    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentForceUpdateBinding.inflate(inflater,container,false)

        val updateUrl = ForceUpdateArgs.fromBundle(requireArguments()).updateUrl

        setButtons(updateUrl)

        roundCornersofDialog()

        isCancelable = false

        return binding.root
    }

    private fun setButtons(updateUrl: String) {

        binding.buttonUpdate.setOnClickListener {

            directToMarket(updateUrl)

        }

        binding.buttonLeave.setOnClickListener {

            requireActivity().finish()

        }

    }

    fun roundCornersofDialog(){

        if (dialog != null && dialog!!.window != null) {

            dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog!!.window!!.requestFeature(Window.FEATURE_NO_TITLE)

        }

    }

    fun directToMarket(updateUrl: String) {

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        requireActivity().startActivity(intent)

    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }



}
