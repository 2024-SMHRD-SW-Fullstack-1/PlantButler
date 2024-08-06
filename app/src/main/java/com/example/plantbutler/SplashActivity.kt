package com.example.plantbutler

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // 2초 후에 지정된 작업을 실행하기 위해 Handler를 사용
        Handler(Looper.getMainLooper()).postDelayed({
            // 앱이 처음 실행된 경우 회원가입 화면으로 전환
            if (SessionManager.isFirstRun(this)) {
                // 회원가입 액티비티로 이동
                startActivity(Intent(this, SignupActivity::class.java))
                // 첫 실행 여부를 false로 설정
                SessionManager.setFirstRun(this, false)
            } else {
                // 그 외의 경우 로그인 액티비티로 이동
                startActivity(Intent(this, LoginActivity::class.java))
            }
            // 현재 액티비티 종료
            finish()
        }, 1500) // 2초 후 실행
    }
}
