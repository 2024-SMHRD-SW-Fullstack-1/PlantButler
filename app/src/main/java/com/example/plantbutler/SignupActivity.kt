package com.example.plantbutler

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SignupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val emailEditText = findViewById<EditText>(R.id.signupEmailEditText)
        val passwordEditText = findViewById<EditText>(R.id.signupPasswordEditText)
        val passwordConfirmEditText = findViewById<EditText>(R.id.signupPasswordConfirmEditText)
        val nicknameEditText = findViewById<EditText>(R.id.signupNicknameEditText)
        val signupButton = findViewById<Button>(R.id.signupButton)

        signupButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val passwordConfirm = passwordConfirmEditText.text.toString()
            val nickname = nicknameEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty() && password == passwordConfirm && nickname.isNotEmpty()) {
                // 회원가입 성공 로직 (예: Firebase Auth)
                Toast.makeText(this, "회원가입 성공", Toast.LENGTH_SHORT).show()

                // 로그인 상태 저장
                SessionManager.setLogin(this, true)

                // 메인 액티비티로 전환
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "모든 필드를 올바르게 입력하세요", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
