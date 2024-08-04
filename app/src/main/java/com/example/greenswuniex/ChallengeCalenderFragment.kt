package com.example.greenswuniex

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
        itemList = loadItemListFromSharedPreferences(requireContext())

        // 어댑터 생성 및 RecyclerView에 설정
        adapter = ChallengeListAdapter(requireContext(), itemList, isHomeFragment = false)
        recyclerView.adapter = adapter

        // 레이아웃 매니저 설정 (가로 스크롤)
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        // 데코레이터 추가
        calendarView.addDecorators(TodayDecorator(requireContext()))

        // SELECT 버튼으로부터 전달된 데이터 받기
        val selectedCategory = arguments?.getString("selectedCategory")
        val selectedDetail = arguments?.getString("selectedDetail")

        if (selectedCategory != null && selectedDetail != null) {
            addItemToRecyclerView(requireContext(), selectedCategory, selectedDetail)
        }

        // 버튼 클릭 시 챌린지 선택으로 이동
        btncalender.setOnClickListener {
            findNavController().navigate(R.id.action_challenge_calender_to_chellengeCategoryFragment)
        }

        return view
    }

    // 프래그먼트가 일시 정지 상태로 전환될 때 호출됨
    override fun onPause() {
        super.onPause()
        saveItemListToSharedPreferences(requireContext(), itemList)
    }

    private fun addItemToRecyclerView(context: Context, category: String, detail: String) {
        val newItem = ChallengeListItem(
            challenge_onCheck = false,
            challenge_category = category,
            challenge_name = detail
        )
        itemList.add(newItem)
        adapter.notifyItemInserted(itemList.size - 1)
        saveItemListToSharedPreferences(context, itemList)
    }

    companion object {
        fun saveItemListToSharedPreferences(context: Context, itemList: MutableList<ChallengeListItem>) {
            val sharedPreferences = context.getSharedPreferences("challenge_prefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            val gson = Gson()
            val json = gson.toJson(itemList)
            editor.putString("item_list", json)
            editor.apply()
        }

        fun loadItemListFromSharedPreferences(context: Context): MutableList<ChallengeListItem> {
            val sharedPreferences = context.getSharedPreferences("challenge_prefs", Context.MODE_PRIVATE)
            val gson = Gson()
            val json = sharedPreferences.getString("item_list", null)
            return if (json != null) {
                val type = object : TypeToken<MutableList<ChallengeListItem>>() {}.type
                gson.fromJson(json, type)
            } else {
                mutableListOf()
            }
        }
    }
}
