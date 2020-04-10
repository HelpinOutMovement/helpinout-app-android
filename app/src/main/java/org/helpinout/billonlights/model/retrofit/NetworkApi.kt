package org.helpinout.billonlights.model.retrofit

import kotlinx.coroutines.Deferred
import org.helpinout.billonlights.model.database.entity.*
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
    fun getRegistrationResponseAsync(@Body body: String): Deferred<RegistrationResponse>

    @POST("user/update")
    fun getProfileUpdateResponseAsync(@Body body: String): Deferred<RegistrationResponse>

    @POST("user/currentlocation")
    fun getUserLocationResponseAsync(@Body body: String): Deferred<String>

    @POST("activity/suggestions")
    fun getActivitySuggestionResponseAsync(@Body body: String): Deferred<String>

    @POST("activity/add")
    fun getActivityNewAddResponseAsync(@Body body: String): Deferred<AddDataResponses>

    @POST("activity/add")
    fun getActivityAmbulancesponseAsync(@Body body: String): Deferred<ServerResponse>


    @POST("activity/delete")
    fun getMappingDeleteResponseAsync(@Body body: String): Deferred<DeleteDataResponses>

    @POST("mapping/rating")
    fun getMappingRatingResponseAsync(@Body body: String): Deferred<String>

    @POST("mapping/call")
    fun getCallInitiateResponseAsync(@Body body: String): Deferred<String>

    @POST("user/pastactivity")
    fun getUserRequestOfferListResponseAsync(@Body body: String): Deferred<AddDataResponses>

    @GET
    fun getSearchResult(@Url url: String): Call<String>

}