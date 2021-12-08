package com.gc.craftyme.model

open class Artwork {

    var title = ""
    var id = ""
    var createdDate = ""
    var artDescription = ""
    var artworkImageUrl = ""

    constructor(id: String, title: String, artDescription: String, artworkImageUrl: String, createdDate: String){
        this.id = id
        this.title = title
        this.artDescription = artDescription
        this.artworkImageUrl = artworkImageUrl
        this.createdDate = createdDate
    }
}
