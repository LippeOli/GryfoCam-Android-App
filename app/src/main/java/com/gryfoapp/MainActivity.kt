package com.gryfoapp

import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var captureIV1: ImageView
    private lateinit var captureIV2: ImageView
    private lateinit var imageUrl1: Uri
    private lateinit var imageUrl2: Uri
    private var photoCount = 0

    private var contract: ActivityResultLauncher<Uri>? = null // Inicializamos a variável como nula

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        captureIV1 = findViewById(R.id.captureImageView1)
        captureIV2 = findViewById(R.id.captureImageView2)

        val captureImageButton = findViewById<Button>(R.id.captureImageButton)
        captureImageButton.setOnClickListener {
            if (photoCount < 2) {
                if (imageUrl1 == Uri.EMPTY) {
                    imageUrl1 = createImageUri()
                    contract?.launch(imageUrl1)
                } else if (imageUrl2 == Uri.EMPTY) {
                    imageUrl2 = createImageUri()
                    contract?.launch(imageUrl2)
                }
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        imageUrl1 = Uri.EMPTY
        imageUrl2 = Uri.EMPTY

        contract = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                val currentImageView = if (captureIV1.drawable == null) captureIV1 else captureIV2
                currentImageView.setImageURI(imageUrl1)

                if (currentImageView == captureIV1) {
                    imageUrl1 = Uri.parse(imageUrl1.toString())
                } else {
                    imageUrl2 = Uri.parse(imageUrl2.toString())
                }

                photoCount++
            }
        }
    }



    private fun createImageUri(): Uri {
        val image = File(filesDir, "camera_photos_${System.currentTimeMillis()}.png")
        return FileProvider.getUriForFile(this, "com.gryfoapp.FileProvider", image)
    }
}
