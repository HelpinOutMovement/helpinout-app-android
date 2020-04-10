package org.helpinout.billonlights.model.dagger

import dagger.Module
import org.helpinout.billonlights.utils.integerProperty
import org.helpinout.billonlights.utils.stringProperty

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