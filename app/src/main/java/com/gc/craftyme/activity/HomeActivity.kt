package com.gc.craftyme.activity


import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gc.craftyme.R
import com.gc.craftyme.helpers.DUBaseActivity
import com.gc.craftyme.model.ItemsViewModel
import kotlinx.android.synthetic.main.activity_home.*


class HomeActivity : DUBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Set Toolbar
        setSupportActionBar(toolbar)

        // getting the recyclerview by its id
        val recyclerview = findViewById<RecyclerView>(R.id.recyclerview)

        // this creates a vertical layout Manager
        recyclerview.layoutManager = LinearLayoutManager(this)

    }

    override fun onStart() {
        super.onStart()
        this.getArtworks()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.navbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var itemView = item.itemId
        when(itemView){
            R.id.profile -> {
                this.goToNextActivity(ProfileActivity::class.java)
            }
        }
        return false
    }

    fun btnAddAction(view: View){
        this.goToNextActivity(AddArtworkActivity::class.java)
    }

    //Firebase
    fun getArtworks(){
        firebaseDatabase.child(NODE_USERS).child(firebaseAuth.uid.toString()).child(NODE_USERS_ARTWORKS).get()
            .addOnSuccessListener {
                Log.d(TAG, "Got Artworks ${(it.getValue())}")

                var artworkMap = it.getValue() as Map<String, Any>
                var artworks: ArrayList<ItemsViewModel> = ArrayList()
                var artwork: ItemsViewModel

                for ((k, v) in artworkMap) {
                    var artworkValuesMap = v as Map<String, String>
                    artwork = ItemsViewModel(
                        artworkValuesMap.get(ARTWORK_ID).toString(),
                        artworkValuesMap.get(ARTWORK_TITLE).toString())
                    artworks.add(artwork)
                }

                //Passing data to custom adapter
                val adapter = CustomAdapter(artworks)
                // Setting the Adapter with the recyclerview
                recyclerview.adapter = adapter

            }.addOnFailureListener{
                Log.e(TAG, "Error getting Artworks", it)
            }
    }
}