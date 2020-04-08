package com.triline.billionlights.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.triline.billionlights.R
import com.triline.billionlights.model.BillionLightsApplication
import com.triline.billionlights.model.database.entity.BottomHelp
import com.triline.billionlights.model.database.entity.OfferReceived
import com.triline.billionlights.model.database.entity.RequestSent
import com.triline.billionlights.service.LocationService
import com.triline.billionlights.utils.OFFER_RECEIVED
import com.triline.billionlights.utils.REQUEST_SENT
import com.triline.billionlights.utils.getStringException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import javax.inject.Inject


class OfferViewModel(application: Application) : AndroidViewModel(application) {


    @Inject
    lateinit var locationService: LocationService


    init {
        (application as BillionLightsApplication).getAppComponent().inject(this)
    }

    fun getReceivedOffers(offerType: Int): MutableLiveData<List<OfferReceived>> {
        val list = MutableLiveData<List<OfferReceived>>()
        val offerItemList = ArrayList<OfferReceived>()


        if (offerType == OFFER_RECEIVED) {
            offerItemList.add(OfferReceived("Tina Jamna", "has offered to help you. 14 April 2020, 10:45 am Call them on +91 543053490 Time: 6pm to 9pm.", "+91 543053490", R.drawable.ic_food))
            offerItemList.add(OfferReceived("Bhaskar Rao", "has offered to help you. 14 April 2020, 10:45 am Call them on +91 543053490 Time: 6pm to 9pm.", "+91 543053490", R.drawable.ic_food))
            offerItemList.add(OfferReceived("Subramanian Swamy", "has offered to help you. 14 April 2020, 10:45 am Call them on +91 543053490 Time: 6pm to 9pm.", "+91 543053490", R.drawable.ic_food))

            offerItemList.add(OfferReceived("Tina Jamna", "has offered to help you. 14 April 2020, 10:45 am Call them on +91 543053490 Time: 6pm to 9pm.", "+91 543053490", R.drawable.ic_food))
            offerItemList.add(OfferReceived("Bhaskar Rao", "has offered to help you. 14 April 2020, 10:45 am Call them on +91 543053490 Time: 6pm to 9pm.", "+91 543053490", R.drawable.ic_food))
            offerItemList.add(OfferReceived("Subramanian Swamy", "has offered to help you. 14 April 2020, 10:45 am Call them on +91 543053490 Time: 6pm to 9pm.", "+91 543053490", R.drawable.ic_food))
        } else {
            offerItemList.add(OfferReceived("Anupam Gupta", "has requested your help 14 April 2020, 10:45 am Call them on +91 543053490", "+91 543053490", R.drawable.ic_food))
            offerItemList.add(OfferReceived("Bhaskar Rao", "has requested your help 14 April 2020, 10:45 am Call them on +91 543053490.", "+91 543053490", R.drawable.ic_food))
            offerItemList.add(OfferReceived("Subramanian Swamy", "has requested your help 14 April 2020, 10:45 am Call them on +91 543053490.", "+91 543053490", R.drawable.ic_food))

        }
        list.postValue(offerItemList)
        return list
    }

    fun getRequestSent(offerType: Int): MutableLiveData<List<RequestSent>> {
        val list = MutableLiveData<List<RequestSent>>()
        val offerItemList = ArrayList<RequestSent>()

        if (offerType == REQUEST_SENT) {
            offerItemList.add(RequestSent("Tina Jamna", "was sent a help request on 14 April 2020, 10:45 am They will call you if they can help you.", R.drawable.ic_food))
            offerItemList.add(RequestSent("Shyam Narayan", "has requested your help 14 April 2020, 10:45 am Call them on +91 543053490", R.drawable.ic_food))
            offerItemList.add(RequestSent("Subramanian Swamy", "was sent a help request on 14 April 2020, 10:45 am They will call you if they can help you.", R.drawable.ic_food))

            offerItemList.add(RequestSent("Shyam Narayan", "was sent a help request on 14 April 2020, 10:45 am They will call you if they can help you.", R.drawable.ic_food))
            offerItemList.add(RequestSent("Bhaskar Rao", "was sent a help request on 14 April 2020, 10:45 am They will call you if they can help you.", R.drawable.ic_food))
            offerItemList.add(RequestSent("Subramanian Swamy", "was sent a help request on 14 April 2020, 10:45 am They will call you if they can help you.", R.drawable.ic_food))
        } else {
            offerItemList.add(RequestSent("Tina Jamna", "was sent your offer to help on 14 April 2020, 10:45 am. They will call you if they need your help", R.drawable.ic_food))
            offerItemList.add(RequestSent("John Doe ", "was sent your offer to help on 14 April 2020, 10:45 am. They will call you if they need your help.", R.drawable.ic_food))
        }

        list.postValue(offerItemList)
        return list
    }

    fun getBottomSheetItem(): MutableLiveData<List<BottomHelp>> {
        val list = MutableLiveData<List<BottomHelp>>()
        val bottomList = ArrayList<BottomHelp>()

        bottomList.add(BottomHelp(R.drawable.ic_food,"Shyam Narayan",4.0f,"Can supply once a day after 12 noon with a minimum 1 day notice. Food will need to be picked up, it cannot be delivered."))
        bottomList.add(BottomHelp(R.drawable.ic_food,"Vinod Kumar",3.5f,"Can supply once a day after 2 noon with a minimum 1 day notice. Food will need to be picked up, it cannot be delivered."))
        bottomList.add(BottomHelp(R.drawable.ic_food,"Sandeep Kumar",4.5f,"Can supply once a day after 2 noon with a minimum 1 day notice. Food will need to be picked up, it cannot be delivered."))

        list.postValue(bottomList)
        return list
    }

    fun deleteActivity(mappingId: String, uuid: String): MutableLiveData<Pair<String?, String>> {
        val response = MutableLiveData<Pair<String?, String>>()
        GlobalScope.launch(Dispatchers.IO) {
            try {
                response.postValue(Pair(locationService.deleteActivity(mappingId, uuid), ""))
            } catch (e: Exception) {
                response.postValue(Pair(null, e.getStringException()))
            }
        }
        return response
    }

    fun makeRating(mappingId: String, uuid: String, rating: String, recommend_other: Int): MutableLiveData<Pair<String?, String>> {
        val response = MutableLiveData<Pair<String?, String>>()
        GlobalScope.launch(Dispatchers.IO) {
            try {
                response.postValue(Pair(locationService.makeRating(mappingId, uuid, rating, recommend_other), ""))
            } catch (e: Exception) {
                response.postValue(Pair(null, e.getStringException()))
            }
        }
        return response
    }

    fun makeCallTracking(mappingId: String, uuid: String): MutableLiveData<Pair<String?, String>> {
        val response = MutableLiveData<Pair<String?, String>>()
        GlobalScope.launch(Dispatchers.IO) {
            try {
                response.postValue(Pair(locationService.makeCallTracking(mappingId, uuid), ""))
            } catch (e: Exception) {
                response.postValue(Pair(null, e.getStringException()))
            }
        }
        return response
    }

    fun getUserRequestOfferList(activityType: String): MutableLiveData<Pair<String?, String>> {
        val response = MutableLiveData<Pair<String?, String>>()
        GlobalScope.launch(Dispatchers.IO) {
            try {
                response.postValue(Pair(locationService.getUserRequestsOfferList(activityType), ""))
            } catch (e: Exception) {
                response.postValue(Pair(null, e.getStringException()))
            }
        }
        return response
    }

}