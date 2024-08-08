package com.example.plantbutler

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import com.google.android.material.tabs.TabLayout

class PlantDetailActivity : AppCompatActivity() {

    private lateinit var scrollView: NestedScrollView
    private lateinit var tabLayout: TabLayout

    private var plantId: String? = null
    private lateinit var plantNickname: String
    private lateinit var plantGoal: String
    private lateinit var plantStartDate: String
    private var myplantIdx: Int = -1 // 기본값 설정

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plant_detail)

        // 인텐트로 전달된 데이터 가져오기
        plantId = intent.getStringExtra("plantId")
        plantNickname = intent.getStringExtra("nickname").orEmpty()
        plantGoal = intent.getStringExtra("goal").orEmpty()
        plantStartDate = intent.getStringExtra("startDate").orEmpty()
        myplantIdx = intent.getIntExtra("myplant_idx", -1) // myplant_idx 값을 읽어옴

        Log.d("PlantDetailActivity", "Received plantId: $plantId, nickname: $plantNickname, myplantIdx: $myplantIdx")

        scrollView = findViewById(R.id.scrollView)
        tabLayout = findViewById(R.id.tabLayout)

        tabLayout.addTab(tabLayout.newTab().setText("기록모음"))
        tabLayout.addTab(tabLayout.newTab().setText("타임라인"))

        // 탭이 선택될 때 호출되는 리스너 설정
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> showPlantDetails()
                    1 -> showTimeline(myplantIdx) // myplantIdx 값을 전달
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        // 초기 화면을 기록모음으로 설정
        showPlantDetails()

        // 인텐트로 전달된 데이터를 UI에 반영
        val plantNameTextView: TextView = findViewById(R.id.plantNickname)
        val plantGoalTextView: TextView = findViewById(R.id.tvGoal)
        val plantStartDateTextView: TextView = findViewById(R.id.tvDate)
        val plantImageView: ImageView = findViewById(R.id.plantImage)

        plantNameTextView.text = plantNickname
        plantGoalTextView.text = plantGoal
        plantStartDateTextView.text = plantStartDate
        // plantImageView에 이미지를 설정할 수 있다면 여기서 설정
    }

    private fun showPlantDetails() {
        scrollView.visibility = View.VISIBLE
        // 타임라인에 해당하는 Fragment나 View는 숨기기
    }

    private fun showTimeline(myplantIdx: Int) {
        scrollView.visibility = View.GONE
        // TimelineActivity로 전환
        val intent = Intent(this, TimelineActivity::class.java).apply {
            putExtra("plantId", plantId)
            putExtra("nickname", plantNickname)
            putExtra("goal", plantGoal)
            putExtra("startDate", plantStartDate)
            putExtra("myplant_idx", myplantIdx)
        }
        startActivity(intent)
    }
}
