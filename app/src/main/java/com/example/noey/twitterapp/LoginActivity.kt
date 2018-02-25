package com.example.noey.twitterapp

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_login.*
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by noey. on 2/24/2018 AD.
 */

class LoginActivity : AppCompatActivity(){

    val REQUEST_CODE_PERMISSION_IMG = 111
    val REQUEST_CODE_PICK_IMG = 222

    private var firebaseAuth:FirebaseAuth?=null

    private var firebaseDatabase = FirebaseDatabase.getInstance()
    private var firebaseDbRef= firebaseDatabase.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        profileImageClick()
        btnLoginClick()

        firebaseAuth = FirebaseAuth.getInstance()
    }

    fun profileImageClick(){
        imgProfile.setOnClickListener({
            checkImagePermission()
            Toast.makeText(this, "image", Toast.LENGTH_LONG).show()
        })
    }

    fun btnLoginClick(){
        btnLogin.setOnClickListener({
            Toast.makeText(this, "btn", Toast.LENGTH_LONG).show()

            loginToFireBase(etEmail.text.toString(), etPassword.text.toString())
        })
    }

    fun checkImagePermission(){
        if(Build.VERSION.SDK_INT >= 23){
            if(ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){

                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_CODE_PERMISSION_IMG)
                return
            }
        }

        loadImage()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            REQUEST_CODE_PERMISSION_IMG -> {
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    loadImage()
                } else{
                    Toast.makeText(this, "Cannot access your images", Toast.LENGTH_LONG)
                            .show()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    private fun loadImage() {
        var intent = Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
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
            imgProfile.setImageBitmap(BitmapFactory.decodeFile(imagePath))

        }
    }

    fun loginToFireBase(email:String,password:String){

        firebaseAuth!!.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this){ task ->

                    if (task.isSuccessful){
                        Toast.makeText(applicationContext,"Successful login",Toast.LENGTH_LONG).show()
                        saveImageInFirebase()

                    }else
                    {
                        Toast.makeText(applicationContext,"Fail login",Toast.LENGTH_LONG).show()
                    }
                }
    }

    fun saveImageInFirebase(){
        var currentUser = firebaseAuth!!.currentUser
        val email:String = currentUser!!.email.toString()

        var storage = FirebaseStorage.getInstance()
        val storageRef = storage.getReferenceFromUrl("gs://twitterapp-868ea.appspot.com")
        val dateFormat = SimpleDateFormat("ddMMyyHHmmss")
        val dataObj = Date()
        val imagePath = splitString(email) + dateFormat.format(dataObj) + ".jpg"
        val imageRef = storageRef.child("images/"+imagePath)

        imgProfile.isDrawingCacheEnabled = true
        imgProfile.buildDrawingCache()
        val drawable = imgProfile.drawable as BitmapDrawable
        val bitmap = drawable.bitmap
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG,100, byteArrayOutputStream)
        val data = byteArrayOutputStream.toByteArray()
        val uploadTask = imageRef.putBytes(data)

        uploadTask.addOnFailureListener {
            Toast.makeText(applicationContext,"Fail to upload image to storage",Toast.LENGTH_LONG).show()

        }.addOnSuccessListener { taskSnapshot ->
            Toast.makeText(applicationContext,"Upload image to storage successfully",Toast.LENGTH_LONG).show()

            var downloadUrl = taskSnapshot.downloadUrl.toString()

            firebaseDbRef.child("users").child(currentUser.uid).child("email").setValue(currentUser.email)
            firebaseDbRef.child("users").child(currentUser.uid).child("profile_image").setValue(downloadUrl)
            loadtweets()
        }

    }

    fun splitString(email: String):String{
        val split = email.split("@")
        return split[0]
    }

    fun loadtweets(){
        var currentUser = firebaseAuth!!.currentUser
        if(currentUser != null){
            var intent = Intent(this, MainActivity::class.java)
            intent.putExtra("email", currentUser.email)
            intent.putExtra("uid", currentUser.uid)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        loadtweets()
    }

}