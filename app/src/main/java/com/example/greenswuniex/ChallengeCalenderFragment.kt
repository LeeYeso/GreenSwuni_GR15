package com.example.greenswuniex

import android.content.Context
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Task
import com.google.android.material.datepicker.DayViewDecorator
import com.google.firebase.firestore.FirebaseFirestore
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Locale

class ChallengeCalenderFragment : Fragment() {


    private lateinit var calendarView: MaterialCalendarView
    lateinit var btncalender: Button


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {//뷰바인딩
        val view = inflater.inflate(R.layout.fragment_challenge_calender, container, false)



        // 뷰 초기화
        calendarView = view.findViewById(R.id.calenderView)
        btncalender = view.findViewById(R.id.btncalender)

        //데코
        calendarView.addDecorators(TodayDecorator(requireContext()))

        //버틑 클릭 시 챌린지 선택으로 이동
        btncalender = view.findViewById(R.id.btncalender)

        btncalender.setOnClickListener {
            findNavController().navigate(R.id.action_challenge_calender_to_chellengeCategoryFragment)

        }
        return view





    }



}