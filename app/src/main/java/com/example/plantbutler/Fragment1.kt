package com.example.plantbutler

import ItemOffsetDecoration
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray

class Fragment1 : Fragment() {

    lateinit var queue: RequestQueue
    lateinit var adapter: PostAdapter
    lateinit var postList: ArrayList<PostVOWithMemImg>
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
        postList = ArrayList<PostVOWithMemImg>()
        queue = Volley.newRequestQueue(mContext)

        adapter = PostAdapter(mContext, postList)
        val itemDecoration = ItemOffsetDecoration(16) // 마진 값 설정
        rvPostList.addItemDecoration(itemDecoration)
        rvPostList.adapter = adapter
        rvPostList.layoutManager = LinearLayoutManager(mContext)

        // 검색 기능
        val searchView: SearchView = view.findViewById(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // 검색할 때
                query?.let {
                    Log.d("query", query)

                    val fragment = Search()
                    val args = Bundle()
                    args.putString("query", query)

                    fragment.arguments = args
                    (context as MainActivity).replaceFragment(fragment)

                }
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                return true
            }
        })

        val request = StringRequest(
            Request.Method.POST,
            "http://192.168.219.60:8089/plantbutler/post",
            {response ->
                Log.d("postLists", response.toString())
                val jsonArray = JSONArray(response)

                for(i in 0 until jsonArray.length()) {
                    val result = jsonArray.getJSONObject(i)
                    val idx = result.getString("idx").toInt()
                    val id = result.getJSONObject("member").getString("id")
                    val nick = result.getJSONObject("member").getString("nick")
                    val content = result.getString("content")
                    val views = result.getString("views").toInt()
                    val title = result.getString("title")
                    val date = result.getString("date")
                    val img: String
                    val memImg: String

                    if(result.getString("img") != null) {
                        img = result.getString("img")
                    }else {
                        img = ""
                    }

                    Log.d("postsImg", img.toString())

                    if(result.getJSONObject("member").getString("img") != null) {
                        memImg = result.getJSONObject("member").getString("img")
                    }else {
                        memImg = ""
                    }

                    postList.add(PostVOWithMemImg(idx, id, img, content, views, title, date, nick ,memImg))

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