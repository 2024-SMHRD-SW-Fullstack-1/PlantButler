package com.example.plantbutler

import android.content.Context
import android.content.SharedPreferences

object SessionManager {
    private const val PREF_NAME = "PlantButlerPrefs"  // SharedPreferences 파일 이름
    private const val KEY_IS_FIRST_RUN = "isFirstRun" // 첫 실행 여부를 저장할 키
    private const val KEY_IS_LOGGED_IN = "isLoggedIn" // 로그인 상태를 저장할 키

    // SharedPreferences 객체를 얻기 위한 함수
    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    // 앱이 처음 실행된 것인지 확인하는 함수
    fun isFirstRun(context: Context): Boolean {
        return getSharedPreferences(context).getBoolean(KEY_IS_FIRST_RUN, true)
    }

    // 앱의 첫 실행 여부를 설정하는 함수
    fun setFirstRun(context: Context, isFirstRun: Boolean) {
        val editor = getSharedPreferences(context).edit()
        editor.putBoolean(KEY_IS_FIRST_RUN, isFirstRun)
        editor.apply()
    }

    // 로그인 상태를 설정하는 함수
    fun setLogin(context: Context, isLoggedIn: Boolean) {
        val editor = getSharedPreferences(context).edit()
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn)
        editor.apply()
    }

    // 로그인 상태를 확인하는 함수
    fun isLoggedIn(context: Context): Boolean {
        return getSharedPreferences(context).getBoolean(KEY_IS_LOGGED_IN, false)
    }
}
