package com.example.plantbutler

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 회원가입 버튼 추가
        val signupButton = findViewById<Button>(R.id.signupButton)
        signupButton.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

        // 로그아웃 버튼 클릭 리스너
        val logoutButton = findViewById<Button>(R.id.logoutButton)
        logoutButton.setOnClickListener {
            SessionManager.setLogin(this, false)
            Toast.makeText(this, "로그아웃 되었습니다", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
