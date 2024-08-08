package com.example.plantbutler

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.GsonBuilder
import java.time.LocalDateTime

class AddNoteActivity : AppCompatActivity() {

    private var myplantIdx: Int? = null
    private lateinit var etContent: EditText
    private lateinit var tvCharCount: TextView
    private lateinit var btnSubmit: Button

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)

        myplantIdx = intent.getIntExtra("myplant_idx", -1)

        etContent = findViewById(R.id.etContent)
        tvCharCount = findViewById(R.id.tvCharCount)
        btnSubmit = findViewById(R.id.btnSubmit)

        // 텍스트 변경 리스너 추가
        etContent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val length = s.length
                tvCharCount.text = "$length/5000"
            }
            override fun afterTextChanged(s: Editable) {}
        })

        btnSubmit.setOnClickListener {
            submitNote()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun submitNote() {
        val content = etContent.text.toString()

        if (content.isEmpty()) {
            Toast.makeText(this, "모든 필드를 입력하세요", Toast.LENGTH_SHORT).show()
            return
        }

        val date = LocalDateTime.now() // 현재 날짜 및 시간을 사용

        val note = Note(
            plantId = myplantIdx ?: -1,
            date = date.toString(),
            content = content
        )

        val queue = Volley.newRequestQueue(this)
        val url = "http://192.168.219.41:8089/plantbutler/api/notes"

        val gson = GsonBuilder().create()
        val requestBody = gson.toJson(note)

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            { response ->
                Toast.makeText(this, "노트 추가 완료", Toast.LENGTH_SHORT).show()
                setResult(Activity.RESULT_OK)
                finish()
            },
            { error ->
                Toast.makeText(this, "에러 발생: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }

            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }
        }

        queue.add(stringRequest)
    }
}
