package org.helpinout.billonlights.model.dagger

import dagger.Module
import org.helpinout.billonlights.utils.*

@Module
class PreferencesService {

    var imeiNumber by stringProperty()
    var appVersion by stringProperty()
    var appId by stringProperty(default = "")
    var countryCode by stringProperty()
    var mobileNumber by stringProperty()
    var step by integerProperty()
    var latitude by doubleProperty(default = 0.0)
    var longitude by doubleProperty(default = 0.0)
    var defaultLanguage by stringProperty()
    var zoomLevel by floatProperty(default = 12.05F)

    var offerFirstTime by booleanProperty(default = true)
    var requestFirstTime by booleanProperty(default = true)

    var gpsAccuracy by stringProperty()
    var firebaseId by stringProperty(default = "fghgfghfgdgghghhggghghghghhgggggg")
}