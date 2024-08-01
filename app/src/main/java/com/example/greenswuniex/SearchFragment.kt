package com.example.greenswuniex

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.naver.maps.map.MapFragment
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.NaverMap

class SearchFragment : Fragment(), OnMapReadyCallback {

    private var naverMap: NaverMap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // SearchFragment의 레이아웃을 인플레이트
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 버튼과 초기 화면, 지도 컨테이너를 찾음
        val findButton: Button = view.findViewById(R.id.find_button)
        val initialScreen: View = view.findViewById(R.id.initial_screen)
        val mapContainer: View = view.findViewById(R.id.map_container)

        // 초기 화면을 숨기고 지도 컨테이너를 숨김
        mapContainer.visibility = View.GONE

        // 버튼 클릭 리스너 설정
        findButton.setOnClickListener {
            // 초기 화면 숨기기
            initialScreen.visibility = View.GONE

            // 지도 컨테이너 보이기
            mapContainer.visibility = View.VISIBLE

            // MapFragment 인스턴스 생성
            val mapFragment = MapFragment.newInstance()

            // map_container에 교체
            childFragmentManager.beginTransaction()
                .replace(R.id.map_container, mapFragment)
                .commit()

            // MapFragment의 지도 초기화
            mapFragment.getMapAsync(this)
        }
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap

    }
}
