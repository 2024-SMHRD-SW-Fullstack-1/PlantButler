package com.example.plantbutler

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MyPageAdapter(val context: Context, var postList: ArrayList<PostVOWithMemImg>)
    : RecyclerView.Adapter<MyPageAdapter.ViewHolder>() {

    lateinit var queue: RequestQueue

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
        queue = Volley.newRequestQueue(context)

        if(postList.get(position).img != null.toString()){
            val byteString = Base64.decode(postList.get(position).img, Base64.DEFAULT)
            val byteArray = BitmapFactory.decodeByteArray(byteString, 0, byteString.size)
            holder.ivMyRecord.setImageBitmap(byteArray)

        }else{
            holder.ivMyRecord.setImageResource(R.drawable.leaf)
        }
        // 아이템 뷰 클릭 시 해당 게시물로 이동
        holder.itemView.setOnClickListener{
            val post = postList[position]
            val fragment = PostDetail()
            val args = Bundle()
            var views = post.views


            args.putString("idx", post.idx.toString())
            args.putString("title", post.title)
            args.putString("id", post.id)
            args.putString("postImg", post.img)
            args.putString("memImg", post.memImg)
            args.putString("nick", post.nick)

            val inputDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val outputDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val parsedDate = inputDateFormat.parse(post.date) ?: Date()
            val formattedDate = outputDateFormat.format(parsedDate)

            args.putString("date", formattedDate)
            args.putString("content", post.content)

            val request = StringRequest(
                Request.Method.GET,
                "http://192.168.219.60:8089/plantbutler/increViews/${post.idx}",
                { response ->
                    Log.d("increViews", response.toString())
                    views = views?.plus(1)
                    args.putString("views", views.toString())

                    fragment.arguments = args

                    (context as MainActivity).replaceFragment(fragment)
                },
                { error ->
                    Log.d("error", error.toString())
                }
            )
            queue.add(request)
        }


    }
}