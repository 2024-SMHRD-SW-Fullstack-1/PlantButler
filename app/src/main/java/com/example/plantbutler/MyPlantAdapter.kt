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

    val plantImgList: ArrayList<String> = arrayListOf(
        "https://images.pexels.com/photos/1003914/pexels-photo-1003914.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",
        "https://images.pexels.com/photos/350349/pexels-photo-350349.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",
        "https://images.pexels.com/photos/122611/pexels-photo-122611.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",
        "https://images.pexels.com/photos/1454288/pexels-photo-1454288.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",
        "https://images.pexels.com/photos/62403/pexels-photo-62403.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",
        "https://images.pexels.com/photos/207518/pexels-photo-207518.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",
        "https://images.pexels.com/photos/1179863/pexels-photo-1179863.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",
        "https://images.pexels.com/photos/4936309/pexels-photo-4936309.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",
        "https://images.pexels.com/photos/4803815/pexels-photo-4803815.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",
        "https://images.pexels.com/photos/1252874/pexels-photo-1252874.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"
    )

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

        if (myPlant.plantIdx != 0) {
            Picasso.get().load(plantImgList.get(myPlant.plantIdx + 1)).into(holder.plantImage)
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
