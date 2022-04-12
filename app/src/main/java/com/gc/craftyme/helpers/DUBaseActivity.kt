package com.gc.craftyme.helpers

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.gc.craftyme.BuildConfig
import com.gc.craftyme.activity.HomeActivity
import com.gc.craftyme.activity.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.File

open class DUBaseActivity : AppCompatActivity() {

    protected val TAG: String = this.javaClass.simpleName
    protected lateinit var firebaseAuth: FirebaseAuth
    protected lateinit var firebaseDatabase: DatabaseReference
    protected lateinit var firebaseStorage: FirebaseStorage

    //NODES in Firebase
    protected val NODE_USERS = "users"
    protected val NODE_USERS_ARTWORKS = "artworks"

    //Artwork Fields
    protected val ARTWORK_ID = "id"
    protected val ARTWORK_TITLE = "title"
    protected val ARTWORK_CREATED_DATE = "createdDate"
    protected val ARTWORK_DESCRIPTION = "artDescription"
    protected val ARTWORK_IMAGE_URL = "artworkImageUrl"

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()
        // Initialize Firebase Database
        firebaseDatabase = Firebase.database.reference
        // Initialize Storage
        firebaseStorage = FirebaseStorage.getInstance()
    }

    fun getUniqueId(): String {
        return System.currentTimeMillis().toString()
    }

    fun setTextFromViewById(id: Int, text: String) {
        (findViewById(id) as TextView).text = text
    }

    fun getTextFromViewById(id: Int): String {
        val text = (findViewById(id) as TextView).text.toString()
        return text
    }

    fun goToNextActivity(activity: Class<*>?){
        val intent = Intent(this, activity)
        startActivity(intent)
    }

    fun goBackToHomeActivity(){
        val intent = Intent(this, HomeActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent);
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

    fun getTmpFileUri(): Uri {
        val tmpFile = File.createTempFile("tmp_image_file", ".jpg", cacheDir).apply {
            createNewFile()
            deleteOnExit()
        }

        return FileProvider.getUriForFile(applicationContext, "${BuildConfig.APPLICATION_ID}.provider", tmpFile)
    }

}