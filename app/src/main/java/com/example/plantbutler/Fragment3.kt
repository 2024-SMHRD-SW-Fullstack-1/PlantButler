package com.example.plantbutler

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson


class Fragment3 : Fragment() {
    lateinit var mContext: Context
    lateinit var mActivity: Activity
    lateinit var queue: RequestQueue

    lateinit var rvMypageList:RecyclerView
    lateinit var postList: ArrayList<PostVO>
    lateinit var adapter: MyPageAdapter


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

        val sharedPreferences = activity?.getSharedPreferences("member", Context.MODE_PRIVATE)
        val memNick = sharedPreferences?.getString("memNick","default_value")
        val memId = sharedPreferences?.getString("memId", "default_value")


        //editor.putString("memId", member.id)
        //editor.putString("memNick", member.nick)
        //editor.putString("memImg",member.img)

        // 닉네임
        val tvNick = view.findViewById<TextView>(R.id.tvNick)
        tvNick.text = memNick+"님"

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

}