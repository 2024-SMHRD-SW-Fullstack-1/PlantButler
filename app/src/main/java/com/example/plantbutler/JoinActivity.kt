package com.example.plantbutler

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson

class JoinActivity : AppCompatActivity() {

    lateinit var queue: RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join)

        val etId = findViewById<EditText>(R.id.etId)
        val etPw = findViewById<EditText>(R.id.etPw)
        val etNick = findViewById<EditText>(R.id.etNick)
        val btnJoinAct = findViewById<Button>(R.id.btnJoinAct)

        queue = Volley.newRequestQueue(this)

        btnJoinAct.setOnClickListener {
            val inputId = etId.text.toString()
            val inputPw = etPw.text.toString()
            val inputNick = etNick.text.toString()
            val pm = Member(inputId,inputPw,inputNick,"profile")

            val request = object: StringRequest(
                Request.Method.POST,
                "http://192.168.219.60:8089/plantbutler/join",
                {response->
                    Log.d("response",response.toString())
                    // 회원가입 후 메인화면
                    val intent = Intent(this,LoginActivity::class.java)
                    startActivity(intent)
                },
                {error->
                    Log.d("error",error.toString())
                }
            ){
                override fun getParams():MutableMap<String,String>{
                    val params:MutableMap<String,String> = HashMap<String,String>()

                    params.put("Member", Gson().toJson(pm))
                    return params
                }
            }

            queue.add(request)

        }

    }
}