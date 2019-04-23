package com.madocakesale.Model

class User {

    var User_name:String? = null
    var User_email:String? = null
    var User_password:String? = null



    constructor()


    constructor(User_name: String?, User_email: String?, User_password: String?) {
        this.User_name = User_name
        this.User_email = User_email
        this.User_password = User_password
    }


}