package com.gc.craftyme.activity

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import com.gc.craftyme.R
import com.gc.craftyme.helpers.DUBaseActivity
import com.gc.craftyme.helpers.Extensions.toast
import com.gc.craftyme.model.User

class ProfileActivity : DUBaseActivity() {

    lateinit var user: User
    private val ID = "id"
    private val FIRST_NAME = "firstName"
    private val LAST_NAME = "lastName"
    private val EMAIL = "email"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        this.getProfile()
    }

    fun btnUpdateAction(view: View){
        this.updateProfile()
    }

    fun btnLogoutAction(view: View){
        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle(R.string.logout)
        //set message for alert dialog
        builder.setMessage(R.string.Are_you_sure_logout)
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton("Yes"){dialogInterface, which ->
            this.signOut()
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

    //Firebase
    private fun getProfile() {
        firebaseDatabase.child(NODE_USERS).child(firebaseAuth.uid.toString()).get()
            .addOnSuccessListener {
                Log.i(TAG, "Got value ${it.value}")
                var userMap = it.getValue() as Map<String, Any>
                user = User(
                    userMap.get(ID) as String,
                    userMap.get(FIRST_NAME) as String,
                    userMap.get(LAST_NAME) as String,
                    userMap.get(EMAIL) as String
                )
                if(user != null){
                    this.setTextFromViewById(R.id.firstName, user.firstName)
                    this.setTextFromViewById(R.id.lastName, user.lastName)
                    this.setTextFromViewById(R.id.email, user.email)
                }
            }.addOnFailureListener{
                Log.e(TAG, "Error getting User data", it)
            }
    }

    private fun updateProfile() {
        user.firstName = this.getTextFromViewById(R.id.firstName)
        user.lastName = this.getTextFromViewById(R.id.lastName)
        val userValues = buildMap(2){
            put(FIRST_NAME, user.firstName)
            put(LAST_NAME, user.lastName)
        }
        firebaseDatabase.child(NODE_USERS).child(user.id).updateChildren(userValues)
            .addOnSuccessListener {
                Log.i(TAG, "User Updated")
                toast("User profile updated")
            }
            .addOnFailureListener{
                Log.e(TAG, "Error updating User data", it)
            }
    }
}
