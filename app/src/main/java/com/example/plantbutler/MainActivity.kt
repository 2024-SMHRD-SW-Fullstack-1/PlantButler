package com.example.plantbutler

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import org.json.JSONObject
import java.lang.reflect.Type

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mainContent = findViewById<FrameLayout>(R.id.mainContent)
        val bnv = findViewById<BottomNavigationView>(R.id.bnv)

        supportFragmentManager.beginTransaction().replace(
            mainContent.id, Fragment1()).commit()

        bnv.setOnItemSelectedListener { // item : 선택한 메뉴 (아이디)
                item ->
            when(item.itemId) {
                R.id.m1 -> {
                    supportFragmentManager.beginTransaction().replace(
                        mainContent.id, Fragment1()).commit()
                }
                R.id.m2 -> {
                    supportFragmentManager.beginTransaction().replace(
                        mainContent.id, Fragment2()).commit()
                }
                R.id.m3 -> {
                    supportFragmentManager.beginTransaction().replace(
                        mainContent.id, Fragment3()).commit()
                }
            }
            true
        }

    }
    fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.mainContent, fragment)
            .addToBackStack(null) // Optional: adds the transaction to the back stack
            .commit()
    }

}