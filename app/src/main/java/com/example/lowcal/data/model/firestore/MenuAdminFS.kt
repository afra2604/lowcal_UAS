package com.example.lowcal.data.model.firestore


data class MenuAdminFS(
    val id : String = "",
    val userId : String = "",
    val foodName: String = "",
    val foodCalorie: Int = 0,
    val date: String = "",
)


