package com.gryfoapp

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import java.io.ByteArrayOutputStream
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var capture1 : ImageView
    private lateinit var imageUrl : Uri
    private lateinit var convertTextView: TextView
    private val contract = registerForActivityResult(ActivityResultContracts.TakePicture()){

        capture1.setImageURI(null)
        capture1.setImageURI(imageUrl)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageUrl = createImageUri()
        capture1 = findViewById(R.id.captureImageView1)
        convertTextView = findViewById(R.id.convertTextView)
        val captureImageButton = findViewById<Button>(R.id.captureImageButton)
        captureImageButton.setOnClickListener {
            contract.launch(imageUrl)
        }

        val buttonConvert = findViewById<Button>(R.id.buttonConvert)
        buttonConvert.setOnClickListener {
            val bitmap = bitMapConvertor()
            val base64String = base64Convertor(bitmap)
            Log.d("base64Converted", "Base 64 encoded : $base64String")
            convertTextView.text = base64String
        }
    }

    private fun bitMapConvertor() : Bitmap {
        val drawable = capture1.drawable as BitmapDrawable
        return drawable.bitmap
    }

    private fun base64Convertor(bitmap: Bitmap) : String {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream)
        val imageBase = stream.toByteArray()
        return Base64.encodeToString(imageBase,Base64.DEFAULT)
    }

    private fun createImageUri():Uri {
        val image = File(filesDir,"camera_photos.png")
        return FileProvider.getUriForFile(this,
            "com.gryfoapp.FileProvider",
            image)
    }
}
