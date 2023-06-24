package com.example.projecttravelog_pbp.ui.signin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignInViewModel : ViewModel() {
    // TODO: Implement the ViewModel
    private val _login = MutableLiveData<String>()
    val login: LiveData<String> = _login

    private val _user = MutableLiveData<FirebaseUser?>()
    val user: LiveData<FirebaseUser?> = _user

    fun loginFirebase(email: String, password: String) =
        Firebase.auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                _login.postValue("Login Success!")
            } else {
                _login.postValue(it.exception.toString())
            }
        }

    fun session() {
        if (Firebase.auth.currentUser != null) {
            _user.postValue(Firebase.auth.currentUser)
        } else {
            _user.postValue(null)
        }
    }
}