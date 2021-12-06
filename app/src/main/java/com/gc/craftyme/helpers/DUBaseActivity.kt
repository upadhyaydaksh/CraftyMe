package com.gc.craftyme.helpers

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.gc.craftyme.activity.HomeActivity
import com.gc.craftyme.activity.LoginActivity
import com.google.firebase.auth.FirebaseAuth

open class DUBaseActivity : AppCompatActivity() {

    protected lateinit var firebaseAuth: FirebaseAuth

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()
    }

    fun getTextFromViewById(id: Int): String {
        val text = (findViewById(id) as TextView).text.toString()
        return text
    }

    fun goToNextActivity(activity: Class<*>?){
        val intent = Intent(this, activity)
        startActivity(intent)
    }

    fun goToNextActivityWithoutHistory(activity: Class<*>?){
        val intent = Intent(this, activity)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    fun signOut(){
        FirebaseAuth.getInstance().signOut();
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

}