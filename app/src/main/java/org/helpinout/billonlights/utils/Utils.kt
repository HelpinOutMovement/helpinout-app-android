package org.helpinout.billonlights.utils

import android.content.Context
import org.helpinout.billonlights.R
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
            return SimpleDateFormat("ZZZZZ", Locale.getDefault()).format(System.currentTimeMillis())
        }

//        fun getDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): String {
//            val km = 1.609344
//            return try {
//                val theta = lon1 - lon2
//                var distance = sin(deg2rad(lat1)) * sin(deg2rad(lat2)) + cos(deg2rad(lat1)) * cos(deg2rad(lat2)) * cos(deg2rad(theta))
//
//                distance = acos(distance)
//                distance = rad2deg(distance)
//                distance *= 60 * 1.1515
//                return String.format("%.1f", distance * km)
//            } catch (e: Exception) {
//                "Unknown"
//            }
//        }

        private fun deg2rad(deg: Double): Double {
            return deg * Math.PI / 180.0
        }

        private fun rad2deg(rad: Double): Double {
            return rad * 180.0 / Math.PI
        }

        fun timeAgo(serverTime: String, context: Context): String {
            try {
                val sourceDateFormat = SimpleDateFormat(SERVER_DATE_FORMAT)
                val time = sourceDateFormat.parse(serverTime).time
                val now = System.currentTimeMillis()
                val diff: Long = now - time

                return if (diff < 24 * HOUR_MILLIS) {
                    context.getString(R.string.today)
                } else if (diff < 48 * HOUR_MILLIS) {
                    context.getString(R.string.yesterday)
                } else {
                    val days = diff / DAY_MILLIS
                    context.getString(R.string.days_ago, days)
                }
            } catch (e: Exception) {
                try {
                    val sourceDateFormat = SimpleDateFormat(TIME_AGO_FORMAT)
                    val time = sourceDateFormat.parse(serverTime).time
                    val now = System.currentTimeMillis()
                    val diff: Long = now - time
                    return if (diff < 24 * HOUR_MILLIS) {
                        context.getString(R.string.today)
                    } else if (diff < 48 * HOUR_MILLIS) {
                        context.getString(R.string.yesterday)
                    } else {
                        val days = diff / DAY_MILLIS
                        context.getString(R.string.days_ago, days)
                    }

                } catch (e: Exception) {
                    return serverTime
                }
            }
        }
    }
}