package com.example.greenswuniex

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs


class ChallengeSelectFragment : Fragment() {

    lateinit var Btn1: Button
    lateinit var Btn2: Button
    lateinit var Btn3: Button
    lateinit var Btn4: Button
    lateinit var startBtn: Button

    private val args : ChallengeSelectFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_challenge_select, container, false)

        Btn1 = view.findViewById(R.id.challenge_btn1)
        Btn2 = view.findViewById(R.id.challenge_btn2)
        Btn3 = view.findViewById(R.id.challenge_btn3)
        Btn4 = view.findViewById(R.id.challenge_btn4)
        val backBtn = view.findViewById<AppCompatImageButton>(R.id.challenge_back_btn)
        startBtn = view.findViewById(R.id.challenge_start_btn)

        val argString = args.argCategory

        when (argString) {
            "생활" -> {
                Btn1.text = "샤워시간을 10분이내로 끝내기\n" +
                        "(0.55kg)"
                Btn2.text = "비닐봉투나 종이쇼핑백 대신 장바구니 사용하기\n" +
                        "(개 당 각각 0.003, 0.012kg)"
                Btn3.text = "쓰레기 줍기 및 올바른 분리배출 실천하기\n" +
                        "(회 당 0.241kg)"
                Btn4.text = "추후 입력 예정"
            }

            "식사" -> {
                Btn1.text = "음식물 쓰레기 줄이기\n" +
                        "(0.5kg당 0.04kg)"
                Btn2.text = "일주일에 하루 고기 없는 식사하기\n" +
                        "(한 끼 식사 당 3.64kg)"
                Btn3.text = "일회용 컵 대신 텀블러나 머그잔 사용하기\n" +
                        "(개 당 0.011kg)"
                Btn4.text = "지역에서 생산된 로컬푸드 음식으로 식사\n" +
                        "(하루 한끼 0.5kg)"
            }

            "디지털" -> {
                Btn1.text = "가까운 거리는 도보 혹은 자전거 이용하기\n" +
                        "(2km 기준 2.09kg)"
                Btn2.text = "개인 승용차 대신 지하철을 이용하기\n" +
                        "(1km당 약 약 0.1kg)"
                Btn3.text = "개인 승용차 대신 버스를 이용하기 \n" +
                        "(1km당 약 0.08kg)"
                Btn4.text = "차량을 공유하여 이용하기\n" +
                        "(1인당 1km 0.1km)"
            }

            "이동수단" -> {
                Btn1.text = "사용하지 않는 가전제품 플러그 뽑기\n" +
                        "(1.05kg)"
                Btn2.text = "스마트폰 사용시간 줄이기\n" +
                        "(1MB당 11g)"
                Btn3.text = "스마트폰 스트리밍대신 다운로드\n" +
                        "(1시간 스트리밍 = 자동차 1km 탄소 배출)"
                Btn4.text = "불필요한 이메일 삭제하기(1통 당 0.004kg)"
            }
        }

        backBtn.setOnClickListener {
            findNavController().navigate(R.id.action_challengeSelect_to_challengeCategory)
        }
        return view
    }
}