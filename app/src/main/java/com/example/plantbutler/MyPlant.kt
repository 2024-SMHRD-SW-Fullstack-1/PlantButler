package com.example.plantbutler

data class MyPlant(
    val myplantIdx: Int,
    val memberId: String?,
    val plantIdx: Int,
    val nickname: String,
    val goal: String,
    val startDate: String?,
    val imageUrl: String? // 필요한 경우 추가
)
