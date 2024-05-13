package com.gryfoapp.util

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {
    data class Request(
        val document_img: String,
        val face_img: String,
        val enable_liveness: Boolean
    )

    data class Response(
        val success: Boolean,
        val message: String,
        val dist: Double,
        val match: Boolean
    )

    //TO DO: colocar autorização válida para a API
    @Headers("Content-Type: application/json", "Authorization: {{partner_id}}:{{api_key}}")
    @POST("face_match")
    fun faceMatch(@Body request: Request): Call<Response>
}
