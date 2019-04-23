package com.madocakesale

import android.app.*
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.text.TextUtils
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
import com.madocakesale.Model.Category
import java.text.SimpleDateFormat
import java.util.*
import com.madocakesale.Model.Product
import com.squareup.picasso.Picasso
import java.lang.StringBuilder
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class OrderActivity : AppCompatActivity() {

    //Firebase
    private lateinit var database: FirebaseDatabase
    private lateinit var dataRef: DatabaseReference
    private lateinit var storage: FirebaseStorage
    private lateinit var storageRef: StorageReference
    private lateinit var context: Context

    private lateinit var product_id: EditText
    private lateinit var product_receive_person: EditText
    private lateinit var product_accept_time: EditText
    private lateinit var product_accept_time_2: EditText
    private lateinit var product_receive_time: EditText
    private lateinit var product_custom_name: EditText
    private lateinit var product_custom_phone: EditText
    private lateinit var product_kind: Spinner
    private lateinit var product_cake_pors: EditText
    private lateinit var product_kind_other: EditText
    private lateinit var product_amount: EditText
    private lateinit var product_size: Spinner
    private lateinit var product_photo_get: ImageView
    private lateinit var product_description: EditText
    private lateinit var progressbar: ProgressBar
    private lateinit var saveproduct: Button
    private lateinit var denyproduct: Button
    private lateinit var choosePhoto: Button
    private lateinit var onDateSetListener: DatePickerDialog.OnDateSetListener
    private var productphotoUrl: Uri? = null
    private lateinit var notificationManager: NotificationManagerCompat


    companion object {
        private var PICK_IMAGE_REQUEST: Int = 1
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)
        this.supportActionBar!!.setTitle("SİFARİŞLƏR")
        initdb()
        initcomponents()
        initvalues()

        notificationManager = NotificationManagerCompat.from(this)

        product_receive_time.setOnClickListener {
            opendatepicker()
        }

        choosePhoto.setOnClickListener {
            getImageAndSave()
        }



        denyproduct.setOnClickListener {
            startActivity(Intent(context, HomeActivity::class.java))
        }

        saveproduct.setOnClickListener {

            progressbar.visibility = View.VISIBLE

               saveproducttoDB()
        }
    }

    private fun getImageAndSave() {
        var imageChoose = Intent()
        imageChoose.setType("image/*")
        imageChoose.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(imageChoose, PICK_IMAGE_REQUEST)
    }


    @RequiresApi(Build.VERSION_CODES.N)
    fun clickTimePicker(view: View) {
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR)
        val minute = c.get(Calendar.MINUTE)

        val tpd = TimePickerDialog(this, TimePickerDialog.OnTimeSetListener(function = { view, h, m ->


            product_accept_time_2.setText(
                StringBuilder(if (h < 10) "0" else "").append(h.toString()).append(":").append(
                    if (m < 10) "0" else ""
                ).append(m.toString())
            )

        }), hour, minute, true)



        tpd.show()
    }


    private fun saveproducttoDB() {

        if (TextUtils.isEmpty(product_receive_person.text)) {
            Toast.makeText(context, "Sifarişi qəbul edən yazılmalıdır...", Toast.LENGTH_LONG).show()
            progressbar.visibility = View.GONE
            return
        }

        if (TextUtils.isEmpty(product_receive_time.text)) {
            Toast.makeText(context, "Təslim vaxtı yazılmalıdır...", Toast.LENGTH_LONG).show()
            progressbar.visibility = View.GONE
            return
        }


        if (TextUtils.isEmpty(product_custom_name.text)) {
            Toast.makeText(context, "Müştəri adı yazılmalıdır...", Toast.LENGTH_LONG).show()
            progressbar.visibility = View.GONE
            return
        }


        if (TextUtils.isEmpty(product_amount.text)) {
            Toast.makeText(context, "Zəhmət olmasa məhsul qiymətini seçin yazın...", Toast.LENGTH_LONG).show()
            progressbar.visibility = View.GONE
            return
        }

        if (product_size.selectedItemPosition == 0) {
            Toast.makeText(context, "Miqdar yazılmalıdır...", Toast.LENGTH_LONG).show()
            progressbar.visibility = View.GONE
            return
        }

        var product_kind_value: String = product_kind.selectedItem.toString()

        if (product_kind.selectedItemPosition == 0) {
            product_kind_value = ""
        }


        var current_product: Product = Product()
        current_product.Product_receive_person = (product_receive_person.text.toString()).capitalize()
        current_product.Product_accept_time = product_accept_time.text.toString()
        current_product.Product_receive_time =
            product_receive_time.text.toString() + " " + product_accept_time_2.text.toString()
        current_product.Product_custom_name = (product_custom_name.text.toString()).capitalize()
        current_product.Product_custom_phone = product_custom_phone.text.toString()
        current_product.Product_kind = product_kind_value
        current_product.Product_cake_pors = product_cake_pors.text.toString() + "Pors"
        current_product.Product_kind_other = product_kind_other.text.toString()
        current_product.Product_amount = product_amount.text.toString() + " " + product_size.selectedItem.toString()
        current_product.Product_description = product_description.text.toString()

        if (productphotoUrl != null) {
            var imageName: String = product_id.text.toString() + "." + getFileExtension(productphotoUrl!!)
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

                            current_product.Product_photo = url

                            Log.d("LINKLINKLINK", url)

                            dataRef.child("Product").child(product_id.text.toString()).setValue(current_product)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        progressbar.visibility = View.GONE
                                        Toast.makeText(context, "Sifarişiniz qeyd edildi...", Toast.LENGTH_LONG).show()

                                        sendNotification()
                                    } else {
                                        progressbar.visibility = View.GONE
                                        Toast.makeText(context, "Sifarişiniz qeyd alınmadı...", Toast.LENGTH_LONG)
                                            .show()
                                    }
                                }
                        }
                    }
                })


        } else {
            dataRef.child("Product").child(product_id.text.toString()).setValue(current_product)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        progressbar.visibility = View.GONE
                        Toast.makeText(context, "Sifarişiniz qeyd edildi...", Toast.LENGTH_LONG).show()
                        sendNotification()

                    } else {
                        progressbar.visibility = View.GONE
                        Toast.makeText(context, "Sifarişiniz qeyd alınmadı...", Toast.LENGTH_LONG).show()
                    }
                }
        }


    }

    private fun sendNotification() {

        var intent:Intent=Intent(this,ProductsActivity::class.java)
        var pendingresult:PendingIntent= PendingIntent.getActivity(this,1,intent,PendingIntent.FLAG_UPDATE_CURRENT)

        var notification: Notification =
            NotificationCompat.Builder(context, com.madocakesale.Other.Notification.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_message)
                .setContentTitle("Yeni Sifariş")
                .setContentText("Yeni sifariş alındı")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setAutoCancel(true)
                .setContentIntent(pendingresult)
                .build()

        notificationManager.notify(1, notification)

    }


    private fun initvalues() {
        var datetimformat: SimpleDateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm")
        var current_time: Date = Date()
        product_accept_time.setText(datetimformat.format(current_time))

        onDateSetListener = DatePickerDialog.OnDateSetListener { datePicker, i, i1, i2 ->


            product_receive_time.setText(
                StringBuilder(if (i2 < 10) "0" else "").append(i2.toString()).append(if (i1 < 10) ".0" else ".").append(
                    i1 + 1
                ).append(
                    "."
                ).append(i.toString())
            )

        }

        var randomid = Random().nextInt(100000000)
        product_id.setText(randomid.toString())

        initsizevalues()
        initcategoryvalues()
    }

    private fun initsizevalues() {
        var spinner_size_values: MutableList<String> = ArrayList()
        spinner_size_values.add("Ölçü Vahidi")
        spinner_size_values.add("KG")
        spinner_size_values.add("ƏDƏD")

        var adapter_size_values = ArrayAdapter(context, android.R.layout.simple_list_item_1, spinner_size_values)

        product_size.adapter = adapter_size_values
    }

    private fun initcategoryvalues() {
        var spinner_category_values: MutableList<String> = ArrayList()

        spinner_category_values.add("Kateqoriya seçin")
        dataRef.child("Category").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(datasnaphot: DataSnapshot) {

                for (data in datasnaphot.children) {
                    var category: Category = data.getValue(Category::class.java)!!
                    spinner_category_values.add(category.category_name.toString())
                }
            }
        })


        var adapter_category_values =
            ArrayAdapter(context, android.R.layout.simple_list_item_1, spinner_category_values)

        product_kind.adapter = adapter_category_values
    }

    private fun initcomponents() {
        product_id = findViewById(R.id.product_id)
        product_receive_person = findViewById(R.id.product_receive_person)
        product_accept_time = findViewById(R.id.product_accept_time)
        product_receive_time = findViewById(R.id.product_receive_time)
        product_custom_name = findViewById(R.id.product_custom_name)
        product_custom_phone = findViewById(R.id.product_custom_phone)
        product_kind = findViewById(R.id.product_kind)
        product_cake_pors = findViewById(R.id.product_cake_pors)
        product_kind_other = findViewById(R.id.product_kind_other)
        product_amount = findViewById(R.id.product_amount)
        product_size = findViewById(R.id.product_size)
        product_photo_get = findViewById(R.id.product_photo_get)
        product_description = findViewById(R.id.product_description)
        saveproduct = findViewById(R.id.saveproduct)
        denyproduct = findViewById(R.id.denyproduct)
        choosePhoto = findViewById(R.id.choosePhoto)
        product_accept_time_2 = findViewById(R.id.product_receive_time_2)
        progressbar = findViewById(R.id.progressbar)


    }


    private fun initdb() {
        database = FirebaseDatabase.getInstance()
        dataRef = database.reference
        storage = FirebaseStorage.getInstance()
        storageRef = storage.getReference("uploads/")
        context = this@OrderActivity
    }

    private fun opendatepicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val dialog = DatePickerDialog(context, onDateSetListener, year, month, day)
        dialog.show()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
            && data != null && data.data != null
        ) {
            productphotoUrl = data.data




            Picasso.with(context).load(productphotoUrl).into(product_photo_get)
        }
    }


    private fun getFileExtension(uri: Uri): String {
        var cr = contentResolver
        var mimetype = MimeTypeMap.getSingleton()
        return mimetype.getExtensionFromMimeType(cr.getType(uri))
    }

    override fun onBackPressed() {

    }

}







































