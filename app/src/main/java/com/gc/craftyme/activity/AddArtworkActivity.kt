package com.gc.craftyme.activity

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import com.gc.craftyme.R
import com.gc.craftyme.helpers.DUBaseActivity
import com.gc.craftyme.helpers.Extensions.toast
import com.gc.craftyme.model.Artwork
import com.gc.craftyme.utils.Constants

class AddArtworkActivity : DUBaseActivity() {

    lateinit var artwork: Artwork
    var artworkId = ""
    var isNew = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_artwork)
    }

    override fun onStart() {
        super.onStart()
        artworkId = intent.getStringExtra(Constants.ARTWORK_DETAIL_ID).toString()
        isNew = intent.getBooleanExtra(Constants.IS_NEW, true)
        if(!isNew){
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

    //Firebase
    fun addArtwork(){
        val title = this.getTextFromViewById(R.id.title)
        artwork = Artwork(this.getUniqueId(), title)
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

    fun updateArtwork(){
        val title = this.getTextFromViewById(R.id.title)
        artwork = Artwork(artwork.id, title)
        firebaseDatabase.child(NODE_USERS).child(firebaseAuth.uid.toString()).child(NODE_USERS_ARTWORKS).child(artwork.id).setValue(artwork)
            .addOnSuccessListener {
                Log.i(TAG, "Artwork Added or Updated successfully")
                toast("Artwork Updated Successfully")
                this.updateUi()
            }
            .addOnFailureListener{
                Log.e(TAG, "Error Updating Artwork data", it)
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
                        artworkMap.get(ARTWORK_TITLE).toString())
                    if(artwork != null){
                        this.setTextFromViewById(R.id.title, artwork.title)
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