package com.example.noey.twitterapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Toast
import com.example.noey.twitterapp.model.PostInfo
import com.example.noey.twitterapp.model.Ticket
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_ticket.view.*
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    val REQUEST_CODE_PICK_IMG = 222

    var listTweets = ArrayList<Ticket>()
    var email:String?=null
    var uid:String?=null
    var downloadUrl:String?=""

    private var firebaseDatabase = FirebaseDatabase.getInstance()
    private var firebaseDbRef= firebaseDatabase.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var bundle:Bundle = intent.extras
        email = bundle.getString("email")
        uid = bundle.getString("uid")

        //dummy data
        listTweets.add(Ticket("0", "him", "url", "add"))
        listTweets.add(Ticket("0", "him", "url", "uid"))
        listTweets.add(Ticket("1", "her", "url", "uid"))

        var adapter = MyTweetAdpater(this, listTweets)
        listviewTweets.adapter = adapter
    }

    private fun loadImage() {
        var intent = Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_CODE_PICK_IMG)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == REQUEST_CODE_PICK_IMG && data != null && resultCode == Activity.RESULT_OK){
            val selectedImage = data.data
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = contentResolver.query(selectedImage, filePathColumn, null, null, null)
            cursor.moveToFirst()
            val columnIndex = cursor.getColumnIndex(filePathColumn[0])
            val imagePath = cursor.getString(columnIndex)
            cursor.close()
            uploadImage(BitmapFactory.decodeFile(imagePath))

        }
    }

    fun uploadImage(bitmap: Bitmap) {
        var storage = FirebaseStorage.getInstance()
        val storageRef = storage.getReferenceFromUrl("gs://twitterapp-868ea.appspot.com")
        val dateFormat = SimpleDateFormat("ddMMyyHHmmss")
        val dataObj = Date()
        val imagePath = splitString(email!!) + dateFormat.format(dataObj) + ".jpg"
        val imageRef = storageRef.child("imagesPost/"+imagePath)

        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG,100, byteArrayOutputStream)
        val data = byteArrayOutputStream.toByteArray()
        val uploadTask = imageRef.putBytes(data)

        uploadTask.addOnFailureListener {
            Toast.makeText(applicationContext,"Fail to upload image to storage",Toast.LENGTH_LONG).show()

        }.addOnSuccessListener { taskSnapshot ->
            Toast.makeText(applicationContext,"Upload image to storage successfully",Toast.LENGTH_LONG).show()

            downloadUrl = taskSnapshot.downloadUrl.toString()


        }
    }

    fun splitString(email: String):String{
        val split = email.split("@")
        return split[0]
    }

    inner class  MyTweetAdpater: BaseAdapter {
        var listNotesAdpater=ArrayList<Ticket>()
        var context: Context?=null
        constructor(context:Context, listNotesAdpater:ArrayList<Ticket>):super(){
            this.listNotesAdpater=listNotesAdpater
            this.context=context
        }

        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {

            var mytweet = listNotesAdpater[p0]

            if(mytweet.tweetPersonUID.equals("add")) {
                var myView = layoutInflater.inflate(R.layout.add_ticket, null)

                myView.image_attach.setOnClickListener {
                    loadImage()
                }

                myView.image_post.setOnClickListener{
                    //upload to server
                    firebaseDbRef.child("posts").push().setValue(
                            PostInfo(uid!!, myView.edt_post.text.toString(), downloadUrl!!))
                }
                return myView

            } else if(mytweet.tweetPersonUID.equals("loading")){
                var myView=layoutInflater.inflate(R.layout.loading_ticket,null)
                return myView

            } else{
                var myView=layoutInflater.inflate(R.layout.tweets_ticket,null)
                return myView
            }
        }

        override fun getItem(p0: Int): Any {
            return listNotesAdpater[p0]
        }

        override fun getItemId(p0: Int): Long {
            return p0.toLong()
        }

        override fun getCount(): Int {

            return listNotesAdpater.size

        }
    }
}



