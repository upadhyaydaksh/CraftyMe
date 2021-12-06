package com.gc.craftyme.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import com.gc.craftyme.R
import com.gc.craftyme.helpers.DUBaseActivity
import com.gc.craftyme.helpers.Extensions.toast
import com.gc.craftyme.model.ItemsViewModel

class AddArtworkActivity : DUBaseActivity() {

    lateinit var artwork: ItemsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_artwork)
    }

    fun btnSaveAction(view: View){
        this.addArtwork()
    }

    fun updateUi(){
        this.goToNextActivity(HomeActivity::class.java)
    }

    //Firebase
    fun addArtwork(){
        val title = this.getTextFromViewById(R.id.title)
        artwork = ItemsViewModel(this.getUniqueId(), title)
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
        artwork = ItemsViewModel(artwork.id, title)
        val artworks = buildMap(1){
            put(firebaseDatabase.key.toString(), artwork)
        }
        firebaseDatabase.child(NODE_USERS).child(firebaseAuth.uid.toString()).child(NODE_USERS_ARTWORKS).updateChildren(artworks)
            .addOnSuccessListener {
                Log.i(TAG, "Artwork Added or Updated successfully")
                toast("Artwork Updated Successfully")
                this.updateUi()
            }
            .addOnFailureListener{
                Log.e(TAG, "Error Adding or Updating Artwork data", it)
            }
    }


}