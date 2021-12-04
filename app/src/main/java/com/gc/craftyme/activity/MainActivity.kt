package com.gc.craftyme.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.gc.craftyme.R
import com.gc.craftyme.helpers.DUBaseActivity


class MainActivity : DUBaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun btnSignupAction(view: View){
        val intent = Intent(this, SignupActivity::class.java)
        startActivity(intent)
    }

    fun btnLoginAction(view: View){
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }
}