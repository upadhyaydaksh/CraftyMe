package com.gc.craftyme.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import com.gc.craftyme.R
import com.gc.craftyme.helpers.DUBaseActivity
import com.gc.craftyme.helpers.Extensions.toast
import com.gc.craftyme.model.ItemsViewModel
import com.google.gson.Gson

class AddArtworkActivity : DUBaseActivity() {

    lateinit var artwork: ItemsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_artwork)
    }

    fun btnSaveAction(view: View){
        this.addArtwork()
    }

    fun btnDeleteAction(view: View){
        this.deleteArtwork()
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
                Log.e(TAG, "Error Updating Artwork data", it)
            }
    }

    fun getArtwork(){
        firebaseDatabase.child(NODE_USERS).child(firebaseAuth.uid.toString()).child(NODE_USERS_ARTWORKS).child(artwork.id).get()
            .addOnSuccessListener {
                Log.i(TAG, "Got value ${it.value}")
                artwork = Gson().fromJson(it.value.toString(), ItemsViewModel::class.java)
                if(artwork != null){
                    this.setTextFromViewById(R.id.title, artwork.title)
                }
            }.addOnFailureListener{
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