package com.example.greenswuniex

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.content.Intent


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_chellenge, R.id.navigation_search, R.id.navigation_setting
            )
        )
        navView.setupWithNavController(navController)


        // 네비게이션 아이템 선택 리스너 설정
        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    // Home 버튼 클릭 시 HomeFragment로 이동
                    if (navController.currentDestination?.id != R.id.navigation_home) {
                        navController.navigate(R.id.navigation_home)
                    }
                    true
                }
                R.id.navigation_chellenge -> {
                    // Chellenge 버튼 클릭 시 ChellengeFragment로 이동
                    if (navController.currentDestination?.id != R.id.navigation_chellenge) {
                        navController.navigate(R.id.navigation_chellenge)
                    }
                    true
                }
                R.id.navigation_search -> {
                    // Search 버튼 클릭 시 SearchFragment로 이동
                    if (navController.currentDestination?.id != R.id.navigation_search) {
                        navController.navigate(R.id.navigation_search)
                    }
                    true
                }
                R.id.navigation_setting -> {
                    // Setting 버튼 클릭 시 SettingFragment로 이동
                    if (navController.currentDestination?.id != R.id.navigation_setting) {
                        navController.navigate(R.id.navigation_setting)
                    }
                    true
                }
                else -> false
            }
        }
    }
}