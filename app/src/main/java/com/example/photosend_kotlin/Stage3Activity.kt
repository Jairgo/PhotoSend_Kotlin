package com.example.photosend_kotlin

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import android.os.Bundle
import android.graphics.Bitmap
import android.os.Parcelable
import android.graphics.BitmapFactory
import android.text.TextWatcher
import android.text.Editable
import android.annotation.SuppressLint
import android.net.Uri
import android.view.View
import com.example.photosend_kotlin.databinding.ActivityStage3Binding
import java.io.FileNotFoundException
import java.io.InputStream

class Stage3Activity : AppCompatActivity() {
    private var binding: ActivityStage3Binding? = null
    private var view: View? = null
    // private var intent: Intent? = null
    private var uriString: String? = null
    private var name: String? = null
    private var email: String? = null
    private var message: String? = null
    private var stream: InputStream? = null
    private lateinit var uri: Uri

    private val resultMailLauncher = registerForActivityResult(StartActivityForResult()){ result ->
        if (result.resultCode == RESULT_OK) {
            binding!!.btnSendMail.text = getString(R.string.emailSent)
            val endIntent = Intent(applicationContext, MainActivity::class.java)
            endIntent.putExtra("emailSent", "true")
            startActivity(endIntent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStage3Binding.inflate(
            layoutInflater
        )
        view = binding!!.root
        setContentView(view)
        val intent = intent

        name = intent.getStringExtra("name")
        email = intent.getStringExtra("email")
        message = "No message included"
        var bitmap = intent.getParcelableExtra<Parcelable>("viewImage") as Bitmap?
        binding!!.viewPhotoS3.setImageBitmap(bitmap)
        uriString = intent.getStringExtra("uriImage")
        uri = Uri.parse(intent.getStringExtra("uriImage"))

        try {
            stream = contentResolver.openInputStream(uri)
            bitmap = BitmapFactory.decodeStream(stream)
            binding!!.viewPhotoS3.setImageBitmap(bitmap)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        binding!!.textName.text = name
        binding!!.textEmail.text = email
        binding!!.edtMessage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                message = binding!!.edtMessage.text.toString()
            }
        })
        binding!!.btnSendMail.setOnClickListener {
            composeEmail(
                email,
                "Hello $name!",
                uri,
                message!!
            )
        }
    }

    @SuppressLint("IntentReset", "QueryPermissionsNeeded")
    private fun composeEmail(address: String?, subject: String, attachment: Uri?, message: String) {
        val sendMailIntent = Intent(Intent.ACTION_SEND)
        sendMailIntent.type = "*/*"
        sendMailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(address))
        sendMailIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
        sendMailIntent.putExtra(Intent.EXTRA_STREAM, attachment)
        sendMailIntent.putExtra(Intent.EXTRA_TEXT, message)
        resultMailLauncher.launch(sendMailIntent)
    }
}