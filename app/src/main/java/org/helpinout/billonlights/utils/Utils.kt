package org.helpinout.billonlights.utils

import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by Avneesh on 3/25/2018.
 */
class Utils {

    companion object {

        fun currentDateTime(): String {
            val msTime = System.currentTimeMillis()
            val curDateTime = Date(msTime)
            val formatter = SimpleDateFormat(SERVER_DATE_FORMAT)
            return formatter.format(curDateTime)
        }

        fun getTimeZoneString(): String {
            return TimeZone.getDefault().id
        }

        fun getTimeZone(): String {
            return SimpleDateFormat("XXX", Locale.getDefault()).format(System.currentTimeMillis())
        }
    }
}