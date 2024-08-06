package com.example.plantbutler

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView

class CommentAdapter(var context: Context, var commentList: ArrayList<CommentVO>)
    : RecyclerView.Adapter<CommentAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val tvCommentNick: TextView
        val tvCommentContent: TextView

        init {
            tvCommentNick = itemView.findViewById(R.id.tvCommentNick)
            tvCommentContent = itemView.findViewById(R.id.tvCommentContent)
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
        holder.tvCommentNick.setText(commentList.get(position).id)
        holder.tvCommentContent.setText(commentList.get(position).content)
    }
}