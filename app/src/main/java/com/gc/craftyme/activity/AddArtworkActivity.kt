package com.gc.craftyme.activity

import android.Manifest
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.lifecycleScope
import com.gc.craftyme.R
import com.gc.craftyme.helpers.DUBaseActivity
import com.gc.craftyme.helpers.Extensions.toast
import com.gc.craftyme.model.Artwork
import com.gc.craftyme.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import com.swein.easypermissionmanager.EasyPermissionManager
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*


class AddArtworkActivity : DUBaseActivity() {

    lateinit var artwork: Artwork
    var artworkId = ""
    var isNew = true

    // Image picker variables
    private var imageUri: Uri? = null
    private var currentuserId = ""

//    private val binding: ActivityMainBinding? = null
    private val CHANNEL_ID = "MY_CHANNEL"
    private var notificationId = 1

    private val artworkImage by lazy { findViewById<ImageView>(R.id.artworkImage) }

    private val easyPermissionManager = EasyPermissionManager(this)

    var datePickerDialog: DatePickerDialog? = null

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
        createNotificationChannel()
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
        findViewById<EditText>(R.id.createdDate).setOnClickListener{
            val c: Calendar = Calendar.getInstance()
            val mYear: Int = c.get(Calendar.YEAR)

            val mMonth: Int = c.get(Calendar.MONTH)

            val mDay: Int = c.get(Calendar.DAY_OF_MONTH)

            datePickerDialog = DatePickerDialog(this,
                { view, year, monthOfYear, dayOfMonth ->
                    val cal = Calendar.getInstance()
                    cal.set(year, monthOfYear, dayOfMonth)

                    findViewById<EditText>(R.id.createdDate).setText(

                        DateTimeFormatter.ofPattern("MMM dd, yyyy")
                            .withZone(ZoneId.of("UTC"))
                            .format(cal.toInstant())
                    )
                }, mYear, mMonth, mDay
            )
            datePickerDialog!!.show()
        }

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

    private fun fireNotification(){

        val builder: NotificationCompat.Builder = NotificationCompat.Builder(
            this,CHANNEL_ID
        )
            .setSmallIcon(R.drawable.splash)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        if(isNew){
            builder.setContentTitle("Artwork Added Successfully")
                .setContentText("Your Artwork Was Added Successfully")
        }else{
            builder.setContentTitle("Artwork Updated Successfully")
                .setContentText("Your Artwork Was Updated Successfully")
        }

        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(notificationId++, builder.build())
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "My Channel"
            val description = "Channel Description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(
                CHANNEL_ID,
                name,
                importance
            )
            channel.description = description
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    //Firebase
    fun addArtwork(){
        val title = this.getTextFromViewById(R.id.title)
        val description = this.getTextFromViewById(R.id.description)
        val createdDate = this.getTextFromViewById(R.id.createdDate)
        var artworkImageUrl = ""
        if(imageUri != null && imageUri.toString() != null){
            var storageRef = firebaseStorage.getReference((currentuserId)+"_ARTWORK_"+getUniqueId()+".jpg");
            storageRef.putFile(imageUri!!).addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener {
                    artworkImageUrl = it.toString()
                    artwork = Artwork(this.getUniqueId(), title, description, artworkImageUrl, createdDate)
                    val artworks = buildMap(1){
                        put(artwork.id, artwork)
                    }
                    firebaseDatabase.child(NODE_USERS).child(firebaseAuth.uid.toString()).child(NODE_USERS_ARTWORKS).updateChildren(artworks)
                        .addOnSuccessListener {
                            Log.i(TAG, "Artwork Added successfully")
                            toast("Artwork Added Successfully")
                            fireNotification()
                            this.updateUi()
                        }
                        .addOnFailureListener{
                            Log.e(TAG, "Error Adding Artwork data", it)
                        }
                }
            }
        }else{
            artwork = Artwork(this.getUniqueId(), title, description, artworkImageUrl, createdDate)
            val artworks = buildMap(1){
                put(artwork.id, artwork)
            }
            firebaseDatabase.child(NODE_USERS).child(firebaseAuth.uid.toString()).child(NODE_USERS_ARTWORKS).updateChildren(artworks)
                .addOnSuccessListener {
                    Log.i(TAG, "Artwork Added successfully")
                    toast("Artwork Added Successfully")
                    fireNotification()
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
        val createdDate = this.getTextFromViewById(R.id.createdDate)
        var artworkImageUrl = ""
        if(imageUri != null && imageUri.toString() != null){
            var storageRef = firebaseStorage.getReference((currentuserId)+"_ARTWORK_"+getUniqueId()+".jpg");
            storageRef.putFile(imageUri!!).addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener {
                    artworkImageUrl = it.toString()
                    artwork = Artwork(artwork.id, title, description, artworkImageUrl, createdDate)
                    firebaseDatabase.child(NODE_USERS).child(firebaseAuth.uid.toString()).child(NODE_USERS_ARTWORKS).child(artwork.id).setValue(artwork)
                        .addOnSuccessListener {
                            Log.i(TAG, "Updated successfully")
                            toast("Artwork Updated Successfully")
                            fireNotification()
                            this.updateUi()
                        }
                        .addOnFailureListener{
                            Log.e(TAG, "Error Updating Artwork data", it)
                        }
                }
            }
        }else{
            artworkImageUrl = artwork.artworkImageUrl
            artwork = Artwork(artwork.id, title, description, artworkImageUrl, createdDate)
            firebaseDatabase.child(NODE_USERS).child(firebaseAuth.uid.toString()).child(NODE_USERS_ARTWORKS).child(artwork.id).setValue(artwork)
                .addOnSuccessListener {
                    Log.i(TAG, "Updated successfully")
                    toast("Artwork Updated Successfully")
                    fireNotification()
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