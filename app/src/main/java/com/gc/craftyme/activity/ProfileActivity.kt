package com.gc.craftyme.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import com.gc.craftyme.R
import com.gc.craftyme.helpers.DUBaseActivity
import com.gc.craftyme.helpers.Extensions.toast
import com.gc.craftyme.model.User
import com.google.gson.Gson

class ProfileActivity : DUBaseActivity() {

    lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        this.getProfile()
    }

    fun btnSaveAction(view: View){
        this.updateProfile()
    }

    fun btnLogoutAction(view: View){
        this.signOut()
    }

    //Firebase
    private fun getProfile() {
        firebaseDatabase.child(NODE_USERS).child(firebaseAuth.uid.toString()).get()
            .addOnSuccessListener {
                Log.i(TAG, "Got value ${it.value}")
                user = Gson().fromJson(it.value.toString(), User::class.java)
                if(user != null){
                    this.setTextFromViewById(R.id.firstName, user.firstName)
                    this.setTextFromViewById(R.id.lastName, user.lastName)
                    this.setTextFromViewById(R.id.email, user.email)
                }
            }.addOnFailureListener{
                Log.e(TAG, "Error getting User data", it)
            }
    }

    private fun updateProfile() {
        user.firstName = this.getTextFromViewById(R.id.firstName)
        user.lastName = this.getTextFromViewById(R.id.lastName)
        firebaseDatabase.child("users").child(user.id).setValue(user)
            .addOnSuccessListener {
                Log.i(TAG, "User Updated")
            }
            .addOnFailureListener{
                Log.e(TAG, "Error updating User data", it)
            }
    }
}
