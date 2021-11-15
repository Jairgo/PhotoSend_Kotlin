package com.example.photosend_kotlin

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.graphics.Color
import android.widget.Toast
import android.widget.TextView
import android.os.Bundle
import android.view.View
import com.example.photosend_kotlin.R
import com.example.photosend_kotlin.MainActivity
import com.example.photosend_kotlin.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var view: View
    private var emailSent: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater )
        view = binding.root
        setContentView(view)
        var intent = intent

        if (intent != null) {
            emailSent = intent.getStringExtra("emailSent")
            if (emailSent != null) {
                val message = Snackbar.make(view,"The email was sent successfully",Snackbar.LENGTH_LONG)
                message.setBackgroundTint(Color.rgb(18, 108, 27))
                if (emailSent == "true")
                    message.show()
            }
        }

        binding.btnStart.setOnClickListener {
            intent = Intent(this, Stage1Activity::class.java)
            startActivity(intent)
        }
    }
}