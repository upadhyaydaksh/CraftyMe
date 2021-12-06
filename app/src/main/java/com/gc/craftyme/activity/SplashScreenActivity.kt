package com.gc.craftyme.activity

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.gc.craftyme.R
import com.gc.craftyme.helpers.DUBaseActivity
import com.gc.craftyme.utils.Constants

class SplashScreenActivity : DUBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
    }

    public override fun onStart() {
        super.onStart()
        Handler(Looper.getMainLooper()).postDelayed({
            // Check if user is signed in (non-null) and update UI accordingly.
            val currentUser = firebaseAuth.currentUser
            if(currentUser != null){
                this.goToNextActivity(HomeActivity::class.java)
            }
            else{
                this.goToNextActivity(MainActivity::class.java)
            }
        }, Constants.SPLASH_TIME_OUT)
    }
}