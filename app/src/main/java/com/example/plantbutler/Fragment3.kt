package com.example.plantbutler

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.Intent.getIntent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import org.json.JSONArray


class Fragment3 : Fragment() {
    lateinit var mContext: Context
    lateinit var mActivity: Activity
    lateinit var queue: RequestQueue

    lateinit var rvMypageList:RecyclerView
    lateinit var postList: ArrayList<PostVO>
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
        postList = ArrayList<PostVO>()
        ivProfileImg = view.findViewById<ImageView>(R.id.ivProfileImg)
        //ivProfileImg.setImageResource(R.drawable.profile)



        // 로그인 정보 가져오기
        val sharedPreferences = activity?.getSharedPreferences("member", Context.MODE_PRIVATE)
        val memNick = sharedPreferences?.getString("memNick","planty")
        val memId = sharedPreferences?.getString("memId", "id")

        // 닉네임
        val tvNick = view.findViewById<TextView>(R.id.tvNick)
        tvNick.text = memNick+"님"
        // 회원정보 수정
        btnEdit = view.findViewById(R.id.btnEdit)
        btnEdit.setOnClickListener {
            val intent = Intent(context,EditActivity::class.java)
            startActivity(intent)
        }

        queue = Volley.newRequestQueue(mContext)

        adapter = MyPageAdapter(mContext,postList)
        rvMypageList.adapter = adapter
        rvMypageList.layoutManager = GridLayoutManager(mContext,3)

        // 내 기록 확인 요청
        val request =  object: StringRequest(
            Request.Method.POST,
            "http://192.168.219.60:8089/plantbutler/mypage",
            {response->
                Log.d("mypageResponse",response.toString())
                //내 게시물
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
                    }else{
                        img = null.toString()
                    }

                    postList.add(PostVO(idx, nick, img, content, views, title, date))

                    adapter.notifyDataSetChanged()
                }



            },
            {error->
                Log.d("error",error.toString())
            }
        ){
            override fun getParams():MutableMap<String,String>{
                val params:MutableMap<String,String> = HashMap<String,String>()
                params.put("Member", Gson().toJson(memId))

                return params
            }
        }
        queue.add(request)


        return view
    }
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        // SharedPreferences로부터 이미지 URI 불러오기
//        val sharedPreferences = activity?.getSharedPreferences("profileImg", Context.MODE_PRIVATE)
//        val imageUri = sharedPreferences?.getString("imageUri", null)
//        if (imageUri != null) {
//            val uri = Uri.parse(imageUri)
//            ivProfileImg.setImageURI(uri)
//        } else {
//            // 기본 이미지 설정
//            ivProfileImg.setImageResource(R.drawable.profile)
//        }
//    }

}