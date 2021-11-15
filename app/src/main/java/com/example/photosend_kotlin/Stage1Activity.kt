package com.example.photosend_kotlin

import android.Manifest
import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import android.graphics.Bitmap
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import android.content.Context
import android.provider.MediaStore
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.View
import androidx.core.app.ActivityCompat
import com.example.photosend_kotlin.databinding.ActivityStage1Binding
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class Stage1Activity : AppCompatActivity() {
    private var binding: ActivityStage1Binding? = null
    private var view: View? = null
    private var bitmap: Bitmap? = null
    private var galleryUri: Uri? = null
    private var cameraUri: Uri? = null

    private val resultPhotoLauncher = registerForActivityResult(StartActivityForResult()){ result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            galleryUri = loadImage(result.data)
            binding!!.btnGallery.setText(R.string.gotoStage2)

            binding!!.btnGallery.setOnClickListener {
                intent = Intent(applicationContext, Stage2Activity::class.java)
                intent!!.putExtra("uriImage", galleryUri.toString())
                startActivity(intent)
            }
        }
    }

    private val resultCameraLauncher = registerForActivityResult(StartActivityForResult()){ result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            bitmap = result.data!!.extras!!["data"] as Bitmap?
            binding!!.viewPhotoS1.setImageBitmap(bitmap)

            binding!!.btnGallery.setText(R.string.gotoStage2)
            cameraUri = getImageUri(applicationContext, bitmap)

            binding!!.btnGallery.setOnClickListener {
                intent = Intent(applicationContext, Stage2Activity::class.java)
                intent!!.putExtra("uriImage", cameraUri.toString())
                startActivity(intent)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStage1Binding.inflate(
            layoutInflater
        )

        view = binding!!.root
        setContentView(view)

        binding!!.btnGallery.setOnClickListener { listenerLoadPicture() }
        binding!!.viewPhotoS1.setOnClickListener { listenerOpenCamera() }
    }

    private fun listenerLoadPicture() {
        val loadPictureIntent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        loadPictureIntent.type = "image/*"
        resultPhotoLauncher.launch(loadPictureIntent)
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun listenerOpenCamera() {

        val openCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        if(openCameraIntent.resolveActivity(packageManager) != null)
            resultCameraLauncher.launch(openCameraIntent)
    }

    private fun loadImage(data: Intent?): Uri? {
        val uri: Uri?
        val stream: InputStream?
        try {
            uri = data!!.data
            stream = contentResolver.openInputStream(uri!!)
            val bitmap = BitmapFactory.decodeStream(stream)
            binding!!.viewPhotoS1.setImageBitmap(bitmap)
            return uri
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        return null
    }

    @SuppressLint("SimpleDateFormat")
    private fun getImageUri(inContext: Context, inImage: Bitmap?): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage!!.compress(Bitmap.CompressFormat.JPEG, 100, bytes)

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            1
        )

        val path = MediaStore.Images.Media.insertImage(
            inContext.contentResolver,
            inImage,
            imageFileName,
            null
        )

        /*val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, imageFileName)
        val imageUri = contentResolver?.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)*/

        return Uri.parse(path)
    }

}