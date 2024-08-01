package com.example.greenswuniex

import android.app.AlertDialog
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.greenswuniex.databinding.ActivityJoinBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class JoinActivity : AppCompatActivity() {

    // 뷰 바인딩
    private lateinit var binding: ActivityJoinBinding

    // 파이어베이스 선언
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    // 배경 설정
    private val invalidBackground: Drawable? by lazy {
        ContextCompat.getDrawable(this, R.drawable.edit_text_invalid)
    }
    private val validBackground: Drawable? by lazy {
        ContextCompat.getDrawable(this, R.drawable.edit_text_valid)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJoinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // FirebaseAuth 및 Firestore 초기화
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // TextWatcher 설정
        binding.idText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                validateId()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                validatePassword()
                if (binding.passwordEditText2.text.isNotEmpty()) {
                    validatePasswordConfirm()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.passwordEditText2.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                validatePasswordConfirm()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.nameEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                validateName()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.mailEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                validateEmail()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.phoneEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                validatePhone()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // 이전 버튼 리스너
        binding.backButton.setOnClickListener {
            finish()
        }

        // 가입 버튼 클릭 리스너
        binding.joinButton2.setOnClickListener {
            // 유효성 검사를 통과하면
            if (validateInputFields()) {
                saveUserToDatabase()
            }
        }
    }

    // 사용자 데이터 저장
    private fun saveUserToDatabase() {
        val userId = binding.idText.text.toString()
        val userPassword = binding.passwordEditText.text.toString()
        val userName = binding.nameEditText.text.toString()
        val userPhone = binding.phoneEditText.text.toString()
        val userMail = binding.mailEditText.text.toString()

        auth.createUserWithEmailAndPassword(userMail, userPassword)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    saveUserData(userPassword, userMail, userName, userPhone)
                    navigateToVerifyActivity(userMail)
                } else {
                    showPopup("duplicate_email")
                }
            }
    }


    private fun showPopup(popupId: String) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.confirm_dialog, null)
        val dialogMessage = dialogView.findViewById<TextView>(R.id.dlgTvConfirm)
        val dialogButton = dialogView.findViewById<TextView>(R.id.btnConfirm)

        when (popupId) {
            "duplicate_email" -> dialogMessage.text = "중복된 이메일입니다."
            else -> dialogMessage.text = "필수 입력값을 입력해주세요."
        }

        val alertDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialogButton.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }


    private fun saveUserData(userPassword: String, userMail: String, userName: String, userPhone: String) {
        val user = hashMapOf(
            "password" to userPassword,
            "email" to userMail,
            "username" to userName,
            "phone" to userPhone,
            "isVerified" to false,
            "verificationDate" to null,
            "student" to "",
            "major" to "",
            "level" to "",
            "loginDate" to null
        )

        firestore.collection("users").document(userMail)
            .set(user)
            .addOnSuccessListener {
                Log.d("JoinActivity", "User data added successfully")
            }
            .addOnFailureListener { e ->
                Log.e("JoinActivity", "Error adding user data", e)
            }
    }

    private fun validateInputFields(): Boolean {
        validateId()
        validatePassword()
        validatePasswordConfirm()
        validateEmail()
        validateName()
        validatePhone()

        return binding.idEmptyError.visibility == View.GONE &&
                binding.passwordEmptyError.visibility == View.GONE &&
                binding.passwordError.visibility == View.GONE &&
                binding.pwEmptyError.visibility == View.GONE &&
                binding.passwordMismatchError.visibility == View.GONE &&
                binding.mailEmptyError.visibility == View.GONE &&
                binding.emailError.visibility == View.GONE &&
                binding.nameEmptyError.visibility == View.GONE &&
                binding.phoneEmptyError.visibility == View.GONE &&
                binding.phoneError.visibility == View.GONE
    }

    // 유효성 조건 및 검사 부분
    fun validateId() {
        val id = binding.idText.text.toString()
        if (id.isEmpty()) {
            binding.idText.background = invalidBackground
            binding.idEmptyError.visibility = View.VISIBLE
            binding.idError.visibility = View.GONE
        } else if (!isValidId(id)) {
            binding.idText.background = invalidBackground
            binding.idEmptyError.visibility = View.GONE
            binding.idError.visibility = View.VISIBLE
        } else {
            binding.idText.background = validBackground
            binding.idEmptyError.visibility = View.GONE
            binding.idError.visibility = View.GONE
        }
    }

    private fun validatePassword() {
        val password = binding.passwordEditText.text.toString()
        if (password.isEmpty()) {
            binding.passwordEditText.background = invalidBackground
            binding.passwordEmptyError.visibility = View.VISIBLE
            binding.passwordError.visibility = View.GONE
        } else if (!isValidPassword(password)) {
            binding.passwordEditText.background = invalidBackground
            binding.passwordEmptyError.visibility = View.GONE
            binding.passwordError.visibility = View.VISIBLE
        } else {
            binding.passwordEditText.background = validBackground
            binding.passwordEmptyError.visibility = View.GONE
            binding.passwordError.visibility = View.GONE
        }
    }

    private fun validatePasswordConfirm() {
        val password = binding.passwordEditText.text.toString()
        val passwordConfirm = binding.passwordEditText2.text.toString()

        if (passwordConfirm.isEmpty()) {
            binding.passwordEditText2.background = invalidBackground
            binding.pwEmptyError.visibility = View.VISIBLE
            binding.passwordMismatchError.visibility = View.GONE
        } else if (password != passwordConfirm) {
            binding.passwordEditText2.background = invalidBackground
            binding.pwEmptyError.visibility = View.GONE
            binding.passwordMismatchError.visibility = View.VISIBLE
        } else {
            binding.passwordEditText2.background = validBackground
            binding.pwEmptyError.visibility = View.GONE
            binding.passwordMismatchError.visibility = View.GONE
        }
    }

    private fun validateEmail() {
        val email = binding.mailEditText.text.toString()
        if (email.isEmpty()) {
            binding.mailEditText.background = invalidBackground
            binding.mailEmptyError.visibility = View.VISIBLE
            binding.emailError.visibility = View.GONE
        } else if (!isValidEmail(email)) {
            binding.mailEditText.background = invalidBackground
            binding.mailEmptyError.visibility = View.GONE
            binding.emailError.visibility = View.VISIBLE
        } else {
            binding.mailEditText.background = validBackground
            binding.mailEmptyError.visibility = View.GONE
            binding.emailError.visibility = View.GONE
        }
    }

    private fun validateName() {
        val name = binding.nameEditText.text.toString()
        if (name.isEmpty()) {
            binding.nameEditText.background = invalidBackground
            binding.nameEmptyError.visibility = View.VISIBLE
        } else {
            binding.nameEditText.background = validBackground
            binding.nameEmptyError.visibility = View.GONE
        }
    }

    private fun validatePhone() {
        val phone = binding.phoneEditText.text.toString()
        if (phone.isEmpty()) {
            binding.phoneEditText.background = invalidBackground
            binding.phoneEmptyError.visibility = View.VISIBLE
            binding.phoneError.visibility = View.GONE
        } else if (!isValidPhone(phone)) {
            binding.phoneEditText.background = invalidBackground
            binding.phoneEmptyError.visibility = View.GONE
            binding.phoneError.visibility = View.VISIBLE
        } else {
            binding.phoneEditText.background = validBackground
            binding.phoneEmptyError.visibility = View.GONE
            binding.phoneError.visibility = View.GONE
        }
    }

    // ID 유효성 검사 패턴 함수
    private fun isValidId(id: String): Boolean {
        val idPattern = "^.{2,}$".toRegex()
        return id.matches(idPattern)
    }

    private fun isValidPassword(password: String): Boolean {
        val passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\|,.<>/?]).{6,}$".toRegex()
        return password.matches(passwordPattern)
    }

    private fun isValidEmail(email: String): Boolean {
        val emailPattern = "^[a-zA-Z0-9._%+-]+@swu\\.ac\\.kr$".toRegex()
        return email.matches(emailPattern)
    }

    private fun isValidPhone(phone: String): Boolean {
        val phonePattern = "^\\d{10,11}$".toRegex()  // 전화번호는 10-11자리의 숫자로 제한
        return phone.matches(phonePattern)
    }

    private fun navigateToVerifyActivity(userMail: String) {
        val intent = Intent(this, EmailActivity::class.java)
        intent.putExtra("USER_EMAIL", userMail)
        startActivity(intent)
        finish()  // 현재 액티비티를 종료하여 뒤로가기 버튼으로 다시 돌아오지 않도록 한다.
    }
}
