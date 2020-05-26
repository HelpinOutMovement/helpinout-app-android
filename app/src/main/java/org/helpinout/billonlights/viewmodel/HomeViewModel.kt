package org.helpinout.billonlights.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import org.helpinout.billonlights.R
import org.helpinout.billonlights.model.BillionLightsApplication
import org.helpinout.billonlights.model.database.entity.*
import org.helpinout.billonlights.model.retrofit.NetworkApi
import org.helpinout.billonlights.service.LocationService
import org.helpinout.billonlights.service.OfferRequestListService
import org.helpinout.billonlights.utils.*
import retrofit2.Retrofit
import javax.inject.Inject


class HomeViewModel(application: Application) : AndroidViewModel(application) {
    @Inject
    lateinit var locationService: LocationService

    @Inject
    lateinit var offerRequestListService: OfferRequestListService

    @Inject
    lateinit var retrofit: Retrofit
    private var service: NetworkApi

    init {
        (application as BillionLightsApplication).getAppComponent().inject(this)
        service = retrofit.create(NetworkApi::class.java)
    }

    fun getLanguageItems(context: Context): MutableLiveData<List<LanguageItem>> {
        val list = MutableLiveData<List<LanguageItem>>()
        val homeItemList = ArrayList<LanguageItem>()
        homeItemList.add(LanguageItem(context.getString(R.string.english), ENGLISH, ENGLISH_CODE))
        homeItemList.add(LanguageItem(context.getString(R.string.hindi), HINDI, HINDI_CODE))
        homeItemList.add(LanguageItem(context.getString(R.string.kannad), KANNAD, KANNAD_CODE))
        homeItemList.add(LanguageItem(context.getString(R.string.tamil), TAMIL, TAMIL_CODE))
        homeItemList.add(LanguageItem(context.getString(R.string.telugu), TELUGU, TELUGU_CODE))
        homeItemList.add(LanguageItem(context.getString(R.string.russian), RUSSIAN, RUSSIAN_CODE))
        //        homeItemList.add(LanguageItem(context.getString(R.string.marathi), MARATHI, MARATHI_CODE))
        //        homeItemList.add(LanguageItem(context.getString(R.string.gujrati), GUJRATI, GUJRATI_CODE))
        list.postValue(homeItemList)
        return list
    }

    fun getAskForHelpItems(context: Context): MutableLiveData<List<OfferHelpItem>> {
        val list = MutableLiveData<List<OfferHelpItem>>()
        val askForHelpItemList = ArrayList<OfferHelpItem>()
        askForHelpItemList.add(OfferHelpItem(context.getString(R.string.food), CATEGORY_FOOD, R.drawable.ic_food))
        askForHelpItemList.add(OfferHelpItem(context.getString(R.string.people), CATEGORY_PEOPLE, R.drawable.ic_group))
        askForHelpItemList.add(OfferHelpItem(context.getString(R.string.shelter), CATEGORY_SHELTER, R.drawable.ic_shelter))
        askForHelpItemList.add(OfferHelpItem(context.getString(R.string.med_ppe), CATEGORY_MED_PPE, R.drawable.ic_mask))
        askForHelpItemList.add(OfferHelpItem(context.getString(R.string.testing), CATEGORY_TESTING, R.drawable.ic_testing))
        askForHelpItemList.add(OfferHelpItem(context.getString(R.string.medicines), CATEGORY_MEDICINES, R.drawable.ic_medicines))
        askForHelpItemList.add(OfferHelpItem(context.getString(R.string.ambulance), CATEGORY_AMBULANCE, R.drawable.ic_ambulance))
        askForHelpItemList.add(OfferHelpItem(context.getString(R.string.medical_equipment), CATEGORY_MEDICAL_EQUIPMENT, R.drawable.ic_medical))
        askForHelpItemList.add(OfferHelpItem(context.getString(R.string.other_things), CATEGORY_OTHERS, R.drawable.ic_other))
        list.postValue(askForHelpItemList)
        return list
    }

