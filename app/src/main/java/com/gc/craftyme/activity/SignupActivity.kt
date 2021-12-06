package com.gc.craftyme.activity

import android.os.Bundle
import android.util.Log
import com.gc.craftyme.R
import com.gc.craftyme.helpers.DUBaseActivity
import com.gc.craftyme.helpers.Extensions.toast
import com.google.firebase.auth.FirebaseUser

class SignupActivity : DUBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
    }

    private fun updateUI(user: FirebaseUser?) {
        this.goToNextActivity(HomeActivity::class.java)
    }

    //Firebase
    private fun createAccount(email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("SignupActivity", "createUserWithEmail:success")
                    this.updateUI(firebaseAuth.currentUser)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("SignupActivity", "createUserWithEmail:failure", task.exception)
                    toast("Authentication failed.")
                    this.updateUI(null)
                }
            }
    }

}