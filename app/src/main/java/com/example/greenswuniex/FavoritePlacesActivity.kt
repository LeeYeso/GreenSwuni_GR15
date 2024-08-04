package com.example.greenswuniex

import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.json.JSONArray
import java.io.InputStream


class FavoritePlacesActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FavoritePlacesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite_places)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)


        val backButton: ImageButton = findViewById(R.id.back_button)
        backButton.setOnClickListener {
            onBackPressed()
        }

        loadFavoritePlaces()
    }

    private fun loadFavoritePlaces() {
        val user = auth.currentUser
        user?.let {
            val userId = it.uid
            db.collection("favorites").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val placeIds = document.get("placeIds") as List<String>
                        loadPlacesFromJsonFiles(placeIds)
                    } else {
                        Log.d("FavoritePlacesActivity", "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("FavoritePlacesActivity", "get failed with ", exception)
                }
        }
    }

    private fun loadPlacesFromJsonFiles(placeIds: List<String>) {
        val jsonFiles = arrayOf("bicycle_locations.json", "food_location.json", "store_location.json")
        val favoritePlaces = mutableListOf<FavoritePlace>()

        for (fileName in jsonFiles) {
            val jsonArray = loadJsonArrayFromAsset(fileName)
            for (i in 0 until jsonArray.length()) {
                val placeJson = jsonArray.getJSONObject(i)
                if (placeIds.contains(placeJson.getString("id"))) {
                    favoritePlaces.add(
                        FavoritePlace(
                            placeJson.getString("id"),
                            placeJson.getString("name"),
                            placeJson.getString("address"),
                            placeJson.getDouble("latitude"),
                            placeJson.getDouble("longitude"),
                            placeJson.getString("type"),
                            placeJson.getString("category")
                        )
                    )
                }
            }
        }

        adapter = FavoritePlacesAdapter(favoritePlaces)
        recyclerView.adapter = adapter
    }

    private fun loadJsonArrayFromAsset(fileName: String): JSONArray {
        val json: String?
        try {
            val inputStream: InputStream = assets.open(fileName)
            json = inputStream.bufferedReader().use { it.readText() }
            return JSONArray(json)
        } catch (ex: Exception) {
            ex.printStackTrace()
            return JSONArray()
        }
    }
}