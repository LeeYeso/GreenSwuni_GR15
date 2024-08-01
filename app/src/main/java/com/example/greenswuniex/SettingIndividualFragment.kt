package com.example.greenswuniex

import android.app.AlertDialog
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.greenswuniex.databinding.SettingIndividualBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SettingIndividualFragment : Fragment() {

    private var _binding: SettingIndividualBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private val invalidBackground: Drawable? by lazy {
        context?.let { ContextCompat.getDrawable(it, R.drawable.edit_text_yellow_invalid) }
    }
    private val validBackground: Drawable? by lazy {
        context?.let { ContextCompat.getDrawable(it, R.drawable.edit_text_yellow_valid) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = SettingIndividualBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        binding.individualBackBtn.setOnClickListener {
            findNavController().navigate(R.id.action_individual_to_setting)
        }

        binding.nameEditText.addTextChangedListener(createTextWatcher { validateName() })
        binding.phoneEditText.addTextChangedListener(createTextWatcher { validatePhone() })
        binding.emailEditText.addTextChangedListener(createTextWatcher { validateEmail() })

        binding.individualChangeBtn.setOnClickListener {
            if (validateInputFields()) {
                updateUserInfo()
            } else {
                showPopup("invalid_input")
            }
        }

        // 포커스 변화 리스너 설정
        setFocusChangeListener(binding.nameEditText, ::validateName)
        setFocusChangeListener(binding.phoneEditText, ::validatePhone)
        setFocusChangeListener(binding.emailEditText, ::validateEmail)
    }

    private fun createTextWatcher(validationFunc: () -> Unit): TextWatcher {
        return object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                validationFunc()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
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

    private fun validateEmail() {
        val email = binding.emailEditText.text.toString()
        if (email.isEmpty()) {
            binding.emailEditText.background = invalidBackground
            binding.passwordEmptyError.visibility = View.VISIBLE
            binding.passwordError.visibility = View.GONE
        } else if (!isValidEmail(email)) {
            binding.emailEditText.background = invalidBackground
            binding.passwordEmptyError.visibility = View.GONE
            binding.passwordError.visibility = View.VISIBLE
        } else {
            binding.emailEditText.background = validBackground
            binding.passwordEmptyError.visibility = View.GONE
            binding.passwordError.visibility = View.GONE
        }
    }

    private fun validateInputFields(): Boolean {
        validateName()
        validatePhone()
        validateEmail()

        return binding.nameEmptyError.visibility == View.GONE &&
                binding.phoneEmptyError.visibility == View.GONE &&
                binding.phoneError.visibility == View.GONE &&
                binding.passwordEmptyError.visibility == View.GONE &&
                binding.passwordError.visibility == View.GONE
    }

    private fun updateUserInfo() {
        val user = auth.currentUser
        val newPassword = binding.emailEditText.text.toString()

        user?.updatePassword(newPassword)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userName = binding.nameEditText.text.toString()
                    val userPhone = binding.phoneEditText.text.toString()
                    saveUserData(userName, userPhone)
                } else {
                    Log.e("SettingIndividualFragment", "Password update failed")
                }
            }
    }

    private fun saveUserData(userName: String, userPhone: String) {
        val userMail = auth.currentUser?.email ?: return
        val user = hashMapOf(
            "username" to userName,
            "phone" to userPhone
        )

        firestore.collection("users").document(userMail)
            .update(user as Map<String, Any>)
            .addOnSuccessListener {
                showPopup("update_success")
            }
            .addOnFailureListener { e ->
                Log.e("SettingIndividualFragment", "Error updating user data", e)
            }
    }

    private fun showPopup(popupId: String) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.confirm_dialog, null)
        val dialogMessage = dialogView.findViewById<TextView>(R.id.dlgTvConfirm)
        val dialogButton = dialogView.findViewById<Button>(R.id.btnConfirm)

        when (popupId) {
            "update_success" -> dialogMessage.text = "변경이 완료되었습니다."
            "invalid_input" -> dialogMessage.text = "다시 입력해주세요."
        }

        val alertDialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .create()

        dialogButton.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun isValidPhone(phone: String): Boolean {
        val phonePattern = "^\\d{10,11}$".toRegex()
        return phone.matches(phonePattern)
    }

    private fun isValidEmail(email: String): Boolean {
        val emailPattern = "^[a-zA-Z0-9._%+-]+@swu\\.ac\\.kr$".toRegex()
        return email.matches(emailPattern)
    }

    private fun setFocusChangeListener(editText: EditText, validationFunc: () -> Unit) {
        editText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                validationFunc()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
