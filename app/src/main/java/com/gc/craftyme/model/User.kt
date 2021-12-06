package com.gc.craftyme.model

open class User {

    var id = ""
    var firstName = ""
    var lastName = ""
    var email = ""

//    constructor()

    constructor(id: String, firstName: String, lastName: String, email: String) {
        this.id = id
        this.firstName = firstName
        this.email = email
        this.lastName = lastName
    }

}