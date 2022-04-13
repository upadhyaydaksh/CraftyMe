package com.gc.craftyme.activity

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.gc.craftyme.R
import com.gc.craftyme.helpers.DUBaseActivity
import com.gc.craftyme.helpers.Extensions.toast
import com.gc.craftyme.model.Artwork
import com.gc.craftyme.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import com.swein.easypermissionmanager.EasyPermissionManager


class AddArtworkActivity : DUBaseActivity() {

    lateinit var artwork: Artwork
    var artworkId = ""
    var isNew = true

    // Image picker variables
    private var imageUri: Uri? = null
    private var currentuserId = ""
    private val artworkImage by lazy { findViewById<ImageView>(R.id.artworkImage) }

    private val easyPermissionManager = EasyPermissionManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(firebaseAuth.uid == null || firebaseAuth.uid == ""){
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        setContentView(R.layout.activity_add_artwork)
        currentuserId = FirebaseAuth.getInstance().currentUser!!.uid
        setClickListeners()
    }

    override fun onStart() {
        super.onStart()
        artworkId = intent.getStringExtra(Constants.ARTWORK_DETAIL_ID).toString()
        isNew = intent.getBooleanExtra(Constants.IS_NEW, true)
        if(isNew){
            (findViewById(R.id.delete) as Button).visibility = Button.GONE
        }else{
            this.setTextFromViewById(R.id.save, "Update")
            this.getArtwork()
        }
    }

    fun btnSaveAction(view: View){
        if(isNew){
            this.addArtwork()
        }else{
            this.updateArtwork()
        }
    }

