package org.helpinout.billonlights.utils

import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin


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

        fun getDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): String {
            val km = 1.609344
            return try {
                val theta = lon1 - lon2
                var distance = sin(deg2rad(lat1)) * sin(deg2rad(lat2)) + cos(deg2rad(lat1)) * cos(deg2rad(lat2)) * cos(deg2rad(theta))

                distance = acos(distance)
                distance = rad2deg(distance)
                distance *= 60 * 1.1515
                return String.format("%.2f", distance * km)
            } catch (e: Exception) {
                "Unknown"
            }
        }

        private fun deg2rad(deg: Double): Double {
            return deg * Math.PI / 180.0
        }

        private fun rad2deg(rad: Double): Double {
            return rad * 180.0 / Math.PI
        }
    }
}