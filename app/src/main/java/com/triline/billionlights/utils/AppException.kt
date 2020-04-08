package com.triline.billionlights.utils

import com.avneesh.crashreporter.CrashReporter
import com.crashlytics.android.Crashlytics

class AppException(t: Exception) : Exception(t) {
    init {
        CrashReporter.logException(t)
        Crashlytics.logException(t)
    }
}
