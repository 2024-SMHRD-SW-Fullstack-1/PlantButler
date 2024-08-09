package com.example.plantbutler

import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mainContent = findViewById<FrameLayout>(R.id.mainContent)
        val bnv = findViewById<BottomNavigationView>(R.id.bnv)

        supportFragmentManager.beginTransaction().replace(
            mainContent.id, Fragment1()).commit()

        bnv.setOnItemSelectedListener { item ->
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
