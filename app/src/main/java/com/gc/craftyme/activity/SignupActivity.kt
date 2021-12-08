package com.gc.craftyme.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import com.gc.craftyme.R
import com.gc.craftyme.helpers.DUBaseActivity
import com.gc.craftyme.helpers.Extensions.toast
import com.gc.craftyme.model.User
import com.google.firebase.auth.FirebaseUser

class SignupActivity : DUBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
    }

    fun btnSignupAction(view: View){
        this.createAccount()
    }

    private fun updateUI(user: FirebaseUser?) {
        this.goToNextActivity(LoginActivity::class.java)
    }

    //Firebase
    private fun createAccount() {
        val firstName: String = this.getTextFromViewById(R.id.firstName)
        val lastName: String = this.getTextFromViewById(R.id.lastName)
        val email: String = this.getTextFromViewById(R.id.email)
        val password: String = this.getTextFromViewById(R.id.password)
        val confirmPassword: String = this.getTextFromViewById(R.id.confirmPassword)
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    var user = User(firebaseAuth.uid.toString() ,firstName, lastName, email, "")
                    firebaseDatabase.child(NODE_USERS).child(user.id).setValue(user)
                        .addOnSuccessListener{
                            this.updateUI(firebaseAuth.currentUser)
                        }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    toast("Authentication failed.")
                    this.updateUI(null)
                }
            }
    }

}