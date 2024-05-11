package com.gryfoapp

import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var captureIV : ImageView
    private lateinit var imageUrl : Uri

    private val contract = registerForActivityResult(ActivityResultContracts.TakePicture()){

        captureIV.setImageURI(null)
        captureIV.setImageURI(imageUrl)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        imageUrl = createImageUri()
        captureIV = findViewById(R.id.captureImageView)
        val captureImageButton = findViewById<Button>(R.id.captureImageButton)
        captureImageButton.setOnClickListener {
            contract.launch(imageUrl)
        }



        //do código
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun createImageUri():Uri{
        val image = File(filesDir,"camera_photos.png")
        return FileProvider.getUriForFile(this,
            "com.gryfoapp.FileProvider",
            image)
    }
}