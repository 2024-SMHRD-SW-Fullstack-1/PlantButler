package com.example.plantbutler

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TimelineActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var timelineAdapter: TimelineAdapter
    private val notes = mutableListOf<Note>()
    private var myplant_idx: Int = -1

    private lateinit var tvNoNotes: TextView
    private lateinit var btnAddFirstNote: Button
    private lateinit var fabAddNote: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timeline)

        // 인텐트로 전달된 myplant_idx 값을 가져옴
        myplant_idx = intent.getIntExtra("myplant_idx", -1)

        // RecyclerView와 어댑터 초기화
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        timelineAdapter = TimelineAdapter(notes, object : TimelineAdapter.OnDeleteClickListener {
            override fun onDeleteClick(note: Note) {
                deleteNoteFromServer(note)
            }
        })
        recyclerView.adapter = timelineAdapter

        // 초기화
        tvNoNotes = findViewById(R.id.tvNoNotes)
        btnAddFirstNote = findViewById(R.id.btnAddFirstNote)
        fabAddNote = findViewById(R.id.fabAddNote)

        // 노트 추가 버튼 클릭 시 AddNoteActivity로 이동
        fabAddNote.setOnClickListener {
            val intent = Intent(this, AddNoteActivity::class.java).apply {
                putExtra("myplant_idx", myplant_idx)
            }
            startActivityForResult(intent, 1)
        }

        btnAddFirstNote.setOnClickListener {
            val intent = Intent(this, AddNoteActivity::class.java).apply {
                putExtra("myplant_idx", myplant_idx)
            }
            startActivityForResult(intent, 1)
        }

        // 서버에서 노트 목록을 로드
        loadNotesFromServer()
    }

    private fun loadNotesFromServer() {
        if (myplant_idx != -1) {
            val url = "http://192.168.219.41:8089/plantbutler/api/notes/$myplant_idx"
            val queue = Volley.newRequestQueue(this)

            val stringRequest = StringRequest(
                Request.Method.GET, url,
                { response ->
                    val gson = Gson()
                    val type = object : TypeToken<List<Note>>() {}.type
                    val noteList: List<Note> = gson.fromJson(response, type)

                    // 날짜를 기준으로 내림차순 정렬
                    val sortedNotes = noteList.sortedByDescending { it.date }

                    notes.clear()
                    notes.addAll(sortedNotes)
                    timelineAdapter.notifyDataSetChanged()

                    // 노트가 비어 있는지 확인
                    toggleEmptyState()
                },
                { error ->
                    val errorMessage = error.networkResponse?.data?.let { String(it) } ?: error.message
                    Toast.makeText(this, "Error: $errorMessage", Toast.LENGTH_SHORT).show()
                }
            )

            queue.add(stringRequest)
        }
    }

    private fun toggleEmptyState() {
        if (notes.isEmpty()) {
            tvNoNotes.visibility = View.VISIBLE
            btnAddFirstNote.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            fabAddNote.visibility = View.GONE
        } else {
            tvNoNotes.visibility = View.GONE
            btnAddFirstNote.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            fabAddNote.visibility = View.VISIBLE
        }
    }

    private fun deleteNoteFromServer(note: Note) {
        val url = "http://192.168.219.41:8089/plantbutler/api/notes/${note.id}"
        val queue = Volley.newRequestQueue(this)

        val stringRequest = StringRequest(
            Request.Method.DELETE, url,
            { response ->
                notes.remove(note)
                timelineAdapter.notifyDataSetChanged()
                Toast.makeText(this, "노트가 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                // 삭제 후 상태 확인
                toggleEmptyState()
            },
            { error ->
                val errorMessage = error.networkResponse?.data?.let { String(it) } ?: error.message
                Toast.makeText(this, "Error: $errorMessage", Toast.LENGTH_SHORT).show()
            }
        )

        queue.add(stringRequest)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            loadNotesFromServer()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, PlantDetailActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra("plantId", intent.getStringExtra("plantId"))
            putExtra("nickname", intent.getStringExtra("nickname"))
            putExtra("goal", intent.getStringExtra("goal"))
            putExtra("startDate", intent.getStringExtra("startDate"))
            putExtra("myplant_idx", myplant_idx)
        }
        startActivity(intent)
        finish()
    }
}
