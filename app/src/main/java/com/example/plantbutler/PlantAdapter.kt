package com.example.plantbutler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class PlantAdapter(
    private val plantList: List<Plant>,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<PlantAdapter.PlantViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    private var selectedPosition = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_plant, parent, false)
        return PlantViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlantViewHolder, position: Int) {
        val plant = plantList[position]
        holder.plantName.text = plant.name

        // 기본 이미지 처리
        if (plant.id == 0 || plant.imageUrl.isNullOrEmpty()) {
            holder.plantImage.setImageResource(R.drawable.basic) // 기본 이미지 리소스
        } else {
            Picasso.get().load(plant.imageUrl).into(holder.plantImage)
        }

        // 선택된 항목의 배경색 변경
        if (selectedPosition == position) {
            holder.itemView.setBackgroundResource(R.color.selected_item) // 선택된 배경색 리소스
        } else {
            holder.itemView.setBackgroundResource(R.color.default_item) // 기본 배경색 리소스
        }

        holder.itemView.setOnClickListener {
            selectedPosition = position
            itemClickListener.onItemClick(position)
            notifyDataSetChanged() // 선택 상태 변경 알림
        }
    }

    override fun getItemCount() = plantList.size

    class PlantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val plantName: TextView = itemView.findViewById(R.id.plantName)
        val plantImage: ImageView = itemView.findViewById(R.id.plantImage)
    }
}
