package com.example.plantbutler

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyPageAdapter(val context: Context, var postList: ArrayList<PostVO>)
    : RecyclerView.Adapter<MyPageAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val ivMyRecord: ImageView

        init{
            ivMyRecord = itemView.findViewById(R.id.ivMyRecord)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPageAdapter.ViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.mypage_item,null)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    override fun onBindViewHolder(holder: MyPageAdapter.ViewHolder, position: Int) {
        if(postList.get(position).img != null.toString()){
            val byteString = Base64.decode(postList.get(position).img, Base64.DEFAULT)
            val byteArray = BitmapFactory.decodeByteArray(byteString, 0, byteString.size)
            holder.ivMyRecord.setImageBitmap(byteArray)

        }else{
            holder.ivMyRecord.setImageResource(R.drawable.leaf)
        }
        // 아이템 뷰 클릭 시 해당 게시물로 이동
        holder.itemView.setOnClickListener{

        }


    }
}