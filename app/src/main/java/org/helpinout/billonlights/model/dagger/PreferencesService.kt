package org.helpinout.billonlights.model.dagger

import dagger.Module
import org.helpinout.billonlights.utils.doubleProperty
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
    var latitude by doubleProperty(default = 20.5937)
    var longitude by doubleProperty(default = 78.9629)
    var defaultLanguage by stringProperty()

    var gpsAccuracy by stringProperty()
    var firebaseId by stringProperty(default = "fghgfghfgdgghghhggghghghghhgggggg")
}