package com.example.plantbutler

import android.app.Activity
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
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

class PostDetail : Fragment() {

    lateinit var queue: RequestQueue
    lateinit var adapter: CommentAdapter
    lateinit var commentList: ArrayList<CommentVO>
    lateinit var rvCommentList: RecyclerView

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
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_post_detail, container, false)

        val idx = arguments?.getInt("idx") // post_idx
        queue = Volley.newRequestQueue(mContext)

        val rvCommentList: RecyclerView = view.findViewById(R.id.rvCommentList)
        val tvDetailTile: TextView = view.findViewById(R.id.tvDetailTitle)
        val tvDetailViews: TextView = view.findViewById(R.id.tvDetailViews)
        val tvDetailNick: TextView = view.findViewById(R.id.tvDetailNick)
        val tvDetailDate: TextView = view.findViewById(R.id.tvDetailDate)
        val tvDetailContent: TextView = view.findViewById(R.id.tvDetailContent)
        val ivDetailPostImg: ImageView = view.findViewById(R.id.ivDetailPostImg)

        val btnCommentAct: Button = view.findViewById(R.id.btnCommentAct)

        tvDetailTile.setText(arguments?.getString("title"))
        tvDetailViews.setText("조회수 "+arguments?.getInt("views")+"회")
        tvDetailNick.setText(arguments?.getString("id"))
        tvDetailDate.setText(arguments?.getString("date"))
        tvDetailContent.setText(arguments?.getString("content"))

        if(arguments?.getString("img") != null) {
            val byteString = Base64.decode(arguments?.getString("img"), Base64.DEFAULT)
            val byteArray = BitmapFactory.decodeByteArray(byteString, 0, byteString.size)
            ivDetailPostImg.setImageBitmap(byteArray)
        }else {
            ivDetailPostImg.setVisibility(View.GONE)
        }

        // 댓글 등록
        btnCommentAct.setOnClickListener {
            val etInputComment: EditText = view.findViewById(R.id.etInputComment)

        }

        // 댓글 목록
        commentList = ArrayList<CommentVO>()
        adapter = CommentAdapter(mContext, commentList)
        rvCommentList.adapter = adapter
        rvCommentList.layoutManager = LinearLayoutManager(mContext)

        val request = StringRequest(
            Request.Method.GET,
            "http://192.168.219.60:8089/plantbutler/post/$idx",
            {response ->
                Log.d("response", response.toString())
                val jsonArray = JSONArray(response)
                val commentListType: Type = object : TypeToken<List<CommentVO>>() {}.type
                val result: ArrayList<CommentVO> = Gson().fromJson(jsonArray.toString(), commentListType)
                for(i in 0 until result.size) {
                    commentList.add(result[i])
                    adapter.notifyDataSetChanged()
                }
            },
            {error ->
                Log.d("error", error.toString())
            }
        )
        queue.add(request)


        return view
    }

}