package com.madocakesale.Model



class Product {


    var Product_receive_person: String? = null
    var Product_accept_time: String? = null
    var Product_receive_time: String? = null
    var Product_custom_name: String? = null
    var Product_custom_phone: String? = "NO"
    var Product_kind: String? = "NO"
    var Product_cake_pors: String? = "NO"
    var Product_kind_other: String? = "NO"
    var Product_amount: String? = null
    var Product_photo: String? = "NO"
    var Product_description: String? = "NO"
    var Product_received:String?="0"
    var Product_arcieve:String?="0"

    constructor()
    constructor(
        Product_receive_person: String?,
        Product_accept_time: String?,
        Product_receive_time: String?,
        Product_custom_name: String?,
        Product_custom_phone: String?,
        Product_kind: String?,
        Product_cake_pors: String?,
        Product_kind_other: String?,
        Product_amount: String?,
        Product_photo: String?,
        Product_description: String?,
        Product_received: String?,
        Product_arcieve: String?
    ) {
        this.Product_receive_person = Product_receive_person
        this.Product_accept_time = Product_accept_time
        this.Product_receive_time = Product_receive_time
        this.Product_custom_name = Product_custom_name
        this.Product_custom_phone = Product_custom_phone
        this.Product_kind = Product_kind
        this.Product_cake_pors = Product_cake_pors
        this.Product_kind_other = Product_kind_other
        this.Product_amount = Product_amount
        this.Product_photo = Product_photo
        this.Product_description = Product_description
        this.Product_received = Product_received
        this.Product_arcieve = Product_arcieve
    }


}

