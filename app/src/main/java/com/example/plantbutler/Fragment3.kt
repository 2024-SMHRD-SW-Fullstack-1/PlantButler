package com.example.plantbutler

import android.app.Activity
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.Intent.getIntent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject
import org.w3c.dom.Text


class Fragment3 : Fragment() {
    lateinit var mContext: Context
    lateinit var mActivity: Activity
    lateinit var queue: RequestQueue

    lateinit var rvMypageList:RecyclerView
    lateinit var postList: ArrayList<PostVOWithMemImg>
    lateinit var adapter: MyPageAdapter
    lateinit var btnEdit:Button
    lateinit var ivProfileImg:ImageView
    //lateinit var imageUri:String



    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        mActivity = context as Activity
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_3, container, false)
        rvMypageList = view.findViewById<RecyclerView>(R.id.rvMypageList)
        postList = ArrayList<PostVOWithMemImg>()
        queue = Volley.newRequestQueue(mContext)

        ivProfileImg = view.findViewById<ImageView>(R.id.ivProfileImg)
        val tvCntPlant: TextView = view.findViewById(R.id.tvCntPlant)
        val tvCntPost: TextView = view.findViewById(R.id.tvCntPost)
        val tvCntComment: TextView = view.findViewById(R.id.tvCntComment)

        // 로그인 정보 가져오기
        val sharedPreferences = activity?.getSharedPreferences("member", Context.MODE_PRIVATE)
        val memNick = sharedPreferences?.getString("memNick","planty")
        val memId = sharedPreferences?.getString("memId", "id")
        val memImg = sharedPreferences?.getString("memImg", "img")

        val byteString:ByteArray = Base64.decode(memImg, Base64.DEFAULT)
        val byteArray = BitmapFactory.decodeByteArray(byteString, 0, byteString.size)

        ivProfileImg.setImageBitmap(byteArray)

        // 닉네임
        val tvNick = view.findViewById<TextView>(R.id.tvNick)
        tvNick.text = memNick

        // 회원정보 수정
        btnEdit = view.findViewById(R.id.btnEdit)
        btnEdit.setOnClickListener {
            val intent = Intent(context,EditActivity::class.java)
            startActivity(intent)
        }
        // 로그아웃
        var btnLogout = view.findViewById<Button>(R.id.btnLogout)
        btnLogout.setOnClickListener {
            val editor = sharedPreferences!!.edit()
            editor.clear()
            editor.commit()
            Toast.makeText(mContext, "로그아웃되었습니다", Toast.LENGTH_SHORT).show()
            val intent = Intent(context,LoginActivity::class.java)
            startActivity(intent)
        }

        // 내 기록 확인 요청
        reqMyrecord(memId, tvCntPlant, tvCntPost, tvCntComment)

        return view
    }

    private fun reqMyrecord(memId: String?, tvCntPlant: TextView, tvCntPost: TextView, tvCntComment: TextView) {
        adapter = MyPageAdapter(mContext, postList)
        rvMypageList.adapter = adapter
        rvMypageList.layoutManager = GridLayoutManager(mContext, 3)
        val request = object : StringRequest(
            Method.POST,
            "http://192.168.219.60:8089/plantbutler/mypage",
            { response ->
                Log.d("mypageResponse", response.toString())
                val jsonObject = JSONObject(response)

                // 키우는 식물, 작성한 글, 작성한 댓글
                val cntPlant = jsonObject.getString("cntPlant")
                val cntPost = jsonObject.getString("cntPost")
                val cntComment = jsonObject.getString("cntComment")

                tvCntPlant.setText(cntPlant)
                tvCntPost.setText(cntPost)
                tvCntComment.setText(cntComment)

                //내 게시물
                val myPostList = jsonObject.getJSONArray("postList")
                for (i in 0 until jsonObject.getJSONArray("postList").length()) {
                    val result = jsonObject.getJSONArray("postList").getJSONObject(1)
                    val idx = result.getString("idx").toInt()
                    val id = result.getJSONObject("member").getString("id")
                    val content = result.getString("content")
                    val views = result.getString("views").toInt()
                    val title = result.getString("title")
                    val date = result.getString("date")
                    val img: String
                    val nick = result.getJSONObject("member").getString("nick")
                    val memImg = result.getJSONObject("member").getString("img")



                    if (result.getString("img") != null) {
                        img = result.getString("img")
                    } else {
                        img = null.toString()
                    }
                    Log.d("defaulImg", img.toString())

                    postList.add(PostVOWithMemImg(idx, id, img, content, views, title, date, nick, memImg))

                    adapter.notifyDataSetChanged()
                }
            },
            { error ->
                Log.d("error", error.toString())
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params: MutableMap<String, String> = HashMap<String, String>()
                params.put("Member", Gson().toJson(memId))

                return params
            }
        }
        queue.add(request)
    }

}