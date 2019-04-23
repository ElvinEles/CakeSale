package com.madocakesale

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.madocakesale.Model.User
import com.madocakesale.Other.Current
import io.paperdb.Paper

class MainActivity : AppCompatActivity() {

    private lateinit var user_name:EditText
    private lateinit var user_password:EditText
    private lateinit var remmember_me:CheckBox
    private lateinit var checkUser:Button
    private lateinit var exitprogram:Button

    //Firebase
    private lateinit var database: FirebaseDatabase
    private lateinit var dataRef: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        initdb()
        initcomponents()



        checkUser.setOnClickListener {
            checkUserdb()
        }

        exitprogram.setOnClickListener {
            finishAffinity()
        }
    }

    private fun avtoenter() {
        var user_name:String? = null
        var user_password:String? = null
        if (Paper.book().read("USER_NAME",null) != null &&  Paper.book().read("USER_PASSWORD",null)!= null ){
             user_name=Paper.book().read("USER_NAME")
             user_password=Paper.book().read("USER_PASSWORD")
            Log.d("USER_NAME",user_name)
            dataRef.child("Users").orderByChild("user_name").equalTo(user_name).addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(datasnapshot: DataSnapshot) {
                    var current_user=User()
                    for (data in datasnapshot.children){
                        current_user =data.getValue(User::class.java)!!
                        Current.current_user_key=data.key!!.toInt()
                    }

                    if (current_user.User_password.equals(user_password)){
                        Current.current_user=current_user



                        startActivity(Intent(this@MainActivity,HomeActivity::class.java))
                        finish()
                    }else{
                        Toast.makeText(this@MainActivity, "Yalnış user", Toast.LENGTH_LONG).show()
                    }
                }
            })
        }
    }

    private fun checkUserdb() {
        var user_name:String=user_name.text.toString()
        var user_password:String=user_password.text.toString()

        dataRef.child("Users").orderByChild("user_name").equalTo(user_name).addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(datasnapshot: DataSnapshot) {
                var current_user=User()
                for (data in datasnapshot.children){
                    current_user =data.getValue(User::class.java)!!
                    Current.current_user_key=data.key!!.toInt()
                }

                if (current_user.User_password.equals(user_password)){
                    Current.current_user=current_user

                    if (remmember_me.isChecked){
                        Paper.book().write("USER_NAME",current_user.User_name.toString())
                        Paper.book().write("USER_PASSWORD",current_user.User_password.toString())
                    }

                    startActivity(Intent(this@MainActivity,HomeActivity::class.java))
                    finish()
                }else{
                    Toast.makeText(this@MainActivity, "Yalnış user", Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun initcomponents() {
        user_name = findViewById(R.id.user_name)
        user_password = findViewById(R.id.user_password)
        remmember_me = findViewById(R.id.remmember_me)
        checkUser = findViewById(R.id.checkUser)
        exitprogram = findViewById(R.id.exitprogram)

    }


    private fun initdb() {
        database = FirebaseDatabase.getInstance()
        dataRef = database.reference
        Paper.init(this)
        avtoenter()

    }
}
