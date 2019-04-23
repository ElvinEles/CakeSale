package com.madocakesale.Admin

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.*
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.madocakesale.Adapter.ViewPageAdapter
import com.madocakesale.Adapter.ViewPageAdapterSlider
import com.madocakesale.Model.Slider
import com.madocakesale.OrderActivity
import com.madocakesale.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_slider.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class SliderActivity : AppCompatActivity() {



    private lateinit var viewPager: ViewPager
    private lateinit var adapter: ViewPageAdapterSlider
    private var slider_list: MutableList<Slider> = ArrayList()

    private lateinit var database: FirebaseDatabase
    private lateinit var dataRef: DatabaseReference
    private lateinit var storage: FirebaseStorage
    private lateinit var storageRef: StorageReference
    private lateinit var context: Context

    private lateinit var timer: Timer
    private var current_position: Int = 0
    private lateinit var spinner_slider: Spinner
    private lateinit var saveslider: Button
    private lateinit var deleteslider: Button
    private lateinit var choosePhoto: Button
    private lateinit var slider_photo_get: ImageView
    private lateinit var slider_name: EditText
    private var productphotoUrl: Uri? = null
    var spinner_category_values: MutableList<String> = ArrayList()
    var spinner_category_keys: MutableList<String> = ArrayList()
    private lateinit var spinner_slider_key:String
    companion object {
        private var PICK_IMAGE_REQUEST: Int = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_slider)

        this.supportActionBar!!.setTitle("SLİDER")

        initdb()
        initcomponents()
        viewPager = findViewById(R.id.viewpagerslider)
        saveslider = findViewById(R.id.saveslider)
        deleteslider = findViewById(R.id.deleteslider)
        choosePhoto = findViewById(R.id.choosePhoto)
        slider_photo_get = findViewById(R.id.slider_photo_get)
        slider_name = findViewById(R.id.slider_name)


        getSliderValues()
        getSpinnerValues()

        choosePhoto.setOnClickListener {
            getImageAndSave()
        }

        saveslider.setOnClickListener {
            savesliderDB()
        }

        deleteslider.setOnClickListener {

            deletesliderdb()
        }

        spinner_slider.onItemSelectedListener=object :AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {


                spinner_slider_key= spinner_category_keys.get(position)
            }
        }


    }

    private fun deletesliderdb() {
        if (spinner_slider.selectedItemPosition==0){
            Toast.makeText(context, "Slider seçmədiniz", Toast.LENGTH_LONG).show()
            return
        }
        dataRef.child("Slider").child(spinner_slider_key).removeValue().addOnSuccessListener {
            Toast.makeText(context, "Slider silindi", Toast.LENGTH_LONG).show()
        }
    }


    private fun savesliderDB() {
        var random = Random().nextInt()
        var slider: Slider = Slider()

        if (productphotoUrl != null) {
            var imageName: String = random.toString() + "." + getFileExtension(productphotoUrl!!)
            slider.Slider_name = slider_name.text.toString()
            var fileRef: StorageReference = storageRef.child(imageName)

            var uploadTask: UploadTask = fileRef.putFile(productphotoUrl!!)

            var task: Task<Uri> =
                uploadTask.continueWithTask(object : Continuation<UploadTask.TaskSnapshot, Task<Uri>> {

                    override fun then(tasksnapshot: Task<UploadTask.TaskSnapshot>): Task<Uri> {
                        if (!tasksnapshot.isSuccessful) {
                            Toast.makeText(context, "Failed", Toast.LENGTH_LONG).show()
                        }

                        return fileRef.downloadUrl
                    }
                }).addOnCompleteListener(object : OnCompleteListener<Uri> {
                    override fun onComplete(task: Task<Uri>) {
                        if (task.isSuccessful) {
                            var url: String = task.result.toString()

                            slider.Slider_image = url

                            dataRef.child("Slider").push().setValue(slider)
                            Toast.makeText(context, "Slider qeyd edildi", Toast.LENGTH_LONG).show()
                        }
                    }
                })


        } else {
            Toast.makeText(context, "Zəhmət olmasa şəkili seçin..", Toast.LENGTH_LONG).show()
            return

        }
    }

    private fun initcomponents() {
        spinner_slider = findViewById(R.id.slider_spinner)
    }

    private fun getSpinnerValues() {

        spinner_category_values.add("Slideri seçin")
        spinner_category_keys.add("indexi seçin")


        dataRef.child("Slider").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(datasnaphot: DataSnapshot) {

                spinner_category_values.clear()
                spinner_category_keys.clear()



                for (data in datasnaphot.children) {
                    var slider: Slider = data.getValue(Slider::class.java)!!



                    spinner_category_values.add(slider.Slider_name.toString())
                    spinner_category_keys.add(data.key.toString())
                }
            }
        })

        var adapter_category_values =
            ArrayAdapter(context, android.R.layout.simple_list_item_1, spinner_category_values)

        spinner_slider.adapter = adapter_category_values

    }


    private fun initdb() {
        database = FirebaseDatabase.getInstance()
        dataRef = database.reference
        context = this@SliderActivity
        storage = FirebaseStorage.getInstance()
        storageRef = storage.getReference("slider/")
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

    private fun getSliderValues() {
        dataRef.child("Slider").addValueEventListener(object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(datasnapshot: DataSnapshot) {
                for (data in datasnapshot.children) {
                    var slider: Slider = data.getValue(Slider::class.java)!!

                    slider_list.add(slider)
                }
                adapter = ViewPageAdapterSlider(slider_list, context)
                viewPager.adapter = adapter
                createSlideShow()
            }
        })
    }

    private fun getImageAndSave() {
        var imageChoose = Intent()
        imageChoose.setType("image/*")
        imageChoose.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(imageChoose, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
            && data != null && data.data != null
        ) {
            productphotoUrl = data.data




            Picasso.with(context).load(productphotoUrl).into(slider_photo_get)
        }
    }


    private fun getFileExtension(uri: Uri): String {
        var cr = contentResolver
        var mimetype = MimeTypeMap.getSingleton()
        return mimetype.getExtensionFromMimeType(cr.getType(uri))
    }


}
