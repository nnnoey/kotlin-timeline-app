package com.example.noey.twitterapp

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_login.*

/**
 * Created by noey. on 2/24/2018 AD.
 */

class LoginActivity : AppCompatActivity(){

    val REQUEST_CODE_PHOTO = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        profileImageClick()
        btnLoginClick()
    }

    fun checkImagePermission(){
        if(Build.VERSION.SDK_INT >= 23){
            if(ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){

                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_CODE_PHOTO)
                return
            }
        }

        loadImage()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            REQUEST_CODE_PHOTO -> {
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
        })
    }
}