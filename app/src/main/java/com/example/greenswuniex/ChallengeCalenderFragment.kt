package com.example.greenswuniex

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.prolificinteractive.materialcalendarview.MaterialCalendarView

class ChallengeCalenderFragment : Fragment() {

    private lateinit var calendarView: MaterialCalendarView
    private lateinit var btncalender: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ChallengeListAdapter
    private lateinit var itemList: MutableList<ChallengeListItem>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 뷰 초기화
        val view = inflater.inflate(R.layout.fragment_challenge_calender, container, false)
        calendarView = view.findViewById(R.id.calenderView)
        btncalender = view.findViewById(R.id.btncalender)
        recyclerView = view.findViewById(R.id.recyclerview) // RecyclerView 초기화

        // 데이터 초기화
        itemList = mutableListOf()

        // 어댑터 생성 및 RecyclerView에 설정
        adapter = ChallengeListAdapter(requireContext(), itemList)
        recyclerView.adapter = adapter

        // 레이아웃 매니저 설정 (가로 스크롤)
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        // 데코레이터 추가
        calendarView.addDecorators(TodayDecorator(requireContext()))

        // SELECT 버튼으로부터 전달된 데이터 받기
        val selectedCategory = arguments?.getString("selectedCategory")
        val selectedDetail = arguments?.getString("selectedDetail")

        if (selectedCategory != null && selectedDetail != null) {
            addItemToRecyclerView(selectedCategory, selectedDetail)
        }

        // 버튼 클릭 시 챌린지 선택으로 이동
        btncalender.setOnClickListener {
            findNavController().navigate(R.id.action_challenge_calender_to_chellengeCategoryFragment)
        }

        return view
    }

    private fun addItemToRecyclerView(category: String, detail: String) {
        val newItem = ChallengeListItem(
            challenge_onCheck = false,
            challenge_category = category,
            challenge_name = detail
        )
        itemList.add(newItem)
        adapter.notifyItemInserted(itemList.size - 1)
    }
}
