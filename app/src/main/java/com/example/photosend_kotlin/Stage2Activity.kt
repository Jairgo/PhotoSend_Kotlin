package com.example.photosend_kotlin

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.graphics.Bitmap
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import android.os.Bundle
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.ContactsContract
import android.view.View
import com.example.photosend_kotlin.databinding.ActivityStage2Binding
import java.io.FileNotFoundException
import java.io.InputStream

class Stage2Activity : AppCompatActivity() {
    private var binding: ActivityStage2Binding? = null
    private var view: View? = null
    // private var intent: Intent? = null
    private lateinit var uri: Uri
    private var stream: InputStream? = null
    private var uriString: String? = null
    private var name: String? = null
    private var email: String? = null
    private var bitmap: Bitmap? = null


    private val resultContactLauncher = registerForActivityResult(StartActivityForResult()){ result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            selectContact(result.data)
            binding!!.btnToStage3.setText(R.string.gotoStage3)
            binding!!.btnToStage3.setOnClickListener {
                intent = Intent(applicationContext, Stage3Activity::class.java)
                intent!!.putExtra("uriImage", uriString)
                intent!!.putExtra("name", name)
                intent!!.putExtra("email", email)
                startActivity(intent)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStage2Binding.inflate(
            layoutInflater
        )
        view = binding!!.root
        setContentView(view)

        val intent = intent

        uriString = intent.getStringExtra("uriImage")
        uri = Uri.parse(intent.getStringExtra("uriImage"))
        try {
            stream = contentResolver.openInputStream(uri)
            bitmap = BitmapFactory.decodeStream(stream)
            binding!!.viewPhotoS2.setImageBitmap(bitmap)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        binding!!.btnToStage3.setOnClickListener { listenerLoadContact() }
    }

    private fun listenerLoadContact() {
        val loadContactIntent = Intent(Intent.ACTION_PICK)
        loadContactIntent.type = ContactsContract.CommonDataKinds.Email.CONTENT_TYPE
        resultContactLauncher.launch(loadContactIntent)
    }

    private fun selectContact(data: Intent?) {
        val uri = data!!.data
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Email.ADDRESS,
            ContactsContract.Contacts.DISPLAY_NAME
        )
        val cursor = contentResolver.query(uri!!, projection, null, null, null)
        if (cursor!!.count > 0) {
            cursor.moveToFirst()
            val nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
            val emailIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)
            name = cursor.getString(nameIndex)
            email = cursor.getString(emailIndex)
            binding!!.editTextPersonName.setText(name)
            binding!!.editTextPersonEmail.setText(email)

            // binding.editTextPersonName.setEnabled(true);
            // binding.editTextPersonEmail.setEnabled(true);
        }
        cursor.close()
    }
}