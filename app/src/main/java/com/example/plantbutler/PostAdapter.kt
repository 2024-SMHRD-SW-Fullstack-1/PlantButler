package com.example.plantbutler

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.green
import androidx.recyclerview.widget.RecyclerView
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.Date

class PostAdapter(var context: Context, var postList: ArrayList<PostVO>)
    : RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var tvPostTitle: TextView
        var tvPostText: TextView
        var tvPostNick: TextView
        var ivPostImg: ImageView
        var tvPostDate: TextView

        init {
            tvPostTitle = itemView.findViewById(R.id.tvPostTitle)
            tvPostText = itemView.findViewById(R.id.tvPostText)
            tvPostNick = itemView.findViewById(R.id.tvPostNick)
            ivPostImg = itemView.findViewById(R.id.ivPostImg)
            tvPostDate = itemView.findViewById(R.id.tvPostDate)
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

        val byteString = Base64.decode(postList.get(position).img, Base64.DEFAULT)
        val byteArray = BitmapFactory.decodeByteArray(byteString, 0, byteString.size)
        holder.ivPostImg.setImageBitmap(byteArray)

        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val outputFormat = SimpleDateFormat("yyyy-MM-dd")

        val date = inputFormat.parse(postList.get(position).date) ?: Date()
        val formatDate = outputFormat.format(date)

        holder.tvPostDate.setText(formatDate)
        holder.tvPostNick.setText(postList.get(position).id)
    }

}