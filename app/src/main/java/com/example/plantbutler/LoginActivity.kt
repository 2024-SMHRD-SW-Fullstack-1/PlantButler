package com.example.plantbutler

import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.os.Handler
import android.transition.ChangeBounds
import android.transition.TransitionManager
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson


class LoginActivity : AppCompatActivity() {

    lateinit var queue: RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // 애니메이션 로드
        val slideUpAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_up)

        // 레이아웃 요소들을 변수에 할당
        val mainLayout = findViewById<ConstraintLayout>(R.id.main)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnJoin = findViewById<Button>(R.id.btnJoin)

        val loginFormContainer = findViewById<ConstraintLayout>(R.id.loginFormContainer)
        val btnClose = findViewById<ImageView>(R.id.btnClose)

        // 뷰가 처음에는 보이지 않게 설정
        btnLogin.visibility = View.INVISIBLE
        btnJoin.visibility = View.INVISIBLE


        // 1초 후에 애니메이션 시작
        Handler().postDelayed({
            btnLogin.startAnimation(slideUpAnimation)
            btnLogin.visibility = View.VISIBLE
        }, 1000)

        // 1초 후에 애니메이션 시작
        Handler().postDelayed({
            btnJoin.startAnimation(slideUpAnimation)
            btnJoin.visibility = View.VISIBLE
        }, 1000)

        // 로그인 버튼 클릭 시 로그인 폼을 애니메이션으로 표시
        btnLogin.setOnClickListener {
            TransitionManager.beginDelayedTransition(mainLayout, ChangeBounds().setDuration(600))
            btnLogin.visibility = View.GONE
            btnJoin.visibility = View.GONE
            loginFormContainer.visibility = View.VISIBLE
        }

        // 회원가입
        btnJoin.setOnClickListener {
            val intent = Intent(this,JoinActivity::class.java)
            startActivity(intent)
        }

        // 닫기 버튼 클릭 시 로그인 폼을 애니메이션으로 숨김
        btnClose.setOnClickListener {
            TransitionManager.beginDelayedTransition(mainLayout, ChangeBounds().setDuration(600))
            btnLogin.visibility = View.VISIBLE
            btnJoin.visibility = View.VISIBLE
            loginFormContainer.visibility = View.GONE
        }

        // 로그인 변수
        val etLoginId = findViewById<EditText>(R.id.etJoinId)
        val etLoginPw = findViewById<EditText>(R.id.etJoinPw)
        val btnLoginAct = findViewById<Button>(R.id.joinButton)

        queue  = Volley.newRequestQueue(this)
        // 로그인
        btnLoginAct.setOnClickListener {
            val inputId = etLoginId.text.toString()
            val inputPw = etLoginPw.text.toString()
            val pm = Member(inputId, inputPw, null, null)

            val request = object: StringRequest(
                Request.Method.POST,
                "http://192.168.219.41:8089/plantbutler/login",
                {response->
                    Log.d("response", response.toString())

                    if(response.toString() != "") {
                        intent.putExtra("member", response.toString())
                        val member = Gson().fromJson(intent.getStringExtra("member"), Member::class.java)
                        // 로그인 회원정보 저장
                        val sharedPreferences = getSharedPreferences("member", MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        // id, nick, img
                        editor.putString("memId", member.id)
                        editor.putString("memPw", member.pw)
                        editor.putString("memNick", member.nick)
                        editor.putString("memImg", member.img)
                        editor.apply()

                        Toast.makeText(applicationContext, "${member.nick}님 반갑습니다!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(applicationContext, "아이디나 비밀번호를 확인해주세요", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                    }
                    finishAffinity()
                },
                { error->
                    Log.d("error", error.toString())
                }
            ){
                override fun getParams(): MutableMap<String, String> {
                    val params: MutableMap<String, String> = HashMap()
                    params["Member"] = Gson().toJson(pm)
                    return params
                }
            }
            queue.add(request)
        }
    }
}
