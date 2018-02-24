package com.example.noey.twitterapp

import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_login.*

/**
 * Created by noey. on 2/24/2018 AD.
 */

class LoginActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        profileImageClick()
        btnLoginClick()
    }

    fun checkPhotoPermission(){
        if(Build.VERSION.SDK_INT >= 23){

        }

        loadImage()
    }

    private fun loadImage() {

    }

    fun profileImageClick(){
        imgProfile.setOnClickListener({
            Toast.makeText(this, "image", Toast.LENGTH_LONG).show()
        })
    }

    fun btnLoginClick(){
        btnLogin.setOnClickListener({
            Toast.makeText(this, "btn", Toast.LENGTH_LONG).show()
        })
    }
}