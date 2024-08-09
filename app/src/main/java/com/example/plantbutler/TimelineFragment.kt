package com.example.plantbutler

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TimelineFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var timelineAdapter: TimelineAdapter
    private val notes = mutableListOf<Note>()
    private var myplantIdx: Int = -1

    private lateinit var tvNoNotes: TextView
    private lateinit var btnAddFirstNote: Button
    private lateinit var fabAddNote: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_timeline, container, false)

        // 전달된 myplantIdx 값을 받기
        myplantIdx = arguments?.getInt("myplant_idx", -1) ?: -1

        // 뷰 초기화
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        timelineAdapter = TimelineAdapter(notes, object : TimelineAdapter.OnDeleteClickListener {
            override fun onDeleteClick(note: Note) {
                deleteNoteFromServer(note)
            }
        })
        recyclerView.adapter = timelineAdapter

        tvNoNotes = view.findViewById(R.id.tvNoNotes)
        btnAddFirstNote = view.findViewById(R.id.btnAddFirstNote)
        fabAddNote = view.findViewById(R.id.fabAddNote)

        // 노트 추가 버튼 클릭 시 동작
        fabAddNote.setOnClickListener {
            val intent = Intent(requireContext(), AddNoteActivity::class.java).apply {
                putExtra("myplant_idx", myplantIdx)
            }
            startActivityForResult(intent, 1)
        }

        btnAddFirstNote.setOnClickListener {
            val intent = Intent(requireContext(), AddNoteActivity::class.java).apply {
                putExtra("myplant_idx", myplantIdx)
            }
            startActivityForResult(intent, 1)
        }

        // 서버에서 노트 목록을 로드
        loadNotesFromServer()

        return view
    }


    private fun loadNotesFromServer() {
        if (myplantIdx != -1) {
            val url = "http://192.168.219.41:8089/plantbutler/api/notes/$myplantIdx"
            val queue = Volley.newRequestQueue(requireContext())

            // 로그 추가
            Log.d("TimelineFragment", "Requesting notes for myplantIdx: $myplantIdx")

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
                    Toast.makeText(requireContext(), "Error: $errorMessage", Toast.LENGTH_SHORT).show()
                }
            )

            queue.add(stringRequest)
        } else {
            Log.e("TimelineFragment", "Invalid myplantIdx: $myplantIdx")
        }
    }

    private fun deleteNoteFromServer(note: Note) {
        val url = "http://192.168.219.41:8089/plantbutler/api/notes/${note.id}"
        val queue = Volley.newRequestQueue(requireContext())

        val stringRequest = StringRequest(
            Request.Method.DELETE, url,
            { response ->
                notes.remove(note)
                timelineAdapter.notifyDataSetChanged()
                Toast.makeText(requireContext(), "노트가 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                // 삭제 후 상태 확인
                toggleEmptyState()
            },
            { error ->
                val errorMessage = error.networkResponse?.data?.let { String(it) } ?: error.message
                Toast.makeText(requireContext(), "Error: $errorMessage", Toast.LENGTH_SHORT).show()
            }
        )

        queue.add(stringRequest)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            loadNotesFromServer()
        }
    }
}
