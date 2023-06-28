package com.example.projecttravelog_pbp.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class SignUpViewModel : ViewModel() {
    private val _register = MutableLiveData<String>()
    val register: LiveData<String> = _register

    fun registerFirebase(email: String, password: String) {
        val username = getUsernameFromEmail(email)

        // Create user in Firebase Authentication
        Firebase.auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { authTask ->
                if (authTask.isSuccessful) {
                    // User registration successful
                    val user = authTask.result?.user
                    user?.let {
                        val userId = it.uid
                        // Create a new document in Firestore for the user
                        val userDoc = FirebaseFirestore.getInstance().collection("users").document(userId)
                        val userData = hashMapOf(
                            "username" to username,
                            "email" to email
                        )
                        // Set the user data in Firestore
                        userDoc.set(userData)
                            .addOnSuccessListener {
                                _register.postValue("Register Success!")
                            }
                            .addOnFailureListener { exception ->
                                _register.postValue(exception.message)
                            }
                    }
                } else {
                    // User registration failed
                    _register.postValue(authTask.exception?.message)
                }
            }
    }

    private fun getUsernameFromEmail(email: String): String {
        // Split email by '@' and return the first part as username
        return email.split("@")[0]
    }
}