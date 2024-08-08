package com.example.plantbutler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class MyPlantAdapter(
    private val myPlantList: List<MyPlant>,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<MyPlantAdapter.MyPlantViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPlantViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_myplant, parent, false)
        return MyPlantViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyPlantViewHolder, position: Int) {
        val myPlant = myPlantList[position]
        holder.plantName.text = myPlant.nickname

        if (!myPlant.imageUrl.isNullOrEmpty()) {
            Picasso.get().load(myPlant.imageUrl).into(holder.plantImage)
        } else {
            holder.plantImage.setImageResource(R.drawable.basic) // 기본 이미지 설정
        }

        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(position)
        }
    }

    override fun getItemCount() = myPlantList.size

    class MyPlantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val plantName: TextView = itemView.findViewById(R.id.plantName)
        val plantImage: ImageView = itemView.findViewById(R.id.plantImage)
    }
}
