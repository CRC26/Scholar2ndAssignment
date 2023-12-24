package ie.wit.scholar.api

import ie.wit.scholar.models.ScholarModel
import retrofit2.Call
import retrofit2.http.*


interface ScholarService {
    @GET("/scholars")
    fun findall(): Call<List<ScholarModel>>

    @GET("/scholars/{email}")
    fun findall(@Path("email") email: String?)
            : Call<List<ScholarModel>>

    @GET("/scholars/{email}/{id}")
    fun get(@Path("email") email: String?,
            @Path("id") id: String): Call<ScholarModel>

    @DELETE("/scholars/{email}/{id}")
    fun delete(@Path("email") email: String?,
               @Path("id") id: String): Call<ScholarWrapper>

    @POST("/scholars/{email}")
    fun post(@Path("email") email: String?,
             @Body scholar: ScholarModel)
            : Call<ScholarWrapper>

    @PUT("/scholars/{email}/{id}")
    fun put(@Path("email") email: String?,
            @Path("id") id: String,
            @Body scholar: ScholarModel
    ): Call<ScholarWrapper>
}

