package com.gryfoapp

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
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
import com.gryfoapp.util.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream
import java.io.File

class MainActivity : AppCompatActivity() {

    // Declaração das variáveis ​​e objetos necessários
    private lateinit var capture1 : ImageView
    private lateinit var capture2 : ImageView
    private lateinit var imageUrl1 : Uri
    private lateinit var imageUrl2 : Uri
    private lateinit var convertTextView: TextView
    private lateinit var apiResponseTextView: TextView
    private var isProcessCompleted = false

    // Contrato para lançar a atividade de captura de imagem
    private val contract = registerForActivityResult(ActivityResultContracts.TakePicture()){ success ->
        if (success) {
            // Verifica se o processo foi concluído e, se sim, redefina-o
            if (isProcessCompleted) {
                resetProcess()
            }
            // Verifica qual ImageView deve ser atualizada com a imagem capturada
            if (capture1.drawable == null) {
                capture1.setImageURI(imageUrl1)
            } else {
                capture2.setImageURI(imageUrl2)
                convertImages() // Converte as imagens e envia para a API
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicialização das variáveis ​​e configuração do botão de captura de imagem
        imageUrl1 = createImageUri("camera_photo1.png")
        imageUrl2 = createImageUri("camera_photo2.png")
        capture1 = findViewById(R.id.captureImageView1)
        capture2 = findViewById(R.id.captureImageView2)
        convertTextView = findViewById(R.id.convertTextView)
        apiResponseTextView = findViewById(R.id.apiResponseTextView)

        val captureImageButton = findViewById<Button>(R.id.captureImageButton)
        captureImageButton.setOnClickListener {
            // Verifica se o processo foi concluído e, se sim, redefina-o
            if (isProcessCompleted) {
                resetProcess()
            }
            // Determina qual imagem será capturada com base na disponibilidade da ImageView
            if (capture1.drawable == null) {
                contract.launch(imageUrl1)
            } else {
                contract.launch(imageUrl2)
            }
        }
    }

    // Método para redefinir o processo
    private fun resetProcess() {
        capture1.setImageDrawable(null)
        capture2.setImageDrawable(null)
        convertTextView.text = ""
        isProcessCompleted = false
    }

    // Método para converter as imagens capturadas em base64 e enviar para a API
    private fun convertImages() {
        val bitmap1 = bitMapConvertor(capture1)
        val base64String1 = base64Convertor(bitmap1)

        val bitmap2 = bitMapConvertor(capture2)
        val base64String2 = base64Convertor(bitmap2)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.gryfo.com.br/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(ApiService::class.java)

        val request = ApiService.Request(base64String1, base64String2, true)
        val call = service.faceMatch(request)

        call.enqueue(object : Callback<ApiService.Response> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<ApiService.Response>, response: Response<ApiService.Response>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    val success = responseBody?.success
                    val message = responseBody?.message
                    val dist = responseBody?.dist
                    val match = responseBody?.match

                    convertTextView.text = "Success: $success\nMessage: $message\nDistance: $dist\nMatch: $match"
                    if (match == true) {
                        apiResponseTextView.setTextColor(Color.GREEN)
                        apiResponseTextView.text = "Match: $match"
                    } else {
                        apiResponseTextView.setTextColor(Color.YELLOW)
                        apiResponseTextView.text = "No Match"
                    }
                    isProcessCompleted = true
                } else {
                    convertTextView.text = "Error: ${response.errorBody()?.string()}"
                    apiResponseTextView.setTextColor(Color.RED)
                    apiResponseTextView.text = "Connection Failed"
                    Log.e("API Response", "Request failed with code: ${response.code()}")
                }
            }

            @SuppressLint("SetTextI18n")
            override fun onFailure(call: Call<ApiService.Response>, t: Throwable) {
                convertTextView.text = "Failure: ${t.message}"
                apiResponseTextView.setTextColor(Color.RED)
                apiResponseTextView.text = "Connection Failed"
            }
        })

    }

    // Método para converter um ImageView em um Bitmap
    private fun bitMapConvertor(imageView: ImageView) : Bitmap {
        val drawable = imageView.drawable as BitmapDrawable
        return drawable.bitmap
    }

    // Método para converter um Bitmap em uma string base64
    private fun base64Convertor(bitmap: Bitmap) : String {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 10, stream)
        val imageBase = stream.toByteArray()
        return Base64.encodeToString(imageBase, Base64.DEFAULT)
    }

    // Método para criar um URI para salvar as imagens capturadas
    private fun createImageUri(filename: String): Uri {
        val image = File(filesDir, filename)
        return FileProvider.getUriForFile(this,
            "com.gryfoapp.FileProvider",
            image)
    }
}
