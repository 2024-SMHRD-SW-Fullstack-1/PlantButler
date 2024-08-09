package com.example.plantbutler

import android.content.Context
import android.graphics.BitmapFactory
import android.media.Image
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson

class CommentAdapter(var context: Context, var commentList: ArrayList<CommentVOWithMemImg>, var memId: String?)
    : RecyclerView.Adapter<CommentAdapter.ViewHolder>() {

    lateinit var queue: RequestQueue

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val tvCommentNick: TextView
        val tvCommentContent: TextView
        val ivCommentImg: ImageView
        val tvCommentDel: TextView

        init {
            tvCommentNick = itemView.findViewById(R.id.tvCommentNick)
            tvCommentContent = itemView.findViewById(R.id.tvCommentContent)
            ivCommentImg = itemView.findViewById(R.id.ivCommentImg)
            tvCommentDel = itemView.findViewById(R.id.tvCommentDel)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.comment_list, null)

        return  ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return commentList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        queue = Volley.newRequestQueue(context)

        holder.tvCommentNick.setText(commentList.get(position).nick)
        holder.tvCommentContent.setText(commentList.get(position).content)

        if(commentList.get(position).memImg != null) {
            val byteString = Base64.decode(commentList.get(position).memImg, Base64.DEFAULT)
            val byteArray = BitmapFactory.decodeByteArray(byteString, 0, byteString.size)
            holder.ivCommentImg.setImageBitmap(byteArray)
        }else {
            holder.ivCommentImg.setVisibility(View.GONE)
        }

        if (memId == commentList.get(position).id) {
            holder.tvCommentDel.visibility = View.VISIBLE
        }else {
            holder.tvCommentDel.visibility = View.GONE
        }

        holder.tvCommentDel.setOnClickListener {

            val delCommentIdx = commentList.get(position).commentIdx

            val request = object: StringRequest(
                Request.Method.POST,
                "http://192.168.219.60:8089/plantbutler/comment/delete",
                {response ->
                    Log.d("commentDelete", response)
                    // 데이터 목록에서 해당 항목 제거
                    commentList.removeAt(position)
                    // 어댑터에 데이터 변경 사항 알림
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, itemCount)
                },
                {error ->
                    Log.d("error", error.toString())
                }
            ){
                override fun getParams(): MutableMap<String, String> {
                    val params:MutableMap<String, String> = HashMap<String, String>()

                    params.put("delComment", Gson().toJson(delCommentIdx))
                    return params
                }
            }

            queue.add(request)
        }
    }
}