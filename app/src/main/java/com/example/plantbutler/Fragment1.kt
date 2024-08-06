package com.example.plantbutler

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class Fragment1 : Fragment() {

    lateinit var queue: RequestQueue
    lateinit var adapter: PostAdapter
    lateinit var postList: ArrayList<PostVO>
    lateinit var rvPostList: RecyclerView

    lateinit var mContext: Context
    lateinit var mActivity: Activity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        mActivity = context as Activity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_1, container, false)

        rvPostList = view.findViewById(R.id.rvPostList)
        postList = ArrayList<PostVO>()
        queue = Volley.newRequestQueue(mContext)

        adapter = PostAdapter(mContext, postList)
        rvPostList.adapter = adapter
        rvPostList.layoutManager = LinearLayoutManager(mContext)

        val request = StringRequest(
            Request.Method.POST,
            "http://192.168.219.60:8089/plantbutler/post",
            {response ->
                Log.d("response", response.toString())
                val jsonArray = JSONArray(response)

                for(i in 0 until jsonArray.length()) {
                    val result = jsonArray.getJSONObject(i)
                    val idx = result.getString("idx").toInt()
                    val nick = result.getJSONObject("member").getString("nick")
                    val content = result.getString("content")
                    val views = result.getString("views").toInt()
                    val title = result.getString("title")
                    val date = result.getString("date")
                    val img: String

                    if(result.getString("img") != null) {
                        img = result.getString("img")
                    }else {
                        img = ""
                    }
                    postList.add(PostVO(idx, nick, img, content, views, title, date))

                    adapter.notifyDataSetChanged()
                }
            },
            {error ->
                Log.d("error", error.toString())
            }
        )
        queue.add(request)

        val btnPostAdd: Button = view.findViewById(R.id.btnPostAdd)

        btnPostAdd.setOnClickListener {
            val fragment = AddPost()
            (context as MainActivity).replaceFragment(fragment)
        }

        return view
    }
}