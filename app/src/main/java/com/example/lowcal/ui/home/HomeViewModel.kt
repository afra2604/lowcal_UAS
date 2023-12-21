package com.example.lowcal.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.lowcal.data.database.MenuRoomDatabase
import com.example.myapplication.data.database.MenuUserDAO

class HomeViewModel(application: Application) : AndroidViewModel(application) {


//    private val menuUserDAO: MenuUserDAO


    private val menuUserDAO: MenuUserDAO

    init {
        val database = MenuRoomDatabase.getDatabase(application.applicationContext)
        menuUserDAO = database?.MenuUserDAO() ?: throw Exception("Database not initialized")
    }

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text

    fun getTotalCaloriesForCurrentUserAndDate(userId: String, date: String): LiveData<Int> {
        return menuUserDAO.getTotalCaloriesByUserIdAndDate(userId,date)
    }

    fun getCaloriesByType(userId: String, date: String, type:String): LiveData<Int> {
        return menuUserDAO.getCaloriesByType(userId,date, type)
    }

}