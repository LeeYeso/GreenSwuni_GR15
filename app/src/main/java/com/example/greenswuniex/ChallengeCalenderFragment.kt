package com.example.greenswuniex

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.DayViewDecorator
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView

class ChallengeCalenderFragment : Fragment() {


    lateinit var btncalender: Button


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {//뷰바인딩
        val view = inflater.inflate(R.layout.fragment_challenge_calender, container, false)



        //버틑 클릭 시 챌린지 선택으로 이동
        btncalender = view.findViewById(R.id.btncalender)

        btncalender.setOnClickListener {
            findNavController().navigate(R.id.action_challenge_calender_to_chellengeCategoryFragment)

        }
        return view





    }



}