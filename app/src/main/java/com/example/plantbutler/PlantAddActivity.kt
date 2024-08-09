package com.example.plantbutler

import android.app.Activity
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PlantAddActivity : AppCompatActivity() {

    private var memberId: String? = null
    private lateinit var etPlantName: EditText
    private lateinit var etGoal: EditText
    private lateinit var etDate: EditText
    private lateinit var rvPlants: RecyclerView
    private lateinit var plantAdapter: PlantAdapter
    private val plantList = mutableListOf<Plant>()
    private var selectedPlantIdx: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plant_add)

        memberId = intent.getStringExtra("memberId")

        val btnClose = findViewById<ImageView>(R.id.btnClose)
        val btnCheck = findViewById<ImageView>(R.id.btnCheck)

        etPlantName = findViewById(R.id.etPlantName)
        etGoal = findViewById(R.id.etGoal)
        etDate = findViewById(R.id.etDate)
        rvPlants = findViewById(R.id.rvPlants)
        rvPlants.layoutManager = LinearLayoutManager(this)

        plantAdapter = PlantAdapter(plantList, object : PlantAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                selectedPlantIdx = plantList[position].id
                plantAdapter.notifyDataSetChanged()
            }
        })
        rvPlants.adapter = plantAdapter

        btnClose.setOnClickListener {
            finish()
        }

        btnCheck.setOnClickListener {
            val plantNickname = etPlantName.text.toString()
            val plantGoal = etGoal.text.toString()
            val plantDate = etDate.text.toString()

            if (plantNickname.isNotEmpty() && plantGoal.isNotEmpty() && plantDate.isNotEmpty() && selectedPlantIdx != -1) {
                addPlantToServer(selectedPlantIdx, plantNickname, plantGoal, plantDate)
            } else {
                Toast.makeText(this, "모든 항목을 입력하세요", Toast.LENGTH_SHORT).show()
            }
        }

        etDate.setOnClickListener {
            showDatePickerDialog()
        }

        loadPlantsFromServer()
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val calendar = Calendar.getInstance()
                calendar.set(selectedYear, selectedMonth, selectedDay)
                val date = calendar.time
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                etDate.setText(dateFormat.format(date))
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    private fun loadPlantsFromServer() {
        val url = "http://192.168.219.60:8089/plantbutler/api/plant_catalog"
        val queue = Volley.newRequestQueue(this)

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                parsePlantCatalog(response)
            },
            { error ->
                val errorMessage = error.networkResponse?.data?.let { String(it) } ?: error.message
                Toast.makeText(this, "Error: $errorMessage", Toast.LENGTH_SHORT).show()
            }
        )

        queue.add(jsonArrayRequest)
    }

    private fun parsePlantCatalog(response: JSONArray) {
        plantList.clear()

        for (i in 0 until response.length()) {
            val plantJson = response.getJSONObject(i)
            val plant = Plant(
                plantJson.optInt("id", -1),
                plantJson.optString("name", ""),
                plantJson.optString("imageUrl", ""),
                plantJson.optString("content", "")
            )
            plantList.add(plant)
        }
        plantAdapter.notifyDataSetChanged()
    }

    private fun addPlantToServer(plantIdx: Int, nickname: String, goal: String, startDate: String) {
        val queue = Volley.newRequestQueue(this)
        val url = "http://192.168.219.41:8089/plantbutler/api/plants"

        val selectedPlant = plantList.find { it.id == plantIdx }

        val myPlant = MyPlant(
            myplantIdx = -1,
            memberId = memberId,
            plantIdx = plantIdx,
            nickname = nickname,
            goal = goal,
            startDate = startDate,
            imageUrl = selectedPlant?.imageUrl // 선택한 식물의 이미지 URL을 설정
        )
        val gson = Gson()
        val requestBody = gson.toJson(myPlant)

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            { response ->
                Log.d("PlantAddActivity", "Response: $response")
                Toast.makeText(this, "식물 추가 완료", Toast.LENGTH_SHORT).show()
                setResult(Activity.RESULT_OK)
                finish()
            },
            { error ->
                Log.e("PlantAddActivity", "Error: ${error.message}")
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
