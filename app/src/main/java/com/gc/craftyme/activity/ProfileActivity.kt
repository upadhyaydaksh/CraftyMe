package com.gc.craftyme.activity

import android.os.Bundle
import android.view.View
import com.gc.craftyme.R
import com.gc.craftyme.helpers.DUBaseActivity

class ProfileActivity : DUBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
    }

    fun btnLogoutAction(view: View){
        this.signOut()
    }
}