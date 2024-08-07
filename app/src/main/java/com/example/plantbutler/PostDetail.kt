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
import android.widget.SearchView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import org.json.JSONArray

class PostDetail : Fragment() {

    lateinit var queue: RequestQueue
    lateinit var adapter: CommentAdapter
    lateinit var commentList: ArrayList<CommentVOWithMemImg>
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

        // 게시글 idx
        val idx = arguments?.getString("idx")
        queue = Volley.newRequestQueue(mContext)

        // 로그인한 회원 id
        val sharedPreferences = activity?.getSharedPreferences("member", Context.MODE_PRIVATE)
        val memId = sharedPreferences?.getString("memId", "default_value")

        val rvCommentList: RecyclerView = view.findViewById(R.id.rvCommentList)
        val tvDetailTile: TextView = view.findViewById(R.id.tvDetailTitle)
        val tvDetailViews: TextView = view.findViewById(R.id.tvDetailViews)
        val tvDetailNick: TextView = view.findViewById(R.id.tvDetailNick)
        val tvDetailDate: TextView = view.findViewById(R.id.tvDetailDate)
        val tvDetailContent: TextView = view.findViewById(R.id.tvDetailContent)
        val ivDetailPostImg: ImageView = view.findViewById(R.id.ivDetailPostImg)
        val ivDetailImg: ImageView = view.findViewById(R.id.ivDetailImg)
        val tvDetailDel: TextView = view.findViewById(R.id.tvDetailDel)
        val etInputComment: EditText = view.findViewById(R.id.etInputComment)

        val btnCommentAct: Button = view.findViewById(R.id.btnCommentAct)

        tvDetailTile.setText(arguments?.getString("title"))
        tvDetailViews.setText("조회수 "+arguments?.getString("views")+"회")
        tvDetailNick.setText(arguments?.getString("nick"))
        tvDetailDate.setText(arguments?.getString("date"))
        tvDetailContent.setText(arguments?.getString("content"))

        arguments?.getString("memImg")?.let { Log.d("memImg", it) }

        if(arguments?.getString("postImg") != null) {
            val byteString = Base64.decode(arguments?.getString("postImg"), Base64.DEFAULT)
            val byteArray = BitmapFactory.decodeByteArray(byteString, 0, byteString.size)
            ivDetailPostImg.setImageBitmap(byteArray)
        }else {
            ivDetailPostImg.visibility = View.GONE
        }

        if(arguments?.getString("memImg") != null) {
            val byteString = Base64.decode(arguments?.getString("memImg"), Base64.DEFAULT)
            val byteArray = BitmapFactory.decodeByteArray(byteString, 0, byteString.size)
            ivDetailImg.setImageBitmap(byteArray)
        }else {
            ivDetailImg.setVisibility(View.GONE)
        }

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

        // 게시글 삭제 보이기 / 안 보이기
        if (memId == arguments?.getString("id")) {
            tvDetailDel.visibility = View.VISIBLE
        }else {
            tvDetailDel.visibility = View.GONE
        }

        // 게시글 삭제
        tvDetailDel.setOnClickListener {

            val request = StringRequest(
                Request.Method.GET,
                "http://192.168.219.60:8089/plantbutler/post/delete/$idx",
                {response ->
                    Log.d("postDelete", response.toString())
                    val fragment = Fragment1()
                    (context as MainActivity).replaceFragment(fragment)
                },
                {error ->
                    Log.d("error", error.toString())
                }
            )
            queue.add(request)
        }

        // 댓글 등록
        btnCommentAct.setOnClickListener {
            val content = etInputComment.text.toString()
            val comment = CommentVO(null, idx, memId, content)

            val request = object: StringRequest(
                Request.Method.POST,
                "http://192.168.219.60:8089/plantbutler/comment/add",
                {response ->
                    Log.d("commentAdd", response.toString())
                    refreshCommentList(memId, rvCommentList, idx)
                    etInputComment.setText("") // 댓글 입력창 초기화
                },
                {error ->
                    Log.d("error", error.toString())
                }
            ){
                override fun getParams(): MutableMap<String, String> {
                    val params:MutableMap<String, String> = HashMap<String, String>()

                    params.put("addComment", Gson().toJson(comment))
                    return params
                }
            }
            queue.add(request)

        }

        refreshCommentList(memId, rvCommentList, idx)


        return view
    }

    private fun refreshCommentList(
        memId: String?,
        rvCommentList: RecyclerView,
        idx: String?
    ) {
        // 댓글 목록
        commentList = ArrayList<CommentVOWithMemImg>()
        adapter = CommentAdapter(mContext, commentList, memId)
        rvCommentList.adapter = adapter
        rvCommentList.layoutManager = LinearLayoutManager(mContext)

        val request = StringRequest(
            Request.Method.GET,
            "http://192.168.219.60:8089/plantbutler/post/$idx",
            { response ->
                Log.d("commentList", response.toString())
                val jsonArray = JSONArray(response)
                for (i in 0 until jsonArray.length()) {
                    val result = jsonArray.getJSONObject(i)
                    val commentIdx = result.getInt("commentIdx")
                    val postIdx = result.getInt("postIdx")
                    var id = result.getJSONObject("member").getString("id")
                    val nick = result.getJSONObject("member").getString("nick")
                    val content = result.getString("content")
                    val memImg: String

                    if (result.getJSONObject("member").getString("img") != null) {
                        memImg = result.getJSONObject("member").getString("img")
                    } else {
                        memImg = ""
                    }

                    commentList.add(
                        CommentVOWithMemImg(
                            commentIdx,
                            postIdx,
                            id,
                            content,
                            nick,
                            memImg
                        )
                    )
                    adapter.notifyDataSetChanged()

                }
            },
            { error ->
                Log.d("error", error.toString())
            }
        )
        queue.add(request)
    }

}