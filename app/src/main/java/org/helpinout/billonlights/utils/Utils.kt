package org.helpinout.billonlights.utils

import androidx.exifinterface.media.ExifInterface
import java.io.IOException
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

        fun setGeoTag(locLatitude: Double, locLongitude: Double, image: String) {
            try {
                val exifInterface = ExifInterface(image)

                val latitude = Math.abs(locLatitude)
                val longitude = Math.abs(locLongitude)

                val num1Lat = Math.floor(latitude).toInt()
                val num2Lat = Math.floor((latitude - num1Lat) * 60).toInt()
                val num3Lat = (latitude - (num1Lat.toDouble() + num2Lat.toDouble() / 60)) * 3600000

                val num1Lon = Math.floor(longitude).toInt()
                val num2Lon = Math.floor((longitude - num1Lon) * 60).toInt()
                val num3Lon = (longitude - (num1Lon.toDouble() + num2Lon.toDouble() / 60)) * 3600000

                val lat = "$num1Lat/1,$num2Lat/1,$num3Lat/1000"
                val lon = "$num1Lon/1,$num2Lon/1,$num3Lon/1000"


                exifInterface.setAttribute(ExifInterface.TAG_GPS_LATITUDE, lat)
                exifInterface.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, lon)
                exifInterface.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, if (locLatitude > 0) "N" else "S")
                exifInterface.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, if (locLongitude > 0) "E" else "W")
                exifInterface.saveAttributes()

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

    }
}