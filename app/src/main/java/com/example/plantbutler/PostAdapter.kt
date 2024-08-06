package com.example.plantbutler

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.green
import androidx.recyclerview.widget.RecyclerView
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class PostAdapter(var context: Context, var postList: ArrayList<PostVO>)
    : RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var tvPostTitle: TextView
        var tvPostText: TextView
        var tvPostNick: TextView
        var ivPostImg: ImageView
        var tvPostDate: TextView
        var clPost: ConstraintLayout

        init {
            tvPostTitle = itemView.findViewById(R.id.tvPostTitle)
            tvPostText = itemView.findViewById(R.id.tvPostText)
            tvPostNick = itemView.findViewById(R.id.tvPostNick)
            ivPostImg = itemView.findViewById(R.id.ivPostImg)
            tvPostDate = itemView.findViewById(R.id.tvPostDate)
            clPost = itemView.findViewById(R.id.clPost)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostAdapter.ViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.post_list, null)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    override fun onBindViewHolder(holder: PostAdapter.ViewHolder, position: Int) {
        holder.tvPostTitle.setText(postList.get(position).title)
        holder.tvPostText.setText(postList.get(position).content)

        if(postList.get(position).img != null) {
            val byteString = Base64.decode(postList.get(position).img, Base64.DEFAULT)
            val byteArray = BitmapFactory.decodeByteArray(byteString, 0, byteString.size)
            holder.ivPostImg.setImageBitmap(byteArray)
        }else {
            holder.ivPostImg.setVisibility(View.GONE)
        }

        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val outputFormat = SimpleDateFormat("yyyy-MM-dd")

        val date = inputFormat.parse(postList.get(position).date) ?: Date()
        val formatDate = outputFormat.format(date)

        holder.tvPostDate.setText(formatDate)
        holder.tvPostNick.setText(postList.get(position).id)

        holder.clPost.setOnClickListener{
            val fragment = PostDetail()
            val args = Bundle()
            postList.get(position).idx?.let { idx -> args.putInt("idx", idx) }
            args.putString("title", postList.get(position).title)
            postList.get(position).views?.let { views -> args.putInt("views", views) }
            args.putString("id", postList.get(position).id)
            args.putString("img", postList.get(position).img)

            // 날짜 변환
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC") // 입력 시간대 설정

            val outputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            outputFormat.timeZone = TimeZone.getDefault() // 출력 시간대 설정 (기본 시간대)

            val date = inputFormat.parse(postList[position].date) ?: Date()
            val formatDate = outputFormat.format(date)

            args.putString("date", formatDate)
            args.putString("content", postList.get(position).content)

            fragment.arguments = args

            (context as MainActivity).replaceFragment(fragment)
        }
    }

}