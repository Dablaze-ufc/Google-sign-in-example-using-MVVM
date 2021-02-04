package com.example.googlesigninexample

import android.app.Application
import androidx.lifecycle.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivityViewModel(private val app: Application, private val listener: OnSignInStartedListener): AndroidViewModel(app) {
    private var auth: FirebaseAuth = Firebase.auth


    private val _currentUser = MutableLiveData<FirebaseUser>()

    val currentUser: LiveData<FirebaseUser> = _currentUser

    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(app.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

    private val googleSignInClient = GoogleSignIn.getClient(app, gso)

    fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                _currentUser.value = auth.currentUser
            } else {
                _currentUser.value = null
            }
        }
    }


    fun signIn() {
        listener.onSignInStarted(googleSignInClient)
    }

    fun signOut(){
        auth.signOut()
    }
}

@Suppress("UNCHECKED_CAST")
class MainActivityViewModelFactory(
        private val application: Application,
        private val listener: OnSignInStartedListener
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainActivityViewModel::class.java)) {

            return MainActivityViewModel(application, listener) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

interface OnSignInStartedListener {
    fun onSignInStarted(client: GoogleSignInClient?)
}