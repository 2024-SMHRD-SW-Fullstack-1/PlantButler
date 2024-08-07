package com.example.plantbutler

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray

class Search : Fragment() {

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
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        var searchQuery = arguments?.getString("query")
        Log.d("searchQuery", searchQuery.toString())

        rvPostList = view.findViewById(R.id.rvSearchResult)
        postList = ArrayList<PostVOWithMemImg>()
        queue = Volley.newRequestQueue(mContext)

        adapter = PostAdapter(mContext, postList)
        rvPostList.adapter = adapter
        rvPostList.layoutManager = LinearLayoutManager(mContext)

        val tvSearchResult: TextView = view.findViewById(R.id.tvSearchResult)

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

        // 검색 결과
        searchList(searchQuery, tvSearchResult)

        return view
    }

    private fun searchList(query: String?, tvSearchResult: TextView) {
        val request = StringRequest(
            Request.Method.GET,
            "http://192.168.219.60:8089/plantbutler/search/$query",
            { response ->
                Log.d("searchResponse", response.toString())

                if (response != "[]") {
                    tvSearchResult.setText("'${query}' 검색 결과")
                    val jsonArray = JSONArray(response)
                    for (i in 0 until jsonArray.length()) {
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

                        Log.d("img", result.getString("img"))

                        if (result.getString("img") != null) {
                            img = result.getString("img")
                        } else {
                            img = ""
                        }

                        if (result.getJSONObject("member").getString("img") != null) {
                            memImg = result.getJSONObject("member").getString("img")
                        } else {
                            memImg = ""
                        }

                        postList.add(
                            PostVOWithMemImg(idx, id, img, content, views, title, date, nick, memImg)
                        )

                        adapter.notifyDataSetChanged()
                    }
                }else {
                    tvSearchResult.setText("검색 결과가 없습니다.")
                }

                tvSearchResult.setText("")

            },
            { error ->
                Log.d("error", error.toString())
            }
        )
        queue.add(request)
    }
}