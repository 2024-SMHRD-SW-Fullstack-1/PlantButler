package com.example.plantbutler

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.plantbutler.SunMainActivity
import org.json.JSONArray

class Fragment2 : Fragment(), MyPlantAdapter.OnItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var myPlantAdapter: MyPlantAdapter
    private val myPlantList = mutableListOf<MyPlant>()
    private var memberId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_2, container, false)

        // SharedPreferences에서 로그인한 사용자의 ID를 가져옴
        val sharedPreferences = activity?.getSharedPreferences("member", Context.MODE_PRIVATE)
        memberId = sharedPreferences?.getString("memId", "id")

        // RecyclerView와 어댑터 초기화
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(activity, 2)

        myPlantAdapter = MyPlantAdapter(myPlantList, this)
        recyclerView.adapter = myPlantAdapter

        // 식물 추가 버튼 클릭 시 PlantAddActivity로 이동
        val btnAddPlant: Button = view.findViewById(R.id.btnAddPlant)
        btnAddPlant.setOnClickListener {
            val intent = Intent(activity, PlantAddActivity::class.java).apply {
                putExtra("memberId", memberId) // PlantAddActivity에 로그인한 사용자의 ID를 전달
            }
            startActivityForResult(intent, 1)
        }

        val btnDiction: Button = view.findViewById(R.id.btnDiction)
        btnDiction.setOnClickListener {
            val intent = Intent(activity, SunMainActivity::class.java).apply{}
            startActivityForResult(intent, 1)
        }

        // 서버에서 식물 목록을 로드
        loadPlantsFromServer()

        return view
    }

    private fun loadPlantsFromServer() {
        memberId?.let { id ->
            // 사용자의 ID를 포함하여 요청 URL을 생성
            val url = "http://192.168.219.60:8089/plantbutler/api/plants/$id"
            val requestQueue = Volley.newRequestQueue(activity)

            val jsonArrayRequest = JsonArrayRequest(
                Request.Method.GET, url, null,
                { response -> parseMyPlantList(response) },
                { error ->
                    Log.e("Fragment2", "Error: ${error.message}")
                    error.printStackTrace() // 에러 상세 정보를 로그에 출력
                }
            )

            requestQueue.add(jsonArrayRequest)
        }
    }

    private fun parseMyPlantList(response: JSONArray) {
        myPlantList.clear()
        for (i in 0 until response.length()) {
            val plantJson = response.getJSONObject(i)
            val myPlant = MyPlant(
                myplantIdx = plantJson.optInt("myplantIdx", -1),
                memberId = plantJson.optString("memberId", ""),
                plantIdx = plantJson.optInt("plantIdx", -1),
                nickname = plantJson.optString("nickname", ""),
                goal = plantJson.optString("goal", ""),
                startDate = plantJson.optString("startDate", ""),
                imageUrl = "" // imageUrl을 추가로 가져오지 않았으므로 빈 문자열로 설정
            )
            myPlantList.add(myPlant)
            Log.d("Fragment2", "Parsed plant: ${myPlant.myplantIdx}, ${myPlant.nickname}, ${myPlant.plantIdx}")
        }
        myPlantAdapter.notifyDataSetChanged()
    }

    override fun onItemClick(position: Int) {
        val selectedMyPlant = myPlantList[position]

        // PlantDetailActivity로 이동하는 코드
        val intent = Intent(activity, PlantDetailActivity::class.java).apply {
            putExtra("plantId", selectedMyPlant.plantIdx) // plantIdx 추가
            putExtra("nickname", selectedMyPlant.nickname)
            putExtra("goal", selectedMyPlant.goal)
            putExtra("startDate", selectedMyPlant.startDate)
            putExtra("myplant_idx", selectedMyPlant.myplantIdx) // myplantIdx 추가
        }
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            loadPlantsFromServer() // 새로고침을 통해 식물 목록 업데이트
        }
    }
}
