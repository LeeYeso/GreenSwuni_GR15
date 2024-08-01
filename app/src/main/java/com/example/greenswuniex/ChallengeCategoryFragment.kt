package com.example.greenswuniex


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class ChallengeCategoryFragment : Fragment() {

    lateinit var lifeBtn : Button
    lateinit var mealBtn : Button
    lateinit var digitalBtn : Button
    lateinit var moveBtn : Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_challenge_category, container, false)

        lifeBtn = view.findViewById(R.id.challenge_life_btn)
        mealBtn = view.findViewById(R.id.challenge_meal_btn)
        digitalBtn = view.findViewById(R.id.challenge_digital_btn)
        moveBtn = view.findViewById(R.id.challenge_move_btn)


        lifeBtn.setOnClickListener {
            val argString = lifeBtn.text.toString()
            val action = ChallengeCategoryFragmentDirections.actionChallengeCategoryToChallengeSelect(argString)
            findNavController().navigate(action)
        }
        mealBtn.setOnClickListener {
            val argString = mealBtn.text.toString()
            val action = ChallengeCategoryFragmentDirections.actionChallengeCategoryToChallengeSelect(argString)
            findNavController().navigate(action)
        }
        digitalBtn.setOnClickListener {
            val argString = digitalBtn.text.toString()
            val action = ChallengeCategoryFragmentDirections.actionChallengeCategoryToChallengeSelect(argString)
            findNavController().navigate(action)
        }
        moveBtn.setOnClickListener {
            val argString = moveBtn.text.toString()
            val action = ChallengeCategoryFragmentDirections.actionChallengeCategoryToChallengeSelect(argString)
            findNavController().navigate(action)
        }

        return view
    }
}