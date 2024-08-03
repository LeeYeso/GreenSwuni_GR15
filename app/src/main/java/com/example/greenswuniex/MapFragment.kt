package com.example.greenswuniex

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.util.FusedLocationSource
import org.json.JSONArray
import java.io.IOException

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mapView: MapView
    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap

    private lateinit var topBar: ConstraintLayout
    private lateinit var closeButton: Button
    private lateinit var school2: Button
    private lateinit var foodText: TextView
    private lateinit var storeText: TextView
    private lateinit var bicycleText: TextView
    private lateinit var backButton: Button

    private lateinit var bottomBar: LinearLayout
    private lateinit var markerName: TextView
    private lateinit var markerType: TextView
    private lateinit var markerAddress: TextView
    private lateinit var markerCategory: TextView

    private lateinit var favoriteButton: ImageButton
    private lateinit var favoriteFullButton: ImageButton

    private val foodMarkers = mutableListOf<Marker>()
    private val storeMarkers = mutableListOf<Marker>()
    private val bicycleMarkers = mutableListOf<Marker>()

    private val firestore: FirebaseFirestore by lazy { Firebase.firestore }
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        createUserFavoritesDocument()

        mapView = view.findViewById(R.id.map_view)
        mapView.onCreate(savedInstanceState)

        // 하단바 요소
        bottomBar = view.findViewById(R.id.bottom_bar)
        markerName = view.findViewById(R.id.marker_name)
        markerType = view.findViewById(R.id.marker_type)
        markerAddress = view.findViewById(R.id.marker_address)
        markerCategory = view.findViewById(R.id.marker_category)
        favoriteButton = view.findViewById(R.id.favorite_button)
        favoriteFullButton = view.findViewById(R.id.favorite_full_button)

        // 상단바 요소
        topBar = view.findViewById(R.id.top_bar)
        closeButton = view.findViewById(R.id.close_button)
        school2 = view.findViewById(R.id.school2)
        foodText = view.findViewById(R.id.food_text)
        storeText = view.findViewById(R.id.store_text)
        bicycleText = view.findViewById(R.id.bicycle_text)
        backButton = view.findViewById(R.id.back_button)

        val schoolButton2 = view.findViewById<Button>(R.id.school_button2)
        val outSchool = view.findViewById<LinearLayout>(R.id.out_school)

        val foodButton = view.findViewById<Button>(R.id.food)
        val storeButton = view.findViewById<Button>(R.id.store)
        val bicycleButton = view.findViewById<Button>(R.id.bicycle)

        // 메뉴 버튼 클릭 시
        view.findViewById<Button>(R.id.map_menu).setOnClickListener {
            schoolButton2.visibility = View.VISIBLE
            outSchool.visibility = View.GONE
        }

        schoolButton2.setOnClickListener {
            outSchool.visibility = View.VISIBLE
        }

        foodButton.setOnClickListener {
            showMarkers("food")
            topBar.visibility = View.VISIBLE
            outSchool.visibility = View.GONE
            closeButton.visibility = View.VISIBLE
            school2.visibility = View.VISIBLE
            foodText.visibility = View.VISIBLE
            storeText.visibility = View.GONE
            bicycleText.visibility = View.GONE
        }

        storeButton.setOnClickListener {
            showMarkers("store")
            topBar.visibility = View.VISIBLE
            outSchool.visibility = View.GONE
            closeButton.visibility = View.VISIBLE
            school2.visibility = View.VISIBLE
            foodText.visibility = View.GONE
            storeText.visibility = View.VISIBLE
            bicycleText.visibility = View.GONE
        }

        bicycleButton.setOnClickListener {
            showMarkers("bicycle")
            topBar.visibility = View.VISIBLE
            outSchool.visibility = View.GONE
            closeButton.visibility = View.VISIBLE
            school2.visibility = View.VISIBLE
            foodText.visibility = View.GONE
            storeText.visibility = View.GONE
            bicycleText.visibility = View.VISIBLE
        }

        backButton.setOnClickListener {
            topBar.visibility = View.GONE
            showMarkers("")
            bottomBar.visibility = View.GONE
        }

        closeButton.setOnClickListener {
            topBar.visibility = View.GONE
            schoolButton2.visibility = View.GONE
            showMarkers("")
            bottomBar.visibility = View.GONE
        }

        // 하단 바의 즐겨찾기 버튼 클릭 리스너 설정
        favoriteButton.setOnClickListener {
            val user = auth.currentUser
            user?.let {
                val placeId = favoriteButton.tag as String ?: return@let
                toggleFavorite(user.uid, placeId, true)
            }
        }

        favoriteFullButton.setOnClickListener {
            val user = auth.currentUser
            user?.let {
                val placeId = favoriteFullButton.tag as String ?: return@let
                toggleFavorite(user.uid, placeId, false)
            }
        }

        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
        mapView.getMapAsync(this)
    }

    private fun createUserFavoritesDocument() {
        val user = auth.currentUser
        user?.let {
            val userId = it.uid  // 사용자 ID를 사용하여 문서 ID 설정

            // Firestore 컬렉션 "favorites"에 사용자 문서 생성
            val favoritesRef = firestore.collection("favorites").document(userId)

            // Firestore 문서 생성 또는 업데이트
            favoritesRef.get()
                .addOnSuccessListener { document ->
                    if (!document.exists()) {
                        // 문서가 존재하지 않으면 새로 생성
                        favoritesRef.set(mapOf("placeIds" to emptyList<String>()))
                            .addOnSuccessListener {
                                Log.d("MapFragment", "User favorites document created")
                            }
                            .addOnFailureListener { e ->
                                Log.e("MapFragment", "Error creating user favorites document", e)
                            }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("MapFragment", "Error checking user favorites document", e)
                }
        }
    }

    // 찜하기 및 찜해제
    private fun toggleFavorite(userId: String, placeId: String, add: Boolean) {
        val favoritesRef = firestore.collection("favorites").document(userId)

        if (add) {
            // Add favorite
            favoritesRef.update("placeIds", FieldValue.arrayUnion(placeId))
                .addOnSuccessListener {
                    favoriteButton.visibility = View.GONE
                    favoriteFullButton.visibility = View.VISIBLE
                    Log.d("MapFragment", "Successfully added favorite: $placeId")
                }
                .addOnFailureListener { e ->
                    Log.e("MapFragment", "Error adding favorite", e)
                }
        } else {
            // Remove favorite
            favoritesRef.update("placeIds", FieldValue.arrayRemove(placeId))
                .addOnSuccessListener {
                    favoriteFullButton.visibility = View.GONE
                    favoriteButton.visibility = View.VISIBLE
                    Log.d("MapFragment", "Successfully removed favorite: $placeId")
                }
                .addOnFailureListener { e ->
                    Log.e("MapFragment", "Error removing favorite", e)
                }
        }
    }



    private fun addFavorite(userId: String, placeId: String) {
        val favoritesRef = firestore.collection("favorites").document(userId)

        favoritesRef.update("placeIds", FieldValue.arrayUnion(placeId))
            .addOnSuccessListener {
                Log.d("MapFragment", "Successfully added favorite: $placeId")
                favoriteButton.visibility = View.GONE
                favoriteFullButton.visibility = View.VISIBLE
            }
            .addOnFailureListener { e ->
                Log.e("MapFragment", "Error adding favorite", e)
            }
    }


    private fun removeFavorite(userId: String, placeId: String) {
        val favoritesRef = firestore.collection("favorites").document(userId)

        favoritesRef.update("placeIds", FieldValue.arrayRemove(placeId))
            .addOnSuccessListener {
                Log.d("MapFragment", "Successfully removed favorite: $placeId")
                favoriteFullButton.visibility = View.GONE
                favoriteButton.visibility = View.VISIBLE
            }
            .addOnFailureListener { e ->
                Log.e("MapFragment", "Error removing favorite", e)
            }
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        naverMap.locationSource = locationSource

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            enableLocationTracking()
            moveCameraToCurrentLocation()
        }
        addMarkersFromAssets()
    }

    private fun addMarkersFromAssets() {
        val foodMarkerInfos = loadMarkersFromJSON(requireContext(), "food_location.json")
        val storeMarkerInfos = loadMarkersFromJSON(requireContext(), "store_location.json")
        val bicycleMarkerInfos = loadMarkersFromJSON(requireContext(), "bicycle_locations.json")

        foodMarkers.addAll(createMarkers(foodMarkerInfos))
        storeMarkers.addAll(createMarkers(storeMarkerInfos))
        bicycleMarkers.addAll(createMarkers(bicycleMarkerInfos))
    }

    private fun loadMarkersFromJSON(context: Context, fileName: String): List<MarkerInfo> {
        val jsonString = loadJSONFromAsset(context, fileName) ?: return emptyList()
        Log.d("MapFragment", "Loaded JSON for $fileName: $jsonString")
        val gson = Gson()
        val foodStoreLocationType = object : TypeToken<List<Location>>() {}.type
        val locations: List<Location> = gson.fromJson(jsonString, foodStoreLocationType) ?: emptyList()
        return locations.map {
            MarkerInfo(
                it.id, it.name, it.type, it.address, it.category, LatLng(it.latitude, it.longitude)
            )
        }
    }

    private fun loadJSONFromAsset(context: Context, fileName: String): String? {
        return try {
            val inputStream = context.assets.open(fileName)
            val buffer = ByteArray(inputStream.available())
            inputStream.read(buffer)
            inputStream.close()
            String(buffer)
        } catch (e: IOException) {
            Log.e("MapFragment", "Error reading JSON file", e)
            null
        }
    }

    private fun createMarkers(markerInfos: List<MarkerInfo>): List<Marker> {
        return markerInfos.map { markerInfo ->
            Marker().apply {
                position = markerInfo.position
                icon = OverlayImage.fromBitmap(createBitmapFromDrawable(requireContext().getDrawable(R.drawable.custom_marker)))
                map = null // 마커를 처음에는 지도에 표시되지 않도록 설정
                setOnClickListener {
                    showMarkerInfo(markerInfo) // MarkerInfo 객체를 넘김
                    true
                }
            }
        }
    }

    private fun createBitmapFromDrawable(drawable: Drawable?): Bitmap {
        return if (drawable is BitmapDrawable) {
            drawable.bitmap
        } else {
            val bitmap = Bitmap.createBitmap(drawable?.intrinsicWidth ?: 0, drawable?.intrinsicHeight ?: 0, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawable?.setBounds(0, 0, canvas.width, canvas.height)
            drawable?.draw(canvas)
            bitmap
        }
    }

    // 마커 클릭시
    private fun showMarkerInfo(markerInfo: MarkerInfo) {
        markerName.text = markerInfo.name
        markerType.text = "${markerInfo.type}"
        markerAddress.text = "${markerInfo.address}"
        markerCategory.text = "${markerInfo.category}"
        favoriteButton.tag = markerInfo.id // 버튼에 고유번호를 태그로 설정
        favoriteFullButton.tag = markerInfo.id // 버튼에 고유번호를 태그로 설정
        bottomBar.visibility = View.VISIBLE

        // 즐겨찾기 버튼 상태 업데이트
        val user = auth.currentUser
        user?.let {
            val userId = it.uid
            val favoritesRef = firestore.collection("favorites").document(userId)

            favoritesRef.get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val placeIds = document.get("placeIds") as? List<String> ?: emptyList()
                        if (placeIds.contains(markerInfo.id)) {
                            favoriteButton.visibility = View.GONE
                            favoriteFullButton.visibility = View.VISIBLE
                        } else {
                            favoriteFullButton.visibility = View.GONE
                            favoriteButton.visibility = View.VISIBLE
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("MapFragment", "Error fetching favorite status", e)
                }
        }
    }

    private fun getTypeForMarker(marker: Marker): String {
        return when (marker) {
            in foodMarkers -> "Food"
            in storeMarkers -> "Store"
            in bicycleMarkers -> "Bicycle"
            else -> "Unknown"
        }
    }

    private fun getAddressForMarker(marker: Marker): String {
        // 실제 주소를 반환하는 로직을 추가하세요
        return "Address Placeholder"
    }

    private fun getCategoryForMarker(marker: Marker): String {
        // 실제 카테고리를 반환하는 로직을 추가하세요
        return "Category Placeholder"
    }

    private fun showMarkers(type: String) {
        when (type) {
            "food" -> {
                foodMarkers.forEach { marker ->
                    marker.map = naverMap // food 마커는 지도에 보이도록 설정
                }
                storeMarkers.forEach { marker ->
                    marker.map = null // store 마커는 지도에서 숨김
                }
                bicycleMarkers.forEach { marker ->
                    marker.map = null // bicycle 마커는 지도에서 숨김
                }
            }
            "store" -> {
                foodMarkers.forEach { marker ->
                    marker.map = null // food 마커는 지도에서 숨김
                }
                storeMarkers.forEach { marker ->
                    marker.map = naverMap // store 마커는 지도에 보이도록 설정
                }
                bicycleMarkers.forEach { marker ->
                    marker.map = null // bicycle 마커는 지도에서 숨김
                }
            }
            "bicycle" -> {
                foodMarkers.forEach { marker ->
                    marker.map = null // food 마커는 지도에서 숨김
                }
                storeMarkers.forEach { marker ->
                    marker.map = null // store 마커는 지도에서 숨김
                }
                bicycleMarkers.forEach { marker ->
                    marker.map = naverMap // bicycle 마커는 지도에 보이도록 설정
                }
            }
            else -> {
                // 아무 필터도 없을 때, 모든 마커를 숨김
                foodMarkers.forEach { marker ->
                    marker.map = null
                }
                storeMarkers.forEach { marker ->
                    marker.map = null
                }
                bicycleMarkers.forEach { marker ->
                    marker.map = null
                }
            }
        }
    }

    private fun enableLocationTracking() {
        naverMap.locationTrackingMode = LocationTrackingMode.Follow
    }

    private fun moveCameraToCurrentLocation() {
        naverMap.locationSource = locationSource
        locationSource.lastLocation?.let { location ->
            // Location 객체를 LatLng으로 변환
            val latLng = LatLng(location.latitude, location.longitude)
            naverMap.cameraPosition = CameraPosition(latLng, 15.0)
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
}

data class Location(
    val id: String,
    val name: String,
    val type: String,
    val address: String,
    val category: String,
    val latitude: Double,
    val longitude: Double
)

data class MarkerInfo(
    val id: String,
    val name: String,
    val type: String,
    val address: String,
    val category: String,
    val position: LatLng
)
