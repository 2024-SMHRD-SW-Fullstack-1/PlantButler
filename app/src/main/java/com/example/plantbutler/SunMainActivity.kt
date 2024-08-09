package com.example.plantbutler;

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.plantbutler.R
import com.example.plantbutler.SunPlant
import com.example.plantbutler.SunPlantAdapter
import org.json.JSONObject

class SunMainActivity : AppCompatActivity() {

    private lateinit var queue: RequestQueue
    private lateinit var adapter: SunPlantAdapter
    private lateinit var plantList: ArrayList<SunPlant>
    private lateinit var rvPlants: RecyclerView
    private lateinit var tvLoading: TextView
    private var currentPage = 1
    private val maxPage = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sunmain)

        rvPlants = findViewById(R.id.rvPlants)
        tvLoading = findViewById<TextView>(R.id.tvLoading)
        plantList = ArrayList()
        queue = Volley.newRequestQueue(this@SunMainActivity)

        adapter = SunPlantAdapter(this, plantList)
        rvPlants.adapter = adapter
        rvPlants.layoutManager = GridLayoutManager(this, 2)

        Handler(Looper.getMainLooper()).postDelayed({
            tvLoading.visibility = View.GONE
        }, 2000)

        loadPlants(currentPage)



        rvPlants.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(1) && currentPage < maxPage) {
                    currentPage++
                    loadPlants(currentPage)
                }
            }
        })
    }

    private fun loadPlants(page: Int) {
        val url = "https://trefle.io/api/v1/plants?token=sC_m01lEXMmE0BMjxDRN-nzctY4N3nSnxZSLlvTQP4Y&page=$page"
        val request = StringRequest(
            Request.Method.GET,
            url,
            { response ->
                Log.d("response", response)
                val jsonObject = JSONObject(response)
                val dataArray = jsonObject.getJSONArray("data")

                for (i in 0 until dataArray.length()) {
                    val plantData = dataArray.getJSONObject(i)
                    val id = plantData.getInt("id")   // 정수형 ID 가져오기
                    val scientificName = plantData.getString("scientific_name")
                    val commonName = plantData.optString("common_name", null) ?: scientificName
                    val imageUrl = plantData.optString("image_url", "")
                    plantList.add(SunPlant(id, commonName, scientificName, imageUrl))
                }

                adapter.notifyDataSetChanged()
            },
            { error ->
                Log.d("error", error.toString())
            }
        )

        queue.add(request)
    }
}