package com.madocakesale.ViewHolder

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.madocakesale.R


class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var product_deliver_time = itemView.findViewById<TextView>(R.id.product_deliver_time)
    var product_custom_name_list = itemView.findViewById<TextView>(R.id.product_custom_name_list)
    var product_kind_list = itemView.findViewById<TextView>(R.id.product_kind_list)
    var product_amount_list = itemView.findViewById<TextView>(R.id.product_amount_list)
    var product_photo = itemView.findViewById<ImageView>(R.id.product_image_list)
    var showproduct = itemView.findViewById<ImageView>(R.id.showproduct)


}