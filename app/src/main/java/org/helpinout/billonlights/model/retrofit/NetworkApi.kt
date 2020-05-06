package org.helpinout.billonlights.model.retrofit

import kotlinx.coroutines.Deferred
import org.helpinout.billonlights.model.database.entity.*
import retrofit2.http.Body
import retrofit2.http.POST


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

    @POST("user/locationsuggestion")
    fun getUserLocationResponseAsync(@Body body: String): Deferred<LocationSuggestionResponses>

    @POST("activity/suggestions")
    fun getActivitySuggestionResponseAsync(@Body body: String): Deferred<ActivityResponses>

    @POST("activity/add")
    fun getActivityNewAddResponseAsync(@Body body: String): Deferred<ActivityResponses>

    @POST("activity/add")
    fun getActivityAmbulancesResponseAsync(@Body body: String): Deferred<ServerResponse>


    @POST("activity/delete")
    fun getActivityDeleteResponseAsync(@Body body: String): Deferred<DeleteDataResponses>

    @POST("mapping/delete")
    fun getMappingDeleteResponseAsync(@Body body: String): Deferred<String>


    @POST("mapping/rating")
    fun getMappingRatingResponseAsync(@Body body: String): Deferred<String>

    @POST("activity/mapping")
    fun sendOfferRequestsAsync(@Body body: String): Deferred<ActivityResponses>

    @POST("mapping/call")
    fun getCallInitiateResponseAsync(@Body body: String): Deferred<String>

    @POST("user/pastactivity")
    fun getUserRequestOfferListResponseAsync(@Body body: String): Deferred<ActivityResponses>

    @POST("user/locationrequestersummary")
    fun getRequestSummaryResponseAsync(@Body body: String): Deferred<OfferHelpResponses>
}