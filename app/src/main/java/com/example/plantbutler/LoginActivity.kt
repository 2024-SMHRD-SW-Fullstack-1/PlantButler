package com.example.plantbutler

import android.content.Intent
import android.os.Bundle
import android.transition.ChangeBounds
import android.transition.TransitionManager
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
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

        // 레이아웃 요소들을 변수에 할당
        val mainLayout = findViewById<ConstraintLayout>(R.id.main)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val loginFormContainer = findViewById<ConstraintLayout>(R.id.loginFormContainer)
        val btnClose = findViewById<ImageView>(R.id.btnClose)

        // 로그인 버튼 클릭 시 로그인 폼을 애니메이션으로 표시
        btnLogin.setOnClickListener {
            TransitionManager.beginDelayedTransition(mainLayout, ChangeBounds().setDuration(600))
            btnLogin.visibility = View.GONE
            loginFormContainer.visibility = View.VISIBLE
        }

        // 닫기 버튼 클릭 시 로그인 폼을 애니메이션으로 숨김
        btnClose.setOnClickListener {
            TransitionManager.beginDelayedTransition(mainLayout, ChangeBounds().setDuration(600))
            btnLogin.visibility = View.VISIBLE
            loginFormContainer.visibility = View.GONE
        }

        // 로그인 변수
        val etLoginId = findViewById<EditText>(R.id.etLoginId)
        val etLoginPw = findViewById<EditText>(R.id.etLoginPw)
        val btnLoginAct = findViewById<Button>(R.id.loginButton)

        queue  = Volley.newRequestQueue(this)

        btnLoginAct.setOnClickListener {
            val inputId = etLoginId.text.toString()
            val inputPw = etLoginPw.text.toString()
            val pm = Member(inputId, inputPw, null, null)

            val request = object: StringRequest(
                Request.Method.POST,
                "http://192.168.219.41:8089/plantbutler/login",
                {response->
                    Log.d("response",response.toString())
                    if(response.toString()!=""){
                        Toast.makeText(applicationContext,"로그인성공",Toast.LENGTH_SHORT).show()
                        // 로그인 성공 시 로그인한 사용자 ID를 다음 액티비티로 전달
                        val intent = Intent(this, MainActivity::class.java).apply {
                            putExtra("memberId", inputId) // 로그인한 사용자의 ID를 전달
                        }
                        startActivity(intent)
                        finish()
                    }else{
                        Toast.makeText(applicationContext,"로그인실패",Toast.LENGTH_SHORT).show()
                    }
                },
                {error->
                    Log.d("error",error.toString())
                }
            ){
                override fun getParams():MutableMap<String,String>{
                    val params:MutableMap<String,String> = HashMap()
                    params["Member"] = Gson().toJson(pm)
                    return params
                }
            }
            queue.add(request)
        }
    }
}
