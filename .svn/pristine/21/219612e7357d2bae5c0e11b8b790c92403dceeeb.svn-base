package com.example.frindshipassignment.api

import ngo.friendship.satellite.communication.ResponseData
import ngo.friendship.satellite.model.demo.Product
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface EndPoints {
    @GET("products/1")
    suspend fun getProducts() : Response<Product>

    @GET("usergate")
    suspend fun getUserGate(): ResponseData

    @POST("/usergate")
    suspend fun suserLogin(): ResponseData

    @FormUrlEncoded
    @POST("/usergate")
    fun userLogin(@FieldMap params: Map<String, String>): Call<ResponseData>
}