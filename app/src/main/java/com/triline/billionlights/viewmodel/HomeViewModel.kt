package com.triline.billionlights.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.triline.billionlights.R
import com.triline.billionlights.model.BillionLightsApplication
import com.triline.billionlights.model.database.entity.*
import com.triline.billionlights.model.retrofit.NetworkApi
import com.triline.billionlights.service.LocationService
import com.triline.billionlights.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import javax.inject.Inject


class HomeViewModel(application: Application) : AndroidViewModel(application) {
    @Inject
    lateinit var locationService: LocationService

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
        homeItemList.add(LanguageItem(context.getString(R.string.marathi), MARATHI, MARATHI_CODE))
        homeItemList.add(LanguageItem(context.getString(R.string.gujrati), GUJRATI, GUJRATI_CODE))

        list.postValue(homeItemList)
        return list
    }

    fun getAskForHelpItems(context: Context): MutableLiveData<List<AskForHelpItem>> {
        val list = MutableLiveData<List<AskForHelpItem>>()
        val askForHelpItemList = ArrayList<AskForHelpItem>()
        askForHelpItemList.add(AskForHelpItem(context.getString(R.string.food), FOOD, R.drawable.ic_food))
        askForHelpItemList.add(AskForHelpItem(context.getString(R.string.people), PEOPLE, R.drawable.ic_group))
        askForHelpItemList.add(AskForHelpItem(context.getString(R.string.shelter), SHELTER, R.drawable.ic_shelter))
        askForHelpItemList.add(AskForHelpItem(context.getString(R.string.med_ppe), MED_PPE, R.drawable.ic_mask))
        askForHelpItemList.add(AskForHelpItem(context.getString(R.string.testing), TESTING, R.drawable.ic_testing))
        askForHelpItemList.add(AskForHelpItem(context.getString(R.string.medicines), MEDICINES, R.drawable.ic_medicines))
        askForHelpItemList.add(AskForHelpItem(context.getString(R.string.ambulance), AMBULANCE, R.drawable.ic_ambulance))
        askForHelpItemList.add(AskForHelpItem(context.getString(R.string.medical_equipment), MEDICAL_EQUIPMENT, R.drawable.ic_medical))
        askForHelpItemList.add(AskForHelpItem(context.getString(R.string.other_things), OTHER_THINGS, R.drawable.ic_other))

        list.postValue(askForHelpItemList)
        return list
    }

    fun getOfferHelpItems(context: Context): MutableLiveData<List<OfferHelpItem>> {
        val list = MutableLiveData<List<OfferHelpItem>>()
        val offerItemList = ArrayList<OfferHelpItem>()
        offerItemList.add(OfferHelpItem(context.getString(R.string.food), FOOD, R.drawable.ic_food, 24, 2300))
        offerItemList.add(OfferHelpItem(context.getString(R.string.people), PEOPLE, R.drawable.ic_group, 43, 2100))
        offerItemList.add(OfferHelpItem(context.getString(R.string.shelter), SHELTER, R.drawable.ic_shelter, 13, 1100))
        offerItemList.add(OfferHelpItem(context.getString(R.string.med_ppe), MED_PPE, R.drawable.ic_mask, 5, 520))
        offerItemList.add(OfferHelpItem(context.getString(R.string.testing), TESTING, R.drawable.ic_testing, 12, 543))
        offerItemList.add(OfferHelpItem(context.getString(R.string.medicines), MEDICINES, R.drawable.ic_medicines, 10, 245))
        offerItemList.add(OfferHelpItem(context.getString(R.string.ambulance), AMBULANCE, R.drawable.ic_ambulance, 345, 4201))
        offerItemList.add(OfferHelpItem(context.getString(R.string.medical_equipment), MEDICAL_EQUIPMENT, R.drawable.ic_medical, 43, 5430))
        offerItemList.add(OfferHelpItem(context.getString(R.string.other_things), OTHER_THINGS, R.drawable.ic_other, 34, 5322))

        list.postValue(offerItemList)
        return list
    }

    fun sendUserLocationToServer(): MutableLiveData<Pair<String?, String>> {
        val currentLocationResponse = MutableLiveData<Pair<String?, String>>()
        GlobalScope.launch(Dispatchers.IO) {
            try {
                currentLocationResponse.postValue(Pair(locationService.getUserCurrentLocationResult(), ""))
            } catch (e: Exception) {
                currentLocationResponse.postValue(Pair(null, e.getStringException()))
            }
        }
        return currentLocationResponse
    }

    fun getSuggestion(body: SuggestionData): MutableLiveData<Pair<String?, String>> {
        val suggestionResponse = MutableLiveData<Pair<String?, String>>()
        GlobalScope.launch(Dispatchers.IO) {
            try {
                suggestionResponse.postValue(Pair(locationService.getNewSuggestionResult(body), ""))
            } catch (e: Exception) {
                suggestionResponse.postValue(Pair(null, e.getStringException()))
            }
        }
        return suggestionResponse
    }

    fun addActivity(body: AddData): MutableLiveData<Pair<String?, String?>> {
        val addActivityResponse = MutableLiveData<Pair<String?, String?>>()
        GlobalScope.launch(Dispatchers.IO) {
            try {
                addActivityResponse.postValue(Pair(locationService.getNewAddActivityResult(body), ""))
            } catch (e: Exception) {
                addActivityResponse.postValue(Pair(null, e.getStringException()))
            }
        }

        return addActivityResponse
    }


    fun getNearestDestination(url: String): MutableLiveData<String>? {
        val mResponse: MutableLiveData<String> = MutableLiveData()
        service.getSearchResult(url).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>?, response: Response<String>?) {
                if (response?.code() == 200) {
                    val body = response.body()
                    mResponse.postValue(body)
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
            }
        })
        return mResponse
    }

    fun parseDestination(json: JSONObject): MutableLiveData<java.util.ArrayList<PlaceData>>? {
        val mNearestResponse = MutableLiveData<java.util.ArrayList<PlaceData>>()
        try {
            val results = json.getJSONArray("results")
            val arrayList = java.util.ArrayList<PlaceData>()
            for (i in 0 until results.length()) {
                val jObj = results.getJSONObject(i)
                val data = PlaceData()
                if (jObj.has("name")) data.name = jObj.getString("name")
                if (jObj.has("vicinity")) data.location = jObj.getString("vicinity")
                if (jObj.has("geometry")) {
                    data.latitude = jObj.getJSONObject("geometry").getJSONObject("location").getDouble("lat")
                    data.longnitude = jObj.getJSONObject("geometry").getJSONObject("location").getDouble("lng")
                }
                arrayList.add(data)
            }
            mNearestResponse.postValue(arrayList)
        } catch (e: JSONException) {
        }
        return mNearestResponse
    }

}