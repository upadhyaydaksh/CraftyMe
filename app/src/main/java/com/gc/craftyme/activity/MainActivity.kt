package com.gc.craftyme.activity

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import com.gc.craftyme.R
import com.gc.craftyme.helpers.DUBaseActivity
import com.gc.craftyme.helpers.Extensions.toast
import com.google.firebase.auth.FirebaseUser


class MainActivity : DUBaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun btnSignupAction(view: View){
        this.goToNextActivity(ProfileActivity::class.java)
    }

    fun btnLoginAction(view: View){
        val email: String = this.getTextFromViewById(R.id.email)
        val password: String = this.getTextFromViewById(R.id.password)
        this.signIn(email, password)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var itemView = item.itemId
        when(itemView){
            R.id.profile -> {
                this.goToNextActivity(ProfileActivity::class.java)
            }
        }
        return false
    }


    private fun updateUI(user: FirebaseUser?) {
        this.goToNextActivity(HomeActivity::class.java)
    }

    //Firebase
    private fun signIn(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("SignInActivity", "signInWithEmail:success")
                    val user = firebaseAuth.currentUser
                    this.updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("SignInActivity", "signInWithEmail:failure", task.exception)
                    toast("Authentication failed. Please Try Again")
                }
            }
    }

}