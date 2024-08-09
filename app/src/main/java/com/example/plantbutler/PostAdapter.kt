package com.example.plantbutler

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.green
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class PostAdapter(var context: Context, var postList: ArrayList<PostVOWithMemImg>)
    : RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    lateinit var queue: RequestQueue

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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        queue = Volley.newRequestQueue(context)

        val post = postList[position]
        holder.tvPostTitle.text = post.title
        holder.tvPostText.text = post.content

        if (post.img != null) {
            val byteString = Base64.decode(post.img, Base64.DEFAULT)
            val byteArray = BitmapFactory.decodeByteArray(byteString, 0, byteString.size)
            holder.ivPostImg.setImageBitmap(byteArray)
        } else {
            holder.ivPostImg.visibility = View.GONE
        }

        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = inputFormat.parse(post.date) ?: Date()
        val formatDate = outputFormat.format(date)

        holder.tvPostDate.text = formatDate
        holder.tvPostNick.text = post.nick

        holder.clPost.setOnClickListener {
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
                "http://192.168.219.41:8089/plantbutler/increViews/${post.idx}",
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