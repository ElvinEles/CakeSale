package com.madocakesale

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.madocakesale.Model.Product
import com.madocakesale.ViewHolder.ProductViewHolder
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.app_bar_home.*
import java.lang.Exception

class ProductsActivity : AppCompatActivity() {

    //Firebase
    private lateinit var database: FirebaseDatabase
    private lateinit var dataRef: DatabaseReference
    private lateinit var storage: FirebaseStorage
    private lateinit var storageRef: StorageReference
    private lateinit var context: Context

    private lateinit var product_recyclerview: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var adapter: FirebaseRecyclerAdapter<Product, ProductViewHolder>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_products)

        this.supportActionBar!!.setTitle("SİFARİŞLƏR")


        initdb()

        swipeRefreshLayout = findViewById(R.id.swiperefreshlayout)

        product_recyclerview = findViewById(R.id.products_recyclerview)
        product_recyclerview.layoutManager = LinearLayoutManager(this)

        getAllValues()
    }

    private fun getAllValues() {

        var query = FirebaseDatabase.getInstance()
            .reference
            .child("Product").orderByChild("product_arcieve").equalTo("0")


        val options = FirebaseRecyclerOptions.Builder<Product>()
            .setQuery(query, Product::class.java)
            .setLifecycleOwner(this)
            .build()

        adapter = object : FirebaseRecyclerAdapter<Product, ProductViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, position: Int): ProductViewHolder {
                var view: View = LayoutInflater.from(context).inflate(R.layout.layout_product_item, parent, false)
                return ProductViewHolder(view)
            }

            override fun onBindViewHolder(holder: ProductViewHolder, position: Int, model: Product) {

                if (model.Product_received.equals("0")) {
                    holder.itemView.setBackgroundResource(R.color.holder_back)
                }


                holder.product_deliver_time.text = model.Product_receive_time
                holder.product_custom_name_list.text = model.Product_custom_name
                holder.product_kind_list.text = model.Product_kind + "" + model.Product_kind_other
                holder.product_amount_list.text = model.Product_amount


                            Picasso.with(context)
                                .load(model.Product_photo)
                                .into(holder.product_photo)




                holder.showproduct.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(v: View?) {
                        var key: String = adapter.getRef(position).key.toString()

                        showproductbuilder(model, key)
                    }
                })


            }


        }

        product_recyclerview.adapter = adapter
    }

    private fun product_received(model: Product, key: String) {

        model.Product_received = "1"

        var product_received_value: String = ""

        dataRef.child("Product").child(key).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(datasnapshot: DataSnapshot) {
                var db_product = datasnapshot.getValue(Product::class.java)
                product_received_value = db_product!!.Product_received.toString()

                if (product_received_value.equals("1")) {
                    Toast.makeText(context, "Sifarişiniz artıq qəbul olunub", Toast.LENGTH_LONG).show()
                } else {
                    dataRef.child("Product").child(key).setValue(model).addOnSuccessListener {
                        Toast.makeText(context, "Sifarişiniz qəbul olundu", Toast.LENGTH_LONG).show()

                    }.addOnFailureListener {
                        object : OnFailureListener {
                            override fun onFailure(e: Exception) {
                                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                            }
                        }

                    }
                }
            }
        })


    }

    private fun showproductbuilder(model: Product, key: String) {

        var alertDialog: AlertDialog
        var showproduct: AlertDialog.Builder = AlertDialog.Builder(context)
        var view: View = LayoutInflater.from(context).inflate(R.layout.layout_product_show, null, false)


        var product_receive_person: TextView = view.findViewById(R.id.product_receive_person)
        var product_accept_time: TextView = view.findViewById(R.id.product_accept_time)
        var product_receive_time: TextView = view.findViewById(R.id.product_receive_time)
        var product_kind_or_other: TextView = view.findViewById(R.id.product_kind_or_other)
        var product_amount: TextView = view.findViewById(R.id.product_amount)
        var product_description: TextView = view.findViewById(R.id.product_description)
        var product_photo: ImageView = view.findViewById(R.id.product_photo)
        var product_show_accept_button: Button = view.findViewById(R.id.product_show_accept_button)

        var product_show_archieve_button: Button = view.findViewById(R.id.product_show_archieve_button)

        product_receive_person.text = model.Product_receive_person
        product_accept_time.text = model.Product_accept_time
        product_receive_time.text = model.Product_receive_time
        product_kind_or_other.text = model.Product_kind + " " + model.Product_kind_other
        product_amount.text = model.Product_amount
        product_description.text = model.Product_description



        Picasso.with(context).load(model.Product_photo).into(product_photo)
        showproduct.setTitle("SİFARİŞ " + key)

        showproduct.setView(view)
        alertDialog = showproduct.show()


        if (model.Product_received == "1") {
            product_show_accept_button.setText("Qəbul edilib")
            product_show_accept_button.setBackgroundResource(R.drawable.btnorder_accepted)
            product_show_accept_button.isEnabled = false

        } else {
            product_show_accept_button.isEnabled = true
            product_show_accept_button.setOnClickListener {
                product_received(model, key)
                alertDialog.dismiss()

            }
        }





        product_show_archieve_button.setOnClickListener {
            product_archieved(model, key)
            alertDialog.dismiss()

        }


    }

    private fun product_archieved(model: Product, key: String) {

        model.Product_arcieve = "1"



        dataRef.child("Product").child(key).setValue(model).addOnSuccessListener {
            Toast.makeText(context, "Sifarişiniz arxivləndi", Toast.LENGTH_LONG).show()

        }.addOnFailureListener {
            object : OnFailureListener {
                override fun onFailure(e: Exception) {
                    Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                }
            }

        }
    }

    private fun initdb() {
        database = FirebaseDatabase.getInstance()
        dataRef = database.reference
        storage = FirebaseStorage.getInstance()
        storageRef = storage.reference
        context = this@ProductsActivity
    }


}
