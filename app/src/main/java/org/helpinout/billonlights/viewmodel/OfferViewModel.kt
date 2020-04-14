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
import org.helpinout.billonlights.utils.*
import org.jetbrains.anko.doAsync
import timber.log.Timber
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

    fun getUserRequestOfferList(context: Context, activityType: String): MutableLiveData<Pair<ActivityResponses?, String>> {
        val result = MutableLiveData<Pair<ActivityResponses?, String>>()
        GlobalScope.launch(Dispatchers.IO) {
            try {

                val response = locationService.getUserRequestsOfferList(activityType)

                val offers = response.data?.offers
                val requests = response.data?.requests
                if (!offers.isNullOrEmpty()) {
                    insertItemToDatabase(context, offers)
                }
                if (!requests.isNullOrEmpty()) {
                    insertItemToDatabase(context, requests)
                }

                result.postValue(Pair(response, ""))
            } catch (e: Exception) {
                result.postValue(Pair(null, e.getStringException()))
            }
        }
        return result
    }

    private fun insertItemToDatabase(context: Context, offers: List<ActivityAddDetail>) {
        val addDataList = ArrayList<AddCategoryDbItem>()
        offers.forEach { offer ->
            try {
                val item = AddCategoryDbItem()
                item.activity_type = offer.activity_type
                item.activity_uuid = offer.activity_uuid

                var itemDetail = ""
                offer.activity_detail?.forEachIndexed { index, it ->

                    if (offer.activity_category == CATEGORY_PEOPLE) {
                        //for people
                        item.volunters_required = it.volunters_required
                        item.volunters_detail = it.volunters_detail
                        item.volunters_quantity = it.volunters_quantity
                        item.technical_personal_required = it.technical_personal_required
                        item.technical_personal_detail = it.technical_personal_detail
                        item.technical_personal_quantity = it.technical_personal_quantity

                        if (!it.volunters_detail.isNullOrEmpty() || !it.volunters_quantity.isNullOrEmpty()) {
                            itemDetail += it.volunters_detail + "(" + it.volunters_quantity + ")"

                        }
                        if (!it.technical_personal_detail.isNullOrEmpty()) {
                            if (itemDetail.isNotEmpty()) {
                                itemDetail += ","
                            }
                            itemDetail += it.technical_personal_detail + "(" + it.technical_personal_quantity + ")"
                        }

                    } else if (offer.activity_category == CATEGORY_AMBULANCE) {
                        item.qty = it.quantity
                        itemDetail = ""
                    } else {
                        itemDetail += it.detail + "(" + it.quantity + ")"
                        if (offer.activity_detail!!.size - 1 != index) {
                            itemDetail += ","
                        }
                    }
                }

                offer.mapping?.forEach { mapping ->
                    if (mapping.offer_detail != null) {
                        mapping.offer_detail?.app_user_detail?.parent_uuid = offer.activity_uuid
                        mapping.offer_detail?.app_user_detail?.activity_type = offer.activity_type
                        mapping.offer_detail?.app_user_detail?.activity_uuid = mapping.offer_detail?.activity_uuid
                        mapping.offer_detail?.app_user_detail?.activity_category = mapping.offer_detail?.activity_category
                        mapping.offer_detail?.app_user_detail?.date_time = mapping.offer_detail?.date_time
                        mapping.offer_detail?.app_user_detail?.geo_location = mapping.offer_detail?.geo_location
                        mapping.offer_detail?.app_user_detail?.offer_condition = mapping.offer_detail?.offer_condition
                        mapping.offer_detail?.app_user_detail?.request_mapping_initiator = mapping.request_mapping_initiator
                    } else if (mapping.request_detail != null) {
                        mapping.request_detail?.app_user_detail?.parent_uuid = offer.activity_uuid
                        mapping.request_detail?.app_user_detail?.activity_type = offer.activity_type
                        mapping.request_detail?.app_user_detail?.activity_uuid = mapping.request_detail?.activity_uuid
                        mapping.request_detail?.app_user_detail?.activity_category = mapping.request_detail?.activity_category
                        mapping.request_detail?.app_user_detail?.date_time = mapping.request_detail?.date_time
                        mapping.request_detail?.app_user_detail?.geo_location = mapping.request_detail?.geo_location
                        mapping.request_detail?.app_user_detail?.offer_condition = mapping.request_detail?.offer_condition
                        mapping.request_detail?.app_user_detail?.request_mapping_initiator = mapping.request_mapping_initiator
                    }
                }
                val mappingList = ArrayList<MappingDetail>()
                offer.mapping?.forEach {
                    if (it.offer_detail != null) {
                        mappingList.add(it.offer_detail!!.app_user_detail!!)
                    } else {
                        mappingList.add(it.request_detail!!.app_user_detail!!)
                    }
                }
                if (mappingList.isNotEmpty()) saveMapping(mappingList)

                item.detail = itemDetail
                item.activity_uuid = offer.activity_uuid
                item.date_time = offer.date_time
                item.activity_category = offer.activity_category
                item.activity_count = offer.activity_count
                item.geo_location = offer.geo_location
                item.address = context.getAddress(preferencesService.latitude.toDouble(), preferencesService.longitude.toDouble())
                item.status = 1
                addDataList.add(item)
            } catch (e: Exception) {
                Timber.d("")
            }
        }
        addDataList.reverse()
        saveFoodItemToDatabase(addDataList)
    }


    fun saveMapping(mappingList: ArrayList<MappingDetail>): MutableLiveData<Boolean> {
        val response = MutableLiveData<Boolean>()
        doAsync {
            response.postValue(locationService.saveMappingToDb(mappingList))
        }
        return response
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