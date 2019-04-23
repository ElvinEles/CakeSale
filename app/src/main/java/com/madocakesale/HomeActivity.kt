package com.madocakesale

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.madocakesale.Adapter.ViewPageAdapter
import com.madocakesale.Admin.CategoryActivity
import com.madocakesale.Admin.SliderActivity
import com.madocakesale.Model.Slider
import com.madocakesale.Other.BottomExitView
import com.madocakesale.Other.Current
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.app_bar_home.*
import java.util.*

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var context: Context
    private lateinit var viewPager: ViewPager
    private lateinit var adapter: ViewPageAdapter
    private var slider_list: MutableList<Slider> = ArrayList()
    private lateinit var btnOrder: Button
    private lateinit var user_name_nav: TextView
    private lateinit var user_email_nav: TextView

    private lateinit var database: FirebaseDatabase
    private lateinit var dataRef: DatabaseReference
    private lateinit var timer: Timer
    private var current_position: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        toolbar.setTitle("ƏSAS SƏHİFƏ")
        setSupportActionBar(toolbar)

        initdb()

        getSliderValues()

        viewPager = findViewById(R.id.viewpager)
        btnOrder = findViewById(R.id.btnOrder)

        var view: View = nav_view.getHeaderView(0)
        user_name_nav = view.findViewById(R.id.user_name_nav)
        user_email_nav = view.findViewById(R.id.user_email_nav)

        initusersinformation()

        btnOrder.setOnClickListener {
            startActivity(Intent(context, OrderActivity::class.java))
        }



        if (Current.current_user_key == 2) {
            nav_view.menu.setGroupVisible(R.id.admin, true)
        }else{
            nav_view.menu.setGroupVisible(R.id.admin, false)
        }


        nav_view.setNavigationItemSelectedListener(this)
    }

    private fun initusersinformation() {
        user_name_nav.text = Current.current_user?.User_name
        user_email_nav.text = Current.current_user?.User_email
    }

    private fun initdb() {
        database = FirebaseDatabase.getInstance()
        dataRef = database.reference
        context = this@HomeActivity
    }

    private fun getSliderValues() {
        dataRef.child("Slider").addValueEventListener(object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(datasnapshot: DataSnapshot) {
                for (data in datasnapshot.children) {
                    var slider: Slider = data.getValue(Slider::class.java)!!

                    slider_list.add(slider)
                }
                adapter = ViewPageAdapter(slider_list, context)
                viewPager.adapter = adapter
                createSlideShow()
            }
        })
    }

    override fun onBackPressed() {
        var bottomExitView: BottomExitView = BottomExitView()
        bottomExitView.show(supportFragmentManager, "Exit App")

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.home, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.opendrawer -> {
                drawer_layout.openDrawer(GravityCompat.START)

                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_sale -> {

                startActivity(Intent(context, ProductsActivity::class.java))
            }

            R.id.nav_sale_do -> {

                startActivity(Intent(context, OrderActivity::class.java))
            }
            R.id.nav_archieve -> {

                startActivity(Intent(context, ArchieveActivity::class.java))
            }

            R.id.nav_slider -> {

                startActivity(Intent(context, SliderActivity::class.java))
            }

            R.id.nav_category -> {
                startActivity(Intent(context, CategoryActivity::class.java))
            }
            R.id.nav_logout -> {
                var bottomExitView: BottomExitView = BottomExitView()
                bottomExitView.show(supportFragmentManager, "Exit App")
            }

        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun createSlideShow() {
        val handler: Handler = Handler()
        val runnable: Runnable = object : Runnable {
            override fun run() {
                if (current_position == slider_list.size) {
                    current_position = 0

                }

                viewPager.setCurrentItem(current_position++, true)


            }
        }

        timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                handler.post(runnable)
            }
        }, 250, 2500)
    }


}
