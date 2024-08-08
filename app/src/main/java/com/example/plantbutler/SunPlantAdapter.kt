package com.example.plantbutler

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class SunPlantAdapter(val context: Context, val plantList: ArrayList<SunPlant>) : RecyclerView.Adapter<SunPlantAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivPlant: ImageView = view.findViewById(R.id.ivPlant)
        val tvName: TextView = view.findViewById(R.id.tvName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.plant_sunitem, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return plantList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val plant = plantList[position]
        holder.tvName.text = plant.commonName
        Glide.with(context).load(plant.imageUrl).into(holder.ivPlant)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, SunDetailActivity::class.java).apply {
                putExtra("plantID", plant.id) // 정수형 ID 전달
                putExtra("plantName", plant.commonName)
                putExtra("plantImgPath", plant.imageUrl)
            }
            context.startActivity(intent)
        }
    }
}