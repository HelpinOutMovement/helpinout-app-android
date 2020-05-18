package org.helpinout.billonlights.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import org.helpinout.billonlights.model.BillionLightsApplication
import org.helpinout.billonlights.model.dagger.PreferencesService
import org.helpinout.billonlights.model.database.entity.*
import org.helpinout.billonlights.service.LocationService
import org.helpinout.billonlights.service.OfferRequestDetailService
import org.helpinout.billonlights.service.OfferRequestListService
import org.helpinout.billonlights.utils.getStringException
import org.jetbrains.anko.doAsync
import javax.inject.Inject


class OfferViewModel(application: Application) : AndroidViewModel(application) {


    @Inject
    lateinit var locationService: LocationService

    @Inject
    lateinit var offerRequestListService: OfferRequestListService

    @Inject
    lateinit var offerRequestDetailService: OfferRequestDetailService

    @Inject
    lateinit var preferencesService: PreferencesService

    private val compositeDisposable = CompositeDisposable()



    init {
        (application as BillionLightsApplication).getAppComponent().inject(this)
    }

    fun getMyRequestsOrOffers(offerType: Int, initiator: Int,context:Context): MutableLiveData<List<AddCategoryDbItem>> {
        val offerRequestResponse = MutableLiveData<List<AddCategoryDbItem>>()
        GlobalScope.launch(Dispatchers.IO) {
            try {
                offerRequestResponse.postValue(offerRequestListService.getMyRequestsOrOffers(offerType, initiator,context))
            } catch (e: Exception) {

            }
        }

        return offerRequestResponse
    }

    fun getRequestDetails(offerType: Int, initiator: Int, activity_uuid: String,location:String): MutableLiveData<List<MappingDetail>> {
        val list = MutableLiveData<List<MappingDetail>>()
        GlobalScope.launch(Dispatchers.IO) {
            val listItems = offerRequestDetailService.getRequestDetails(offerType, initiator, activity_uuid,location)
            list.postValue(listItems)
        }
        return list
    }

    fun deleteActivity(uuid: String, activity_type: Int): MutableLiveData<Pair<DeleteDataResponses?, String>> {
        val response = MutableLiveData<Pair<DeleteDataResponses?, String>>()
        GlobalScope.launch(Dispatchers.IO) {
            try {
                response.postValue(Pair(offerRequestDetailService.deleteActivity(uuid, activity_type), ""))
            } catch (e: Exception) {
                response.postValue(Pair(null, e.getStringException()))
            }
        }
        return response
    }

    fun sendOfferRequesterToServer(radius:Float,lat:Double,longitude:Double,isSendToAll: Int, activity_type: Int, activity_uuid: String, list: List<ActivityAddDetail>): MutableLiveData<Pair<ActivityResponses?, String>> {
        val response = MutableLiveData<Pair<ActivityResponses?, String>>()
        GlobalScope.launch(Dispatchers.IO) {
            try {
                response.postValue(Pair(locationService.sendOfferRequests(radius,lat,longitude,isSendToAll, activity_type, activity_uuid, list), ""))
            } catch (e: Exception) {
                response.postValue(Pair(null, e.getStringException()))
            }
        }
        return response
    }



    fun deleteMapping(parent_uuid: String?, activity_uuid: String, activity_type: Int,mapping_initiator: Int): MutableLiveData<Pair<String?, String>> {
        val response = MutableLiveData<Pair<String?, String>>()
        GlobalScope.launch(Dispatchers.IO) {
            try {
                response.postValue(Pair(offerRequestDetailService.deleteMappingFromServer(parent_uuid, activity_uuid, activity_type,mapping_initiator), ""))
            } catch (e: Exception) {
                response.postValue(Pair(null, e.getStringException()))
            }
        }
        return response
    }


