package com.example.plantbutler

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import java.util.*

class PlantAddActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plant_add)

        val btnClose = findViewById<ImageView>(R.id.btnClose)
        val btnCheck = findViewById<ImageView>(R.id.btnCheck)

        btnClose.setOnClickListener {
            // 식물 리스트 화면으로 돌아가기
            finish()
        }

        btnCheck.setOnClickListener {
            // 애칭, 목표 등 입력 값 확인 및 저장 로직
            val plantNickname = findViewById<EditText>(R.id.plantNicknameEditText).text.toString()
            val plantGoal = findViewById<EditText>(R.id.plantGoalEditText).text.toString()
            val plantDate = findViewById<TextView>(R.id.plantDateTextView).text.toString()

            if (plantNickname.isNotEmpty() && plantGoal.isNotEmpty() && plantDate.isNotEmpty()) {
                // 식물 추가 로직
                Toast.makeText(this, "식물 추가 완료", Toast.LENGTH_SHORT).show()
                // 식물 리스트 화면으로 돌아가기
                finish()
            } else {
                Toast.makeText(this, "모든 항목을 입력하세요", Toast.LENGTH_SHORT).show()
            }
        }

        // 날짜 선택 TextView 클릭 이벤트
        findViewById<TextView>(R.id.plantDateTextView).setOnClickListener {
            showDatePickerDialog()
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            android.R.style.Theme_Material_Light_Dialog_Alert,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedYear-${selectedMonth + 1}-$selectedDay"
                findViewById<TextView>(R.id.textView).text = selectedDate
            },
            year, month, day
        )

        datePickerDialog.show()

    }
}
