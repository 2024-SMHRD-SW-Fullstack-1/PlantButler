package com.example.plantbutler

import android.content.Intent
import android.os.Bundle
import android.transition.ChangeBounds
import android.transition.TransitionManager
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val mainLayout = findViewById<ConstraintLayout>(R.id.main)
        val btLogin = findViewById<Button>(R.id.btLogin)
        val loginFormContainer = findViewById<ConstraintLayout>(R.id.loginFormContainer)
        val btnClose = findViewById<ImageView>(R.id.btnClose)

        btLogin.setOnClickListener {
            TransitionManager.beginDelayedTransition(mainLayout, ChangeBounds().setDuration(600))
            btLogin.visibility = View.GONE
            loginFormContainer.visibility = View.VISIBLE
        }

        btnClose.setOnClickListener {
            TransitionManager.beginDelayedTransition(mainLayout, ChangeBounds().setDuration(600))
            btLogin.visibility = View.VISIBLE
            loginFormContainer.visibility = View.GONE
        }

        val loginEmailEditText = findViewById<EditText>(R.id.loginEmailEditText)
        val loginPasswordEditText = findViewById<EditText>(R.id.loginPasswordEditText)
        val loginButton = findViewById<Button>(R.id.loginButton)

        loginButton.setOnClickListener {
            val email = loginEmailEditText.text.toString()
            val password = loginPasswordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                // 로그인 성공 로직 (예: Firebase Auth)
                Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()

                // 로그인 상태 저장
                SessionManager.setLogin(this, true)

                // 메인 액티비티로 전환
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "이메일과 비밀번호를 입력하세요", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
