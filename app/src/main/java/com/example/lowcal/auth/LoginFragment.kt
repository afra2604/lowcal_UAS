package com.example.lowcal.auth

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.lowcal.BottomNavigationActivity


import com.example.lowcal.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentLoginBinding
    private lateinit var sharedPreferences: SharedPreferences
    private val viewModel: AuthViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Firebase Authentication
        auth = FirebaseAuth.getInstance()


        sharedPreferences = requireContext().getSharedPreferences("myAppPreferences", Context.MODE_PRIVATE)



        // Set up your UI components using View Binding
        binding.loginBtLogin.setOnClickListener {
            val email = binding.loginEtEmail.text.toString().trim()
            val password = binding.loginEtPassword.text.toString().trim()



            // Validate email and password (add your own validation logic)
            if (email.isEmpty() || password.isEmpty()) {
                showToast("Please fill in all fields.")
                return@setOnClickListener
            }

            binding.progressBarLogin.visibility = View.VISIBLE

//            // Sign in user with email and password
//            auth.signInWithEmailAndPassword(email, password)
//                .addOnCompleteListener(requireActivity()) { task ->
//                    try {
//                        if (task.isSuccessful) {
//                            // Login success
//                            val user = auth.currentUser
//                            // Store the user ID (you can use ViewModel, SharedPreferences, etc.)
//                            val userId = user?.uid
//
//                            saveUserid(userId.toString())
//
//                            val welcomeIntent = Intent(requireContext(), BottomNavigationActivity::class.java)
//                            welcomeIntent.putExtra("val_uid", user?.uid);
//                            startActivity(welcomeIntent)
//
//                            showToast("Login successful!")
//                            // TODO: Navigate to the home fragment or activity
//                        } else {
//                            // If login fails, display a message to the user.
//                            throw task.exception ?: Exception("Unknown error occurred.")
//                        }
//                    } catch (e: Exception) {
//                        showToast("Login failed: ${e.message}")
//                    }
//                }

            viewModel.loginUser(email, password) { success, message ->
                binding.progressBarLogin.visibility = View.GONE
                if (success) {
                    // If login is successful, fetch user role
                    viewModel.fetchUserRoleFromFirestore() { role ->
                        if (role == 1){
                            val toMainActivity = Intent(requireContext(), BottomNavigationActivity::class.java)
                            startActivity(toMainActivity)
                        }
                    }

                } else {
                    // If login is not successful, show an error message
                    showToast("Gagal: $message")
                }
            }


        }
        }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }




    }


//
//    fun saveUserid(userId: String) {
//        val editor = sharedPreferences.edit()
//        editor.putString("userId", userId)
//        editor.putInt("isLoggegIn",1);
//        editor.apply()
//    }



