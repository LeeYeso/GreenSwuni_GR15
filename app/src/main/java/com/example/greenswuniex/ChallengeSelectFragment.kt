package com.example.greenswuniex

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.greenswuniex.databinding.FragmentChallengeSelectBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import android.content.Context

class ChallengeSelectFragment : Fragment() {

    private lateinit var binding: FragmentChallengeSelectBinding

    private var selectedButton: Button? = null

    // DB
    private lateinit var firestore: FirebaseFirestore
    private lateinit var selectedTask: String

    private val args: ChallengeSelectFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChallengeSelectBinding.inflate(inflater, container, false)
        val view = binding.root

        val argString = args.argCategory

        setupButtons(argString)

        binding.challengeBackBtn.setOnClickListener {
            findNavController().navigate(R.id.action_challengeSelect_to_challengeCategory)
        }

        binding.challengeStartBtn.setOnClickListener {
            if (selectedButton != null) {
                val selectedDetail = selectedButton?.text.toString()
                if (isChallengeAlreadyAdded(argString, selectedDetail)) {
                    showAlreadyAddedDialog()
                } else {
                    val bundle = Bundle().apply {
                        putString("selectedCategory", argString)
                        putString("selectedDetail", selectedDetail)
                    }
                    findNavController().navigate(R.id.action_challengeSelect_to_challengeCalender, bundle)
                }
            } else {
                showConfirmDialog()
            }
        }

        return view
    }

    private fun setupButtons(category: String) {
        val buttons = listOf(binding.challengeBtn1, binding.challengeBtn2, binding.challengeBtn3, binding.challengeBtn4)

        when (category) {
            "생활" -> {
                binding.challengeBtn1.text = "샤워시간을 10분이내로 끝내기\n(0.55kg 감축)"
                binding.challengeBtn2.text = "비닐봉투나 종이쇼핑백 대신 장바구니 사용하기\n(개 당 0.003, 0.012kg 감축)"
                binding.challengeBtn3.text = "쓰레기 줍기 및 올바른 분리배출 실천하기\n(회 당 0.241kg 감축)"
                binding.challengeBtn4.text = "사용하지 않는 가전제품 플러그 뽑기\n(1.05kg 감축)"
            }

            "식사" -> {
                binding.challengeBtn1.text = "음식물 쓰레기 줄이기\n(0.5kg당 0.04kg 감축)"
                binding.challengeBtn2.text = "일주일에 하루 고기 없는 식사하기\n(한 끼 식사 당 3.64kg 감축)"
                binding.challengeBtn3.text = "일회용 컵 대신 텀블러나 머그잔 사용하기\n(개 당 0.011kg 감축)"
                binding.challengeBtn4.text = "지역에서 생산된 로컬푸드 음식으로 식사\n(하루 한끼 0.5kg 감축)"
            }

            "이동수단" -> {
                binding.challengeBtn1.text = "가까운 거리는 도보 혹은 자전거 이용하기\n(2km 기준 2.09kg 감축)"
                binding.challengeBtn2.text = "개인 승용차 대신 지하철을 이용하기\n(1km당 약 약 0.1kg 감축)"
                binding.challengeBtn3.text = "개인 승용차 대신 버스를 이용하기\n(1km당 약 0.08kg 감축)"
                binding.challengeBtn4.text = "차량을 공유하여 이용하기\n(1인당 1km 0.1km 감축)"
            }

            "디지털" -> {
                binding.challengeBtn1.text = "스마트폰 절전 및 밝기 조절\n(밝기를 70%로 낮추면 에너지 20% 감축)"
                binding.challengeBtn2.text = "스마트폰 사용시간 줄이기\n(1MB당 11g)"
                binding.challengeBtn3.text = "스마트폰 스트리밍대신 다운로드\n(1시간 스트리밍 = 자동차 1km 탄소 배출)"
                binding.challengeBtn4.text = "불필요한 이메일 및 파일 정리하기\n(1통 당 0.004kg, 파일 1MB 당 0.036kg 감축)"
            }
        }

        buttons.forEach { button ->
            button.setOnClickListener {
                onSelectButton(button)
            }
        }
    }

    private fun onSelectButton(button: Button) {
        if (selectedButton == button) {
            // 같은 버튼을 다시 클릭하면 선택 해제
            button.isSelected = false
            button.background = resources.getDrawable(R.drawable.round_button_orange, null) // 기본 배경으로 변경
            selectedButton = null
        } else {
            // 다른 버튼을 클릭하면 기존 선택 해제 후 새로운 버튼 선택
            selectedButton?.isSelected = false
            selectedButton?.background = resources.getDrawable(R.drawable.round_button_orange, null) // 기본 배경으로 변경

            button.isSelected = true
            button.background = resources.getDrawable(R.drawable.select_button, null) // 선택된 배경으로 변경
            selectedButton = button
        }
    }

    private fun showConfirmDialog() {
        val dialogView = layoutInflater.inflate(R.layout.confirm_dialog, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        val messageTextView = dialogView.findViewById<TextView>(R.id.dlgTvConfirm)
        val confirmButton = dialogView.findViewById<Button>(R.id.btnConfirm)

        messageTextView.text = "카테고리를 선택해주세요."

        confirmButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showAlreadyAddedDialog() {
        val dialogView = layoutInflater.inflate(R.layout.confirm_dialog, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        val messageTextView = dialogView.findViewById<TextView>(R.id.dlgTvConfirm)
        val confirmButton = dialogView.findViewById<Button>(R.id.btnConfirm)

        messageTextView.text = "이미 진행 중이거나 완료된 챌린지입니다. 다시 선택해 주세요!"

        confirmButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun isChallengeAlreadyAdded(category: String, detail: String): Boolean {
        val sharedPreferences = requireContext().getSharedPreferences("challenge_prefs",  Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("item_list", null)
        if (json != null) {
            val type = object : TypeToken<MutableList<ChallengeListItem>>() {}.type
            val savedItemList: MutableList<ChallengeListItem> = gson.fromJson(json, type)
            return savedItemList.any { it.challenge_category == category && it.challenge_name == detail }
        }
        return false
    }
}
