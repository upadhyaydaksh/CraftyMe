package com.gc.craftyme.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.gc.craftyme.R
import com.gc.craftyme.helpers.DUBaseActivity

class MainActivity : DUBaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}