    fun makeRating(parent_uuid: String?, activity_uuid: String, activityType: Int, rating: String, recommendOther: Int, comments: String): MutableLiveData<Pair<String?, String>> {
        val response = MutableLiveData<Pair<String?, String>>()
        GlobalScope.launch(Dispatchers.IO) {
            try {
                response.postValue(Pair(locationService.makeRating(parent_uuid, activity_uuid, activityType, rating, recommendOther, comments), ""))
            } catch (e: Exception) {
                response.postValue(Pair(null, e.getStringException()))
            }
        }
        return response
    }

    fun makeCallTracking(parent_uuid: String?, activity_uuid: String, activityType: Int,mapping_initiator:Int): MutableLiveData<Pair<String?, String>> {
        val response = MutableLiveData<Pair<String?, String>>()
        GlobalScope.launch(Dispatchers.IO) {
            try {
                response.postValue(Pair(offerRequestDetailService.makeCallTracking(parent_uuid, activity_uuid, activityType,mapping_initiator), ""))
            } catch (e: Exception) {
                response.postValue(Pair(null, e.getStringException()))
            }
        }
        return response
    }

    fun getUserRequestOfferList(context: Context, activityType: Int, activity_uuid: String = ""): MutableLiveData<Pair<ActivityResponses?, String>> {
        val result = MutableLiveData<Pair<ActivityResponses?, String>>()
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = offerRequestListService.getUserRequestsOfferList(context, activityType, activity_uuid)
                result.postValue(Pair(response, ""))
            } catch (e: Exception) {
                result.postValue(Pair(null, e.getStringException()))
            }
        }
        return result
    }

    fun getNewMatchesResponse(activityType: Int): MutableLiveData<Pair<NewMatchResponses?, String>> {
        val result = MutableLiveData<Pair<NewMatchResponses?, String>>()
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = offerRequestListService.getNewMatches(activityType)
                result.postValue(Pair(response, ""))
            } catch (e: Exception) {
                result.postValue(Pair(null, e.getStringException()))
            }
        }
        return result
    }

    fun sendPeopleHelp(peopleHelp: AddData, activityDetail: ActivityDetail): MutableLiveData<Pair<ActivityResponses?, String>> {
        val response = MutableLiveData<Pair<ActivityResponses?, String>>()
        GlobalScope.launch(Dispatchers.IO) {
            try {
                response.postValue(Pair(locationService.getPeopleResponse(peopleHelp,activityDetail), ""))
            } catch (e: Exception) {
                response.postValue(Pair(null, e.getStringException()))
            }
        }
        return response
    }

    fun sendAmbulanceHelp(ambulanceHelp: AddData): MutableLiveData<Pair<ServerResponse?, String>> {
        val response = MutableLiveData<Pair<ServerResponse?, String>>()
        GlobalScope.launch(Dispatchers.IO) {
            try {
                response.postValue(Pair(locationService.getAmbulanceHelpResponse(ambulanceHelp), ""))
            } catch (e: Exception) {
                response.postValue(Pair(null, e.getStringException()))
            }
        }
        return response
    }

    fun saveFoodItemToDatabase(addItemList: ArrayList<AddCategoryDbItem>): MutableLiveData<Boolean> {
        val response = MutableLiveData<Boolean>()
        doAsync {
            response.postValue(offerRequestListService.saveFoodItemsToDb(addItemList))
        }
        return response
    }

    fun saveMapping(mappingList: ArrayList<MappingDetail>): MutableLiveData<Boolean> {
        val response = MutableLiveData<Boolean>()
        doAsync {
            response.postValue(offerRequestListService.saveMappingToDb(mappingList))
        }
        return response
    }


    fun deleteMappingFromDatabase(parent_uuid: String?, activity_uuid: String): MutableLiveData<Boolean> {
        val response = MutableLiveData<Boolean>()
        doAsync {
            response.postValue(offerRequestDetailService.deleteMappingFromDb(parent_uuid, activity_uuid))
        }
        return response
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

}