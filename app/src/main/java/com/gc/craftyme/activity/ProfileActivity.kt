package com.gc.craftyme.activity

import android.Manifest
import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.gc.craftyme.BuildConfig
import com.gc.craftyme.R
import com.gc.craftyme.helpers.DUBaseActivity
import com.gc.craftyme.helpers.Extensions.toast
import com.gc.craftyme.model.User
import com.google.android.material.button.MaterialButton
import com.squareup.picasso.Picasso
import com.swein.easypermissionmanager.EasyPermissionManager
import java.io.File

class ProfileActivity : DUBaseActivity() {

    lateinit var user: User
    private val ID = "id"
    private val FIRST_NAME = "firstName"
    private val LAST_NAME = "lastName"
    private val EMAIL = "email"
    private val PROFILE_PICTURE = "profilePicture"

    private val easyPermissionManager = EasyPermissionManager(this)
    private var imageUri: Uri? = null

    private val profileImage by lazy { findViewById<ImageView>(R.id.profileImage) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        this.getProfile()
        setClickListeners()
    }

    private fun setClickListeners() {
        findViewById<Button>(R.id.captureImage).setOnClickListener { captureImage() }
        findViewById<Button>(R.id.chooseImage).setOnClickListener { chooseImage() }
    }

    private val captureImageResult = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
        if (isSuccess) {
            imageUri?.let { uri ->
                profileImage.setImageURI(uri)
            }
        }
    }

    private val chooseImageResult = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            imageUri = uri
            profileImage.setImageURI(imageUri) }
    }

    private fun captureImage() {
        easyPermissionManager.requestPermission("permisison", "permission are necessary", "setting",
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE)){
            lifecycleScope.launchWhenStarted {
                getTmpFileUri().let { uri ->
                    imageUri = uri
                    captureImageResult.launch(imageUri)

                }
            }
        }
    }

    private fun chooseImage() {
        easyPermissionManager.requestPermission("permisison", "permission are necessary", "setting",
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE)){
            chooseImageResult.launch("image/*")
        }
    }

    fun btnUpdateAction(view: View){
        this.updateProfile()
    }

    fun btnLogoutAction(view: View){
        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle(R.string.logout)
        //set message for alert dialog
        builder.setMessage(R.string.Are_you_sure_logout)
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton("Yes"){dialogInterface, which ->
            this.signOut()
        }
        //performing cancel action
        builder.setNeutralButton("Cancel"){dialogInterface , which -> }

        //performing negative action
        builder.setNegativeButton("No"){dialogInterface, which -> }

        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()

        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    //Firebase
    private fun getProfile() {
        firebaseDatabase.child(NODE_USERS).child(firebaseAuth.uid.toString()).get()
            .addOnSuccessListener {
                Log.i(TAG, "Got value ${it.value}")
                var userMap = it.getValue() as Map<String, Any>
                user = User(
                    userMap.get(ID) as String,
                    userMap.get(FIRST_NAME) as String,
                    userMap.get(LAST_NAME) as String,
                    userMap.get(EMAIL) as String,
                    userMap.get(PROFILE_PICTURE) as String
                )
                if(user != null){
                    this.setTextFromViewById(R.id.firstName, user.firstName)
                    this.setTextFromViewById(R.id.lastName, user.lastName)
                    this.setTextFromViewById(R.id.email, user.email)
                    val profileImage: ImageView = findViewById(R.id.profileImage) as ImageView
                    if(user.profilePicture != ""){
                        Picasso.get().load(user.profilePicture).into(profileImage)
                    }
                    else{
                        profileImage.setImageResource(R.drawable.splash)
                    }
                }
            }.addOnFailureListener{
                Log.e(TAG, "Error getting User data", it)
            }
    }

    private fun updateProfile() {
        user.firstName = this.getTextFromViewById(R.id.firstName)
        user.lastName = this.getTextFromViewById(R.id.lastName)
        var a = "";
        if(imageUri != null){
            var storageRef = firebaseStorage.getReference((user.id)+getUniqueId()+".jpg");
            storageRef.putFile(imageUri!!).addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener {
                    val userValues = buildMap(2){
                        put(FIRST_NAME, user.firstName)
                        put(LAST_NAME, user.lastName)
                        put(PROFILE_PICTURE, it.toString())
                    }
                    firebaseDatabase.child(NODE_USERS).child(user.id).updateChildren(userValues)
                        .addOnSuccessListener {
                            Log.i(TAG, "User Updated")
                            toast("User profile updated")
                        }
                        .addOnFailureListener{
                            Log.e(TAG, "Error updating User data", it)
                        }
                }
            }
        }else{
            val userValues = buildMap(2){
                put(FIRST_NAME, user.firstName)
                put(LAST_NAME, user.lastName)
            }
            firebaseDatabase.child(NODE_USERS).child(user.id).updateChildren(userValues)
                .addOnSuccessListener {
                    Log.i(TAG, "User Updated")
                    toast("User profile updated")
                }
                .addOnFailureListener{
                    Log.e(TAG, "Error updating User data", it)
                }
        }

    }
}
