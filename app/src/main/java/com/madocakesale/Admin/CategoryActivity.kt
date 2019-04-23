package com.madocakesale.Admin

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.madocakesale.Model.Category
import com.madocakesale.R

class CategoryActivity : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase
    private lateinit var dataRef: DatabaseReference
    private lateinit var storage: FirebaseStorage
    private lateinit var storageRef: StorageReference
    private lateinit var context: Context

    private lateinit var category_name: EditText
    private lateinit var category_spinner: Spinner
    private lateinit var savecategory: Button
    private lateinit var deletecategory: Button
    var spinner_category_values: MutableList<String> = ArrayList()
    var spinner_category_keys: MutableList<String> = ArrayList()
    private lateinit var spinner_slider_key:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        initdb()
        initcomponents()
        getSpinnerValues()

        category_spinner.onItemSelectedListener=object :AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(position > 0){
                    spinner_slider_key= spinner_category_keys.get(position)
                }


            }
        }

        savecategory.setOnClickListener {
            savecategorydb()
        }

        deletecategory.setOnClickListener {
            deletecategorydb()
        }
    }

    private fun deletecategorydb() {
        if (category_spinner.selectedItemPosition==0){
            Toast.makeText(context, "Kateqoriya seçmədiniz", Toast.LENGTH_LONG).show()
            return
        }

        dataRef.child("Category").child(spinner_slider_key).removeValue().addOnSuccessListener {
            Toast.makeText(context, "Kateqoriya silindi", Toast.LENGTH_LONG).show()
        }
    }

    private fun getSpinnerValues() {
            spinner_category_values.add("Kateqoriya seçin")
            dataRef.child("Category").addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(datasnaphot: DataSnapshot) {
                    spinner_category_values.clear()
                    spinner_category_keys.clear()

                    for (data in datasnaphot.children) {
                        var category:Category=data.getValue(Category::class.java)!!
                        spinner_category_values.add(category.category_name.toString())
                        spinner_category_keys.add(data.key.toString())
                    }
                }
            })


            var adapter_category_values =
                ArrayAdapter(context, android.R.layout.simple_list_item_1, spinner_category_values)

        category_spinner.adapter = adapter_category_values

    }

    private fun savecategorydb() {

        if (TextUtils.isEmpty(category_name.text)) {
            Toast.makeText(context, "Kateqoriya əlavə etmədiniz...", Toast.LENGTH_LONG).show()
            return
        }

        var query: Query = dataRef.child("Category").orderByKey().limitToLast(1)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(datasnapshot: DataSnapshot) {
                var last_keys = datasnapshot.children
                var last_key_to: Int = 0
                lateinit var db_category:Category
                for (last_key in last_keys) {
                    last_key_to = 1 + last_key.key!!.toInt()
                    db_category= last_key.getValue(Category::class.java)!!
                }

                var category: Category = Category()
                category.category_name = category_name.text.toString().capitalize()

                if (db_category.category_name!!.toLowerCase().equals(category.category_name!!.toLowerCase())){
                    Toast.makeText(context, "Belə kateqoriya mövcuddur", Toast.LENGTH_LONG).show()
                    return
                }else{
                    dataRef.child("Category").child(last_key_to.toString()).setValue(category).addOnSuccessListener {
                        Toast.makeText(context, "Kateqoriya əlavə edildi", Toast.LENGTH_LONG).show()
                    }
                }



            }
        })
    }

    private fun initcomponents() {
        category_spinner = findViewById(R.id.category_spinner)
        category_name = findViewById(R.id.category_name)
        savecategory = findViewById(R.id.savecategory)
        deletecategory = findViewById(R.id.deletecategory)
    }

    private fun initdb() {
        database = FirebaseDatabase.getInstance()
        dataRef = database.reference
        context = this@CategoryActivity
        storage = FirebaseStorage.getInstance()
        storageRef = storage.getReference("slider/")
    }
}
