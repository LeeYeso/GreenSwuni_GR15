package com.example.greenswuniex

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldPath
import java.util.Date

class EmailActivity : AppCompatActivity() {

    private lateinit var emailTextView: TextView
    private lateinit var majorEditText: EditText
    private lateinit var studentEditText: EditText
    private lateinit var verifyFailLayout: LinearLayout

    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email)

        // Firebase 초기화
        firestore = FirebaseFirestore.getInstance()

        // 뷰 초기화
        emailTextView = findViewById(R.id.email_text_view)
        majorEditText = findViewById(R.id.major)
        studentEditText = findViewById(R.id.student)
        verifyFailLayout = findViewById(R.id.verify_fail)

        // EditText 배경 설정 리스너
        setFocusChangeListener(majorEditText)
        setFocusChangeListener(studentEditText)

        // JoinActivity에서 전달된 데이터 처리
        val email = intent.getStringExtra("USER_EMAIL")
        if (!email.isNullOrEmpty()) {
            emailTextView.text = email
        }

        // 확인 버튼 클릭 시
        findViewById<Button>(R.id.verify_button).setOnClickListener {
            val major = majorEditText.text.toString()
            val student = studentEditText.text.toString()

            if (validateInputs(major, student)) {
                updateUserDataAndCheck(email ?: "", major, student)
            } else {
                showVerifyFailPopup()
            }
        }

        // 인증 실패 팝업 확인 버튼 클릭 시
        findViewById<Button>(R.id.login_fail_button).setOnClickListener {
            verifyFailLayout.visibility = View.GONE
        }

        // 뒤로 가기 버튼 클릭 시 현재 액티비티 종료
        findViewById<ImageButton>(R.id.back_button).setOnClickListener {
            finish()
        }
    }

    // 포커스 변경 시 EditText의 배경 리소스를 변경하는 함수
    private fun setFocusChangeListener(editText: EditText) {
        editText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                // 포커스가 있으면 배경을 valid로 설정
                editText.setBackgroundResource(R.drawable.edit_text_valid)
            } else {
                // 포커스가 없으면 배경을 default로 설정
                editText.setBackgroundResource(R.drawable.edit_text)
            }
        }
    }

    // 입력 필드 유효성 검사
    private fun validateInputs(major: String, student: String): Boolean {
        return major.isNotEmpty() && student.isNotEmpty()
    }

    // 사용자 데이터를 업데이트하고 인증 상태 확인
    private fun updateUserDataAndCheck(email: String, major: String, student: String) {
        val userRef = firestore.collection("users").document(email)

        // 사용자 데이터를 업데이트
        userRef.update(
            mapOf(
                "major" to major,
                "student" to student
            )
        )
            .addOnSuccessListener {
                // 업데이트가 성공한 후, 자신을 제외한 다른 문서에서 중복 여부 확인
                checkStudentExists(email, student)
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
                showVerifyFailPopup()
            }
    }

    // 같은 학생 값이 있는지 확인 (현재 문서를 제외)
    private fun checkStudentExists(currentEmail: String, student: String) {
        if (student.isBlank()) {
            // student 값이 비어있거나 공백인 경우 인증 실패 처리
            showVerifyFailPopup()
            return
        }

        // 현재 문서의 ID를 직접 사용하여 중복 여부 확인
        firestore.collection("users")
            .whereEqualTo("student", student)
            .get()
            .addOnSuccessListener { documents ->
                // 현재 문서를 제외한 나머지 문서에서 중복 여부를 확인
                val isDuplicate = documents.any { it.id != currentEmail }

                if (!isDuplicate) {
                    // student 값이 중복되지 않는 경우, 인증 상태 업데이트
                    updateUserVerificationStatus(currentEmail)
                } else {
                    // student 값이 중복되는 경우, 인증 실패 처리
                    showVerifyFailPopup()
                }
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
                showVerifyFailPopup()
            }
    }

    // 사용자 인증 상태 업데이트
    private fun updateUserVerificationStatus(email: String) {
        val userRef = firestore.collection("users").document(email)

        userRef.update(
            mapOf(
                "isVerified" to true,
                "verificationDate" to Date()
            )
        )
            .addOnSuccessListener {
                // 인증 성공 후 JoinSuccessActivity로 이동
                val intent = Intent(this, JoinSuccessActivity::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
                showVerifyFailPopup()
            }
    }

    // 인증 실패 팝업 메시지 표시
    private fun showVerifyFailPopup() {
        verifyFailLayout.visibility = View.VISIBLE
    }


}
