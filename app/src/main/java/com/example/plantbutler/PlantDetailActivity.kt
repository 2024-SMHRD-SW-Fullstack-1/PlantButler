package com.example.plantbutler

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout

class PlantDetailActivity : AppCompatActivity() {

    private lateinit var scrollView: NestedScrollView
    private lateinit var tabLayout: TabLayout

    private var plantId: String? = null
    private lateinit var plantNickname: String
    private lateinit var plantGoal: String
    private lateinit var plantStartDate: String
    private var myplantIdx: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plant_detail)

        // Intent에서 데이터 가져오기
        plantId = intent.getStringExtra("plantId")
        plantNickname = intent.getStringExtra("nickname").orEmpty()
        plantGoal = intent.getStringExtra("goal").orEmpty()
        plantStartDate = intent.getStringExtra("startDate").orEmpty()
        myplantIdx = intent.getIntExtra("myplant_idx", -1)

        // View 초기화
        scrollView = findViewById(R.id.scrollView)
        tabLayout = findViewById(R.id.tabLayout)

        // TextView에 데이터 바인딩
        val nicknameTextView = findViewById<TextView>(R.id.plantNickname)
        val goalTextView = findViewById<TextView>(R.id.tvGoal)
        val startDateTextView = findViewById<TextView>(R.id.tvDate)

        nicknameTextView.text = plantNickname
        goalTextView.text = plantGoal
        startDateTextView.text = plantStartDate

        // Tab 초기화 및 리스너 설정
        tabLayout.addTab(tabLayout.newTab().setText("기록모음"))
        tabLayout.addTab(tabLayout.newTab().setText("타임라인"))

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> showPlantDetails()
                    1 -> showTimeline()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        showPlantDetails()
    }

    private fun showPlantDetails() {
        scrollView.visibility = View.VISIBLE
        findViewById<View>(R.id.fragmentContainer).visibility = View.GONE
        replaceFragment(null)
    }

    private fun showTimeline() {
        scrollView.visibility = View.GONE
        findViewById<View>(R.id.fragmentContainer).visibility = View.VISIBLE
        val fragment = TimelineFragment()
        val bundle = Bundle().apply {
            putInt("myplant_idx", myplantIdx)
        }
        fragment.arguments = bundle
        replaceFragment(fragment)
    }

    private fun replaceFragment(fragment: Fragment?) {
        if (fragment != null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit()
        } else {
            Log.d("PlantDetailActivity", "Fragment is null, nothing to replace")
        }
    }
}
