package com.example.plantbutler

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import org.json.JSONObject

class SunDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sundetail)

        val ivPlant = findViewById<ImageView>(R.id.ivDetailPlant)
        val tvName = findViewById<TextView>(R.id.tvDetailName)
        val tvScientific = findViewById<TextView>(R.id.tvScientific)
        val tvFamily = findViewById<TextView>(R.id.tvFamily)
        val tvGenus = findViewById<TextView>(R.id.tvGenus)
        val tvSynonyms = findViewById<TextView>(R.id.tvSynonyms)

        val plantId = intent.getIntExtra("plantID", -1)
        val plantName = intent.getStringExtra("plantName")
        val plantImgPath = intent.getStringExtra("plantImgPath")

        tvName.text = plantName
        Glide.with(this).load(plantImgPath).into(ivPlant)

        if (plantId != -1) {
            loadPlantDetails(plantId.toString(), tvScientific, tvFamily, tvGenus, tvSynonyms)
        } else {
            Log.e("SunDetailActivity", "Invalid plant ID")
        }
    }

    private fun loadPlantDetails(plantId: String, tvScientific: TextView, tvFamily: TextView, tvGenus: TextView, tvSynonyms: TextView) {
        val url = "https://trefle.io/api/v1/species/$plantId?token=sC_m01lEXMmE0BMjxDRN-nzctY4N3nSnxZSLlvTQP4Y"
        Log.d("URL", url)

        val request = StringRequest(
            Request.Method.GET,
            url,
            { response ->
                Log.d("response", response.toString())
                val detail = JSONObject(response).getJSONObject("data")

                val scientificName = detail.optString("scientific_name", "N/A")
                val family = detail.optString("family", "N/A")
                val genus = detail.optString("genus", "N/A")
                val synonymsArray = detail.optJSONArray("synonyms")

                val synonyms = if (synonymsArray != null && synonymsArray.length() > 0) {
                    // Extract the first synonym's "name" field
                    synonymsArray.getJSONObject(0).optString("name", "N/A")
                } else {
                    "N/A"
                }

                Log.d("PlantDetails", "Scientific Name: $scientificName, Family: $family, Genus: $genus, Synonyms: $synonyms")
                Log.d("Synonym", synonyms)

                tvScientific.text = scientificName
                tvFamily.text = family
                tvGenus.text = genus
                tvSynonyms.text = synonyms
            },
            { error ->
                Log.d("error", error.toString())
            }
        )

        Volley.newRequestQueue(this).add(request)
    }
}
