package com.example.greenswuniex

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.greenswuniex.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import java.util.concurrent.TimeUnit

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
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
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        // 데이터 리스트 초기화 (추후 실제 데이터를 불러오는 코드로 대체)
        val dataList = List(20) { "Item ${it + 1}" }
        val chellengeAdapter = ChellengeRecylclerAdapter(dataList)
        binding.recyclerView.adapter = chellengeAdapter

        // 버튼 클릭 리스너 설정
        binding.chellengeBtn.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_chellenge)
        }

        binding.searchBtn.setOnClickListener {
            val intent = Intent(requireContext(), FavoritePlacesActivity::class.java)
            startActivity(intent)
        }

        // 사용자 데이터 불러오기 및 사용 일 수 계산
        val userEmail = auth.currentUser?.email
        if (userEmail != null) {
            Log.d("HomeFragment", "User email: $userEmail")
            getUserData(userEmail)
        } else {
            binding.startDayTv.text = "사용자 정보 없음"
            Log.e("HomeFragment", "User email is null")
        }

        return view
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
