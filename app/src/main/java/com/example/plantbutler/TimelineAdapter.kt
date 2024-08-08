package com.example.plantbutler

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TimelineAdapter(private val notes: List<Note>, private val onDeleteClickListener: OnDeleteClickListener) :
    RecyclerView.Adapter<TimelineAdapter.TimelineViewHolder>() {

    interface OnDeleteClickListener {
        fun onDeleteClick(note: Note)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimelineViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        return TimelineViewHolder(view)
    }

    override fun onBindViewHolder(holder: TimelineViewHolder, position: Int) {
        val note = notes[position]
        holder.noteDate.text = note.date.toString().substring(0, 10) // 시간 제거
        holder.noteContent.text = note.content
        holder.btnDeleteNote.setOnClickListener {
            onDeleteClickListener.onDeleteClick(note)
        }
    }

    override fun getItemCount() = notes.size

    class TimelineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val noteDate: TextView = itemView.findViewById(R.id.noteDate)
        val noteContent: TextView = itemView.findViewById(R.id.noteContent)
        val btnDeleteNote: Button = itemView.findViewById(R.id.btnDeleteNote)
    }
}
