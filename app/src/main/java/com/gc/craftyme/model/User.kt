package com.gc.craftyme.model

open class User {

    var id = ""
    var firstName = ""
    var lastName = ""
    var email = ""
    var profilePicture = ""

    constructor(id: String, firstName: String, lastName: String, email: String, profilePicture: String) {
        this.id = id
        this.firstName = firstName
        this.lastName = lastName
        this.email = email
        this.profilePicture = profilePicture
    }

}