package com.triline.billionlights.model.dagger

import com.triline.billionlights.utils.integerProperty
import com.triline.billionlights.utils.stringProperty
import dagger.Module

@Module
class PreferencesService {

    var imeiNumber by stringProperty()
    var appVersion by stringProperty()
    var appId by stringProperty(default = "")
    var password by stringProperty(default = "")
    var countryCode by stringProperty()
    var mobileNumber by stringProperty()
    var step by integerProperty()
    var latitude by stringProperty(default = "0.0f")
    var longitude by stringProperty(default = "0.0f")
    var defaultLanguage by stringProperty()

    var gpsAccuracy by stringProperty()
    var firebaseId by stringProperty(default = "fghgfghfgdgghghhggghghghghhgggggg")
}