    fun getOfferHelpItems(context: Context, radius: Float): MutableLiveData<List<OfferHelpItem>> {
        val list = MutableLiveData<List<OfferHelpItem>>()
        val offerItemList = ArrayList<OfferHelpItem>()

        offerItemList.add(OfferHelpItem(context.getString(R.string.food), CATEGORY_FOOD, R.drawable.ic_food))
        offerItemList.add(OfferHelpItem(context.getString(R.string.people), CATEGORY_PEOPLE, R.drawable.ic_group))
        offerItemList.add(OfferHelpItem(context.getString(R.string.shelter), CATEGORY_SHELTER, R.drawable.ic_shelter))
        offerItemList.add(OfferHelpItem(context.getString(R.string.med_ppe), CATEGORY_MED_PPE, R.drawable.ic_mask))
        offerItemList.add(OfferHelpItem(context.getString(R.string.testing), CATEGORY_TESTING, R.drawable.ic_testing))
        offerItemList.add(OfferHelpItem(context.getString(R.string.medicines), CATEGORY_MEDICINES, R.drawable.ic_medicines))
        offerItemList.add(OfferHelpItem(context.getString(R.string.ambulance), CATEGORY_AMBULANCE, R.drawable.ic_ambulance))
        offerItemList.add(OfferHelpItem(context.getString(R.string.medical_equipment), CATEGORY_MEDICAL_EQUIPMENT, R.drawable.ic_medical))
        offerItemList.add(OfferHelpItem(context.getString(R.string.other_things), CATEGORY_OTHERS, R.drawable.ic_other))

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = locationService.getRequesterSummary(radius)
                if (response.status == 1) {
                    offerItemList.forEach { off ->
                        val item = response.data.find { off.type == it.activity_category }
                        item?.let {
                            off.nearRequest = it.near
                            off.totalRequest = it.total
                        }
                    }
                }
                list.postValue(offerItemList)
            } catch (e: Exception) {
                list.postValue(offerItemList)
            }
        }
        return list
    }

    fun sendUserLocationToServer(radius: Float): MutableLiveData<Pair<LocationSuggestionResponses?, String>> {
        val currentLocationResponse = MutableLiveData<Pair<LocationSuggestionResponses?, String>>()
        GlobalScope.launch(Dispatchers.IO) {
            try {
                currentLocationResponse.postValue(Pair(locationService.getUserCurrentLocationResult(radius), ""))
            } catch (e: Exception) {
                currentLocationResponse.postValue(Pair(null, e.getStringException()))
            }
        }
        return currentLocationResponse
    }

    fun getSuggestion(body: SuggestionRequest, radius: Float): MutableLiveData<Pair<ActivityResponses?, String>> {
        val suggestionResponse = MutableLiveData<Pair<ActivityResponses?, String>>()
        GlobalScope.launch(Dispatchers.IO) {
            try {
                suggestionResponse.postValue(Pair(locationService.getNewSuggestionResult(body, radius), ""))
            } catch (e: Exception) {
                suggestionResponse.postValue(Pair(null, e.getStringException()))
            }
        }
        return suggestionResponse
    }

    fun addActivity(body: AddData, address: String): MutableLiveData<Pair<ActivityResponses?, String>> {
        val addActivityResponse = MutableLiveData<Pair<ActivityResponses?, String>>()
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = locationService.getNewAddActivityResult(body, address)
                addActivityResponse.postValue(Pair(response, ""))
            } catch (e: Exception) {
                addActivityResponse.postValue(Pair(null, e.getStringException()))
            }
        }
        return addActivityResponse
    }

    fun sendEmailToServer(email: String): MutableLiveData<Pair<ServerResponse?, String>> {
        val emailResponse = MutableLiveData<Pair<ServerResponse?, String>>()
        GlobalScope.launch(Dispatchers.IO) {
            try {
                emailResponse.postValue(Pair(offerRequestListService.sendEmailToServer(email), ""))
            } catch (e: Exception) {
                emailResponse.postValue(Pair(null, e.getStringException()))
            }
        }
        return emailResponse
    }


}