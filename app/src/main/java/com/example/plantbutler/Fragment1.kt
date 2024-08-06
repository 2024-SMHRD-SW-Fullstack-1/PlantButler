package com.example.plantbutler

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import java.lang.reflect.Type

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
            "http://192.168.219.60:8089/plantbutler/postlist",
            {response ->
                Log.d("response", response.toString())
                val jsonArray = JSONArray(response)
                val postListType: Type = object : TypeToken<List<PostVO>>() {}.type
                val result: ArrayList<PostVO> = Gson().fromJson(jsonArray.toString(), postListType)
                for(i in 0 until result.size) {
                    if(result[i].img != null) {
                        postList.add(result[i])
                    }else {
                        postList.add(PostVO(result[i].idx, result[i].id, "",
                            result[i].content, result[i].views, result[i].title, result[i].date))
                    }

                    adapter.notifyDataSetChanged()
                }
                Log.d("result", result.toString())
            },
            {error ->
                Log.d("error", error.toString())
            }
        )
        queue.add(request)

        return view
    }
}