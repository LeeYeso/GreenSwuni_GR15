package com.example.greenswuniex

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.greenswuniex.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val mailEditText: EditText = binding.mailEditText
        val passwordEditText: EditText = binding.passwordEditText

        // 포커스 변화 리스너 설정
        setFocusChangeListener(mailEditText)
        setFocusChangeListener(passwordEditText)

        // 로그인 버튼 클릭 시
        binding.loginButton.setOnClickListener {
            val email = mailEditText.text.toString()
            val pw = passwordEditText.text.toString()

            if (email.isNotEmpty() && pw.isNotEmpty()) {
                checkUserExistence(email, pw)
            } else {
                showPopup("no_edit")
            }
        }

        // Join 버튼 클릭 시 JoinActivity로 이동
        binding.joinButton.setOnClickListener {
            val intent = Intent(this, JoinActivity::class.java)
            startActivity(intent)
        }

        // 뒤로 가기 버튼 클릭 시 현재 액티비티 종료
        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun setFocusChangeListener(editText: EditText) {
        editText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                editText.setBackgroundResource(R.drawable.edit_text_valid)
            } else {
                editText.setBackgroundResource(R.drawable.edit_text)
            }
        }
    }

    // 사용자 존재 여부 확인
    private fun checkUserExistence(email: String, password: String) {
        firestore.collection("users")
            .document(email)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    signInUser(email, password)
                } else {
                    showPopup("no_user")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("LoginActivity", "checkUserExistence: 데이터 조회 실패", exception)
                showPopup("login_fail")
            }
    }

    // 로그인 시도 및 패스워드 검증
    private fun signInUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    checkUserVerification(email)
                } else {
                    showPopup("wrong_password")
                }
            }
    }

    // 인증 상태 확인
    private fun checkUserVerification(email: String) {
        firestore.collection("users")
            .document(email)
            .get()
            .addOnSuccessListener { document ->
                val isVerified = document.getBoolean("isVerified") ?: false
                if (isVerified) {
                    updateLoginDate(email)
                    navigateToMainPage()
                } else {
                    showPopup("not_verified")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("LoginActivity", "checkUserVerification: 데이터 조회 실패", exception)
                showPopup("login_fail")
            }
    }

    // 로그인 날짜 업데이트
    private fun updateLoginDate(email: String) {
        val loginDate = Date()

        firestore.collection("users")
            .document(email)
            .update("loginDate", loginDate)
            .addOnSuccessListener {
                Log.d("LoginActivity", "Login date updated successfully")
            }
            .addOnFailureListener { e ->
                Log.e("LoginActivity", "Error updating login date", e)
            }
    }

    private fun showPopup(popupId: String) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.confirm_dialog, null)
        val dialogMessage = dialogView.findViewById<TextView>(R.id.dlgTvConfirm)
        val dialogButton = dialogView.findViewById<TextView>(R.id.btnConfirm)

        when (popupId) {
            "no_user" -> dialogMessage.text = "사용자를 찾을 수 없습니다."
            "not_verified" -> dialogMessage.text = "이메일 인증이 필요합니다."
            "wrong_password" -> dialogMessage.text = "잘못된 비밀번호입니다."
            "no_edit" -> dialogMessage.text = "이메일과 비밀번호를 입력하세요."
            else -> dialogMessage.text = "로그인에 실패했습니다."
        }

        val alertDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialogButton.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun navigateToMainPage() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
