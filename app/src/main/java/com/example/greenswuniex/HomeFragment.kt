package com.example.greenswuniex

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.greenswuniex.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import java.util.concurrent.TimeUnit
private lateinit var recyclerView: RecyclerView
private lateinit var adapter: ChallengeListAdapter
private lateinit var itemList: MutableList<ChallengeListItem>


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding?=null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        // Firebase 초기화
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // RecyclerView 초기화
        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        // 데이터 리스트 초기화
        itemList = ChallengeCalenderFragment.loadItemListFromSharedPreferences(requireContext())

        // 어댑터 생성 및 RecyclerView에 설정
        adapter = ChallengeListAdapter(
            requireContext(),
            itemList,
            isHomeFragment = true
        ) { checkedCount ->
            updateProgressBarAndLevel(checkedCount) // 체크된 항목 수를 이용해 프로그래스 바와 레벨 업데이트
        }
        /*
        adapter = ChallengeListAdapter(requireContext(), itemList, isHomeFragment = true){
            updateProgressBarAndLevel() // 체크 상태 변경 시 프로그래스 바 업데이트
        }*/
        //추가된 부분
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerView.adapter = adapter
        //recyclerView.adapter = adapter

        // 버튼 클릭 리스너 설정
        binding.chellengeBtn.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_chellenge)
        }

        binding.searchBtn.setOnClickListener {
            val intent = Intent(requireContext(), FavoritePlacesActivity::class.java)
            startActivity(intent)
        }

        // 날짜 초기화 확인 및 상태 초기화
        ///checkAndResetChallengeSelection()

        // 사용자 데이터 불러오기 및 사용 일 수 계산
        val userEmail = auth.currentUser?.email
        if (userEmail != null) {
            Log.d("HomeFragment", "User email: $userEmail")
            getUserData(userEmail)
        } else {
            binding.startDayTv.text = "사용자 정보 없음"
            Log.e("HomeFragment", "User email is null")
        }

// 초기 프로그래스 바 업데이트
        updateProgressBarAndLevel(itemList.count { it.challenge_onCheck })
        return view
    }


    //프로그래스 바
    interface UpdateProgressListener {
        fun updateProgressBarAndLevel(checkedCount: Int)
    }
    private fun updateProgressBarAndLevel(checkedCount: Int) {
      //  val checkedCount = itemList.count { it.challenge_onCheck }
        val progress = when {
            checkedCount >= 30 -> 100
            checkedCount >= 20 -> 75
            checkedCount >= 10 -> 50
            checkedCount >= 5 -> 25
            else -> 0
        }

        // 프로그래스 바 업데이트
        binding.customProgressBar.progress = progress
        binding.progressText.text = "$progress%"

        // 레벨 계산
        val level = calculateLevel(checkedCount)
        binding.homeLevelTv.text = getLevelText(level)
    }

    private fun calculateLevel(checkedCount: Int): Int {
        return when {
            checkedCount >= 30 -> 5
            checkedCount in 20..29 -> 4
            checkedCount in 10..19 -> 3
            checkedCount in 5..9 -> 2
            else -> 1
        }
    }

    private fun getLevelText(level: Int): String {
        return when (level) {
            5 -> "마스터 푸른 행성의 수호자 LV.$level"
            4 -> "울창한 산림의 수호천사 LV.$level"
            3 -> "든든한 나무의 친구 LV.$level"
            2 -> "활짝 피어난 환경 운동가 LV.$level"
            else -> "꼬꼬마 환경 지킴이 LV.$level"
        }
    }
    private fun getUserData(userEmail: String) {
        val userRef = firestore.collection("users").document(userEmail)
        userRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val userId = document.getString("username") ?: "Unknown"
                    binding.userId.text = "$userId 의 \n그린슈니"

                    val verificationDate = document.getDate("verificationDate")
                    val loginDate = document.getDate("loginDate")
                    Log.d("HomeFragment", "Document data: ${document.data}")
                    if (verificationDate != null && loginDate != null) {
                        val daysSinceLastLogin = calculateDaysSinceLastLogin(verificationDate, loginDate)
                        displayDaysSinceLastLogin(daysSinceLastLogin)
                    } else {
                        binding.startDayTv.text = "로그인 날짜 정보 없음"
                        Log.e("HomeFragment", "Missing date information in document")
                    }
                } else {
                    binding.startDayTv.text = "데이터 없음"
                    Log.e("HomeFragment", "Document does not exist")
                }
            }
            .addOnFailureListener { e ->
                binding.startDayTv.text = "데이터 가져오기 실패"
                Log.e("HomeFragment", "Failed to get document", e)
            }
    }

    private fun calculateDaysBetweenDates(startDate: Date, endDate: Date): Long {
        val diffInMillis = endDate.time - startDate.time
        return TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS)
    }

    private fun calculateDaysSinceLastLogin(verificationDate: Date, loginDate: Date): Long {
        return calculateDaysBetweenDates(verificationDate, loginDate)
    }

    private fun displayDaysSinceLastLogin(days: Long) {
        val daysWithOffset = days + 1
        binding.startDayTv.text = "우리의 여정 +$daysWithOffset 일째"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
