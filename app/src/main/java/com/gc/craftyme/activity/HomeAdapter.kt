package com.gc.craftyme.activity

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gc.craftyme.R
import com.gc.craftyme.model.Artwork
import com.gc.craftyme.utils.Constants
import com.squareup.picasso.Picasso

class HomeAdapter(private val mList: List<Artwork>) : RecyclerView.Adapter<HomeAdapter.ViewHolder>() {

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.home_card_view_design, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val artwork = mList[position]

        // sets the image to the imageview from our itemHolder class
        if(artwork.artworkImageUrl != null && artwork.artworkImageUrl != ""){
            Picasso.get().load(artwork.artworkImageUrl).into(holder.artworkImage)
        }
        else{
            holder.artworkImage.setImageResource(R.drawable.splash)
        }

//        if (position == 0) {
//            holder.artworkImage.setImageResource(R.drawable.ic_img1)
//        } else if (position == 1) {
//            holder.artworkImage.setImageResource(R.drawable.ic_img2)
//        } else {
//            holder.artworkImage.setImageResource(R.drawable.ic_img3)
//        }

        // sets the text to the textview from our itemHolder class
        holder.titleView.text = artwork.title

        holder.createdDateView.text = artwork.createdDate

        holder.descriptionView.text = artwork.artDescription

        holder.itemView.setOnClickListener { view ->
            onItemClick(view, artwork)
        }

    }

    fun onItemClick(view: View, artwork: Artwork){
        val context: Context = view.context
        var intent = Intent(context, AddArtworkActivity::class.java)
        intent.putExtra(Constants.ARTWORK_DETAIL_ID, artwork.id)
        intent.putExtra(Constants.IS_NEW, false)
        context.startActivity(intent)
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val artworkImage: ImageView = itemView.findViewById(R.id.artworkImage)
        val titleView: TextView = itemView.findViewById(R.id.title)
        val descriptionView: TextView = itemView.findViewById(R.id.description)
        val createdDateView: TextView = itemView.findViewById(R.id.createdDate)
    }
}
