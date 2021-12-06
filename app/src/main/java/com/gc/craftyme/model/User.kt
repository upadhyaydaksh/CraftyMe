package com.gc.craftyme.model

import android.app.Activity
import android.widget.Toast
import com.google.gson.Gson

open class User {

    var id = ""
    var firstName = ""
    var lastName = ""
    var email = ""

    constructor(id: String, firstName: String, lastName: String, email: String) {
        this.id = id
        this.firstName = firstName
        this.email = email
        this.lastName = lastName
    }

}