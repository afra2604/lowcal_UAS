package com.example.lowcal.admin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lowcal.auth.AuthActivity
import com.example.lowcal.data.model.firestore.MenuAdminFS
import com.example.lowcal.databinding.ActivityAdminBinding
import com.example.lowcal.util.SharedPreferencesHelper
import com.google.firebase.firestore.FirebaseFirestore

class AdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBinding
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private lateinit var admindataAdapter: AdminDataAdapter
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance()
        sharedPreferencesHelper = SharedPreferencesHelper.getInstance(this@AdminActivity)

        val recyclerView = binding.recyclerviewAdmin
        recyclerView.layoutManager = LinearLayoutManager(this)
        admindataAdapter = AdminDataAdapter()
        recyclerView.adapter = admindataAdapter
        fetchDataAndObserve()

        // Set text
        binding.adminTvName.text = sharedPreferencesHelper.getUserName().toString()

        // Logout
        binding.adminLogout.setOnClickListener {
            val toMainActivity = Intent(this@AdminActivity, AuthActivity::class.java)
            sharedPreferencesHelper.setLoggedIn(false)
            startActivity(toMainActivity)
        }

        binding.adminFabAdd.setOnClickListener{
            val toMainActivity = Intent(this@AdminActivity, AdminAddFoodActivity::class.java)
            startActivity(toMainActivity)
        }

        // Set up search functionality
        val searchEditText = binding.sgnPassword
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // Call fetchDataAndObserve with the search query
                fetchDataAndObserve(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not needed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Not needed
            }
        })
    }

    private fun fetchDataAndObserve(searchQuery: String = "") {
        try {
            val bukuCollection = firestore.collection("makanan")

            // Add a condition to filter data based on the search query
            val query = if (searchQuery.isNotEmpty()) {

                bukuCollection.whereGreaterThanOrEqualTo("foodName", searchQuery)
                    .whereLessThanOrEqualTo("foodName", searchQuery + "\uf8ff")
            } else {
                bukuCollection
            }

            // Observe Firestore changes
            query.addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    showToast(this@AdminActivity, "Error fetching data from Firestore")
                    return@addSnapshotListener
                }

                snapshot?.let { documents ->
                    val bukus = mutableListOf<MenuAdminFS>()
                    for (document in documents) {
                        val bukuId = document.id
                        val buku = document.toObject(MenuAdminFS::class.java).copy(id = bukuId)
                        bukus.add(buku)
                    }

                    // Update the UI with the Firestore data
                    admindataAdapter.setMakanan(bukus, searchQuery)
                }
            }
        } catch (e: Exception) {
            showToast(this@AdminActivity, e.toString())
            Log.d("ERRORKU", e.toString())
        }
    }

    private fun showToast(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, message, duration).show()
    }
}
