package com.example.lowcal.auth
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.lowcal.databinding.FragmentSignUpBinding
import com.example.lowcal.getstarted.GS1_inputNameActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUpFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var binding: FragmentSignUpBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Firebase Authentication and Firestore
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Set up your UI components using View Binding
        binding.signupBtSignup.setOnClickListener {
            val email = binding.signupEtEmail.text.toString().trim()
            val password = binding.signupEtPassword.text.toString().trim()

            // Validate email and password (add your own validation logic)
            if (email.isEmpty() || password.isEmpty()) {
                // Handle empty fields
                showToast("Please fill in all fields.")
                return@setOnClickListener
            }

            binding.progressBar.visibility = View.VISIBLE

            // Create user with email and password
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity()) { task ->
                    try {
                        binding.progressBar.visibility = View.GONE
                        if (task.isSuccessful) {
                            // Sign up success
                            val user = auth.currentUser
                            // Add additional data to Firestore
                            addUserToFirestore(user?.uid, email)
                            val welcomeIntent = Intent(requireContext(), GS1_inputNameActivity::class.java)
                            welcomeIntent.putExtra("val_uid", user?.uid);
                            welcomeIntent.putExtra("val_email", email);
                            startActivity(welcomeIntent)

                            showToast("Sign up Success")
                        } else {
                            // If sign up fails, display a message to the user.
                            throw task.exception ?: Exception("Unknown error occurred.")
                        }
                    } catch (e: Exception) {
                        showToast("Sign up failed: ${e.message}")
                    }
                }
        }


    }

    private fun addUserToFirestore(userId: String?, email: String) {
        // Add user data to Firestore
        val user = hashMapOf(
            "email" to email,
            // Add any additional data you want to store
        )

        if (userId != null) {
            firestore.collection("users")
                .document(userId)
                .set(user)
                .addOnSuccessListener {
                    // Log.d(TAG, "DocumentSnapshot successfully written!")
                    // TODO: Handle success
                }
                .addOnFailureListener { e ->
                    // Log.w(TAG, "Error writing document", e)
                    // TODO: Handle failure
                }
        }
    }

    companion object {
        // private const val TAG = "SignUpFragment"
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
