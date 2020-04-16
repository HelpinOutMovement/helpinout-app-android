package org.helpinout.billonlights.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import org.helpinout.billonlights.model.BillionLightsApplication
import org.helpinout.billonlights.model.dagger.PreferencesService
import org.helpinout.billonlights.model.database.entity.*
import org.helpinout.billonlights.service.LocationService
import org.helpinout.billonlights.utils.getIcon
import org.helpinout.billonlights.utils.getName
import org.helpinout.billonlights.utils.getStringException
import org.jetbrains.anko.doAsync
import javax.inject.Inject


class OfferViewModel(application: Application) : AndroidViewModel(application) {


    @Inject
    lateinit var locationService: LocationService

    @Inject
    lateinit var preferencesService: PreferencesService

    init {
        (application as BillionLightsApplication).getAppComponent().inject(this)
    }

    fun getMyRequestsOrOffers(offerType: Int, initiator: Int, context: Context): MutableLiveData<List<AddCategoryDbItem>> {
        val list = MutableLiveData<List<AddCategoryDbItem>>()
        GlobalScope.launch(Dispatchers.IO) {
            val listItems = locationService.getMyRequestsOrOffers(offerType, initiator)
            listItems.forEach { item ->
                item.name = context.getString(item.activity_category.getName())
                item.icon = item.activity_category.getIcon()
            }
            list.postValue(listItems)
        }
        return list
    }

    fun getRequestDetails(offerType: Int, initiator: Int, activity_uuid: String): MutableLiveData<List<MappingDetail>> {
        val list = MutableLiveData<List<MappingDetail>>()
        GlobalScope.launch(Dispatchers.IO) {
            val listItems = locationService.getRequestDetails(offerType, initiator, activity_uuid)
            list.postValue(listItems)
        }
        return list
    }

    fun sendOfferRequesterToServer(activity_type: Int, activity_uuid: String, list: List<ActivityAddDetail>): MutableLiveData<Pair<ActivityResponses?, String>> {
        val response = MutableLiveData<Pair<ActivityResponses?, String>>()
        GlobalScope.launch(Dispatchers.IO) {
            try {
                response.postValue(Pair(locationService.sendOfferRequests(activity_type, activity_uuid, list), ""))
            } catch (e: Exception) {
                response.postValue(Pair(null, e.getStringException()))
            }
        }
        return response
    }

    fun deleteActivity(uuid: String, activity_type: Int): MutableLiveData<Pair<DeleteDataResponses?, String>> {
        val response = MutableLiveData<Pair<DeleteDataResponses?, String>>()
        GlobalScope.launch(Dispatchers.IO) {
            try {
                response.postValue(Pair(locationService.deleteActivity(uuid, activity_type), ""))
            } catch (e: Exception) {
                response.postValue(Pair(null, e.getStringException()))
            }
        }
        return response
    }

    fun deleteMapping(parent_uuid: String?, activity_uuid: String, activity_type: Int): MutableLiveData<Pair<String?, String>> {
        val response = MutableLiveData<Pair<String?, String>>()
        GlobalScope.launch(Dispatchers.IO) {
            try {
                response.postValue(Pair(locationService.deleteMappingFromServer(parent_uuid, activity_uuid, activity_type), ""))
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

    fun makeCallTracking(parent_uuid: String?, activity_uuid: String, activityType: Int): MutableLiveData<Pair<String?, String>> {
        val response = MutableLiveData<Pair<String?, String>>()
        GlobalScope.launch(Dispatchers.IO) {
            try {
                response.postValue(Pair(locationService.makeCallTracking(parent_uuid, activity_uuid, activityType), ""))
            } catch (e: Exception) {
                response.postValue(Pair(null, e.getStringException()))
            }
        }
        return response
    }

    fun getUserRequestOfferList(context: Context, activityType: Int): MutableLiveData<Pair<ActivityResponses?, String>> {
        val result = MutableLiveData<Pair<ActivityResponses?, String>>()
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = locationService.getUserRequestsOfferList(context, activityType)
                result.postValue(Pair(response, ""))
            } catch (e: Exception) {
                result.postValue(Pair(null, e.getStringException()))
            }
        }
        return result
    }


    fun sendPeopleHelp(peopleHelp: AddData): MutableLiveData<Pair<ActivityResponses?, String>> {
        val response = MutableLiveData<Pair<ActivityResponses?, String>>()
        GlobalScope.launch(Dispatchers.IO) {
            try {
                response.postValue(Pair(locationService.getPeopleResponse(peopleHelp), ""))
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
            response.postValue(locationService.saveFoodItemsToDb(addItemList))
        }
        return response
    }

    fun saveMapping(mappingList: ArrayList<MappingDetail>): MutableLiveData<Boolean> {
        val response = MutableLiveData<Boolean>()
        doAsync {
            response.postValue(locationService.saveMappingToDb(mappingList))
        }
        return response
    }

    fun deleteActivityFromDatabase(activityUuid: String?): MutableLiveData<Boolean> {
        val response = MutableLiveData<Boolean>()
        doAsync {
            response.postValue(locationService.deleteActivityFromDb(activityUuid))
        }
        return response
    }

    fun deleteMappingFromDatabase(parent_uuid: String?, activity_uuid: String): MutableLiveData<Boolean> {
        val response = MutableLiveData<Boolean>()
        doAsync {
            response.postValue(locationService.deleteMappingFromDb(parent_uuid, activity_uuid))
        }
        return response
    }
}