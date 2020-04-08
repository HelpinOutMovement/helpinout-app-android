package com.triline.billionlights.model.retrofit

import com.triline.billionlights.model.database.entity.LoginResponse
import kotlinx.coroutines.Deferred
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url


/**
 * Created by Avneesh on 3/25/2018.
 */
interface NetworkApi {

    @POST("user/login")
    fun verifyExistingUserAsync(@Body body: String): Deferred<LoginResponse>

    @POST("user/register")
    fun getRegistrationResponseAsync(@Body body: String): Deferred<LoginResponse>

    @POST("user/currentlocation")
    fun getUserLocationResponseAsync(@Body body: String): Deferred<String>

    @POST("activity/suggestions")
    fun getActivitySuggestionResponseAsync(@Body body: String): Deferred<String>

    @POST("activity/add")
    fun getActivityNewAddResponseAsync(@Body body: String): Deferred<String>

    @POST("activity/delete")
    fun getMappingDeleteResponseAsync(@Body body: String): Deferred<String>

    @POST("mapping/rating")
    fun getMappingRatingResponseAsync(@Body body: String): Deferred<String>

    @POST("mapping/call")
    fun getCallInitiateResponseAsync(@Body body: String): Deferred<String>

    @POST("user/pastactivity")
    fun getUserRequestOfferListResponseAsync(@Body body: String): Deferred<String>

    @GET
    fun getSearchResult(@Url url: String): Call<String>

}