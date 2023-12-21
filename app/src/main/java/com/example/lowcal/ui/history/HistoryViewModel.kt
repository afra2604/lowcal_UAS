package com.example.lowcal.ui.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.lowcal.data.database.MenuRoomDatabase
import com.example.myapplication.data.database.MenuUserDAO

class HistoryViewModel(application: Application) : AndroidViewModel(application){


    private val menuUserDAO: MenuUserDAO

    init {
        val database = MenuRoomDatabase.getDatabase(application.applicationContext)
        menuUserDAO = database?.MenuUserDAO() ?: throw Exception("Database not initialized")
    }



}