    fun btnDeleteAction(view: View){
        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle(R.string.delete)
        //set message for alert dialog
        builder.setMessage(R.string.Are_you_sure_delete_artwork)
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton("Yes"){dialogInterface, which ->
            this.deleteArtwork()
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

    fun updateUi(){
        this.goBackToHomeActivity()
    }

    private fun setClickListeners() {
        findViewById<Button>(R.id.captureImage).setOnClickListener { captureImage() }
        findViewById<Button>(R.id.chooseImage).setOnClickListener { chooseImage() }
    }

    private val captureImageResult = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
        if (isSuccess) {
            imageUri?.let { uri ->
                Picasso.get().load(uri).into(artworkImage)
            }
        }
    }

    private val chooseImageResult = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            imageUri = uri
            Picasso.get().load(imageUri).into(artworkImage)
        }
    }

    private fun captureImage() {
        easyPermissionManager.requestPermission("Camera Permission", "Camera Permissions are necessary", "Settings",
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
        easyPermissionManager.requestPermission("Camera Permission", "Camera Permissions are necessary", "Settings",
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE)){
            chooseImageResult.launch("image/*")
        }
    }

    //Firebase
    fun addArtwork(){
        val title = this.getTextFromViewById(R.id.title)
        val description = this.getTextFromViewById(R.id.description)
        var artworkImageUrl = ""
        if(imageUri != null && imageUri.toString() != null){
            var storageRef = firebaseStorage.getReference((currentuserId)+"_ARTWORK_"+getUniqueId()+".jpg");
            storageRef.putFile(imageUri!!).addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener {
                    artworkImageUrl = it.toString()
                    artwork = Artwork(this.getUniqueId(), title, description, artworkImageUrl, "")
                    val artworks = buildMap(1){
                        put(artwork.id, artwork)
                    }
                    firebaseDatabase.child(NODE_USERS).child(firebaseAuth.uid.toString()).child(NODE_USERS_ARTWORKS).updateChildren(artworks)
                        .addOnSuccessListener {
                            Log.i(TAG, "Artwork Added successfully")
                            toast("Artwork Added Successfully")
                            this.updateUi()
                        }
                        .addOnFailureListener{
                            Log.e(TAG, "Error Adding Artwork data", it)
                        }
                }
            }
        }else{
            artwork = Artwork(this.getUniqueId(), title, description, artworkImageUrl, "")
            val artworks = buildMap(1){
                put(artwork.id, artwork)
            }
            firebaseDatabase.child(NODE_USERS).child(firebaseAuth.uid.toString()).child(NODE_USERS_ARTWORKS).updateChildren(artworks)
                .addOnSuccessListener {
                    Log.i(TAG, "Artwork Added successfully")
                    toast("Artwork Added Successfully")
                    this.updateUi()
                }
                .addOnFailureListener{
                    Log.e(TAG, "Error Adding Artwork data", it)
                }
        }
    }

    fun updateArtwork(){
        val title = this.getTextFromViewById(R.id.title)
        val description = this.getTextFromViewById(R.id.description)
        var artworkImageUrl = ""
        if(imageUri != null && imageUri.toString() != null){
            var storageRef = firebaseStorage.getReference((currentuserId)+"_ARTWORK_"+getUniqueId()+".jpg");
            storageRef.putFile(imageUri!!).addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener {
                    artworkImageUrl = it.toString()
                    artwork = Artwork(artwork.id, title, description, artworkImageUrl, "")
                    firebaseDatabase.child(NODE_USERS).child(firebaseAuth.uid.toString()).child(NODE_USERS_ARTWORKS).child(artwork.id).setValue(artwork)
                        .addOnSuccessListener {
                            Log.i(TAG, "Updated successfully")
                            toast("Artwork Updated Successfully")
                            this.updateUi()
                        }
                        .addOnFailureListener{
                            Log.e(TAG, "Error Updating Artwork data", it)
                        }
                }
            }
        }else{
            artworkImageUrl = artwork.artworkImageUrl
            artwork = Artwork(artwork.id, title, description, artworkImageUrl, "")
            firebaseDatabase.child(NODE_USERS).child(firebaseAuth.uid.toString()).child(NODE_USERS_ARTWORKS).child(artwork.id).setValue(artwork)
                .addOnSuccessListener {
                    Log.i(TAG, "Updated successfully")
                    toast("Artwork Updated Successfully")
                    this.updateUi()
                }
                .addOnFailureListener{
                    Log.e(TAG, "Error Updating Artwork data", it)
                }
        }
    }

    fun getArtwork(){
        firebaseDatabase.child(NODE_USERS).child(firebaseAuth.uid.toString()).child(NODE_USERS_ARTWORKS).child(artworkId).get()
            .addOnSuccessListener {
                if(it.value != null){
                    Log.i(TAG, "Got value ${it.value}")
                    var artworkMap = it.getValue() as Map<String, Any>
                    artwork = Artwork(
                        artworkMap.get(ARTWORK_ID).toString(),
                        artworkMap.get(ARTWORK_TITLE).toString(),
                        artworkMap.get(ARTWORK_DESCRIPTION).toString(),
                        artworkMap.get(ARTWORK_IMAGE_URL).toString(),
                        artworkMap.get(ARTWORK_CREATED_DATE).toString())
                    if(artwork != null){
                        this.setTextFromViewById(R.id.title, artwork.title)
                        this.setTextFromViewById(R.id.description, artwork.artDescription)
                        this.setTextFromViewById(R.id.createdDate, artwork.createdDate)
                        if(artwork.artworkImageUrl != null && artwork.artworkImageUrl != "null" && artwork.artworkImageUrl != ""){
                            Picasso.get().load(artwork.artworkImageUrl).into(artworkImage)
                        }
                        else{
                            Picasso.get().load(R.drawable.splash).into(artworkImage)
                        }
                    }
                }
            }
            .addOnFailureListener{
                Log.e(TAG, "Error getting artwork data", it)
            }
    }

    fun deleteArtwork(){
        firebaseDatabase.child(NODE_USERS).child(firebaseAuth.uid.toString()).child(NODE_USERS_ARTWORKS).child(artwork.id).removeValue()
            .addOnSuccessListener {
                Log.i(TAG, "Artwork deleted successfully")
                toast("Artwork deleted Successfully")
                this.updateUi()
            }
            .addOnFailureListener{
                Log.e(TAG, "Error deleting Artwork", it)
            }
    }


}