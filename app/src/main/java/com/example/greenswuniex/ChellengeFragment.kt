package com.example.greenswuniex

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class ChellengeFragment :Fragment() {

    lateinit var chellenge_button :Button
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_chellenge, container, false)

        chellenge_button = view.findViewById(R.id.chellenge_button)

        chellenge_button.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_chellenge_to_challenge_calender)


        }
        return view
    }
}