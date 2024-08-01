package com.example.greenswuniex

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class SettingFragment : Fragment() {

    lateinit var individualBtn : Button
    lateinit var accountBtn : Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_setting, container, false)

        individualBtn = view.findViewById(R.id.setting_individual_btn)
        accountBtn = view.findViewById(R.id.setting_account_btn)

        individualBtn.setOnClickListener {
            findNavController().navigate(R.id.action_setting_to_individual)
        }
        return view
    }
}