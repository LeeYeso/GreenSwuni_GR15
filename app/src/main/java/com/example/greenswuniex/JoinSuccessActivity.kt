package com.example.greenswuniex

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.greenswuniex.databinding.ActivityJoinSuccessBinding

class JoinSuccessActivity : AppCompatActivity() {

    private lateinit var binding: ActivityJoinSuccessBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityJoinSuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // '시작' 버튼 클릭 시 로그인 페이지로 이동
        binding.startButton.setOnClickListener {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
            finish()
        }
    }
}
