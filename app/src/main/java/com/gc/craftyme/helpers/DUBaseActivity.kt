package com.gc.craftyme.helpers

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.gc.craftyme.activity.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

open class DUBaseActivity : AppCompatActivity() {

    protected val TAG: String = this.javaClass.simpleName
    protected lateinit var firebaseAuth: FirebaseAuth
    protected lateinit var firebaseDatabase: DatabaseReference

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = Firebase.database.reference
    }

    fun setTextFromViewById(id: Int) {
        val text = (findViewById(id) as TextView)
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