package com.triline.billionlights.utils

import com.orhanobut.hawk.Hawk
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


fun booleanProperty(key: String? = null, default: Boolean = false) =
    object : ReadWriteProperty<Any, Boolean> {

        override fun getValue(thisRef: Any, property: KProperty<*>): Boolean {
            return Hawk.get(key ?: property.name, default)
        }

        override fun setValue(thisRef: Any, property: KProperty<*>, value: Boolean) {
            Hawk.put(key ?: property.name, value)
        }
    }

fun stringProperty(key: String? = null, default: String = "") =
    object : ReadWriteProperty<Any, String> {

        override fun getValue(thisRef: Any, property: KProperty<*>): String {
            return Hawk.get(key ?: property.name, default)
        }

        override fun setValue(thisRef: Any, property: KProperty<*>, value: String) {
            Hawk.put(key ?: property.name, value)
        }
    }

fun integerProperty(key: String? = null, default: Int = 0) =
    object : ReadWriteProperty<Any, Int> {

        override fun getValue(thisRef: Any, property: KProperty<*>): Int {
            return Hawk.get(key ?: property.name, default)
        }

        override fun setValue(thisRef: Any, property: KProperty<*>, value: Int) {
            Hawk.put(key ?: property.name, value)
        }
    }

fun longProperty(key: String? = null, default: Long = 0) =
    object : ReadWriteProperty<Any, Long> {

        override fun getValue(thisRef: Any, property: KProperty<*>): Long {
            return Hawk.get(key ?: property.name, default)
        }

        override fun setValue(thisRef: Any, property: KProperty<*>, value: Long) {
            Hawk.put(key ?: property.name, value)
        }
    }

fun floatProperty(key: String? = null, default: Float = 0.0f) =
    object : ReadWriteProperty<Any, Float> {

        override fun getValue(thisRef: Any, property: KProperty<*>): Float {
            return Hawk.get(key ?: property.name, default)
        }

        override fun setValue(thisRef: Any, property: KProperty<*>, value: Float) {
            Hawk.put(key ?: property.name, value)
        }
    }

fun doubleProperty(key: String? = null, default: Double = 0.0) =
    object : ReadWriteProperty<Any, Double> {

        override fun getValue(thisRef: Any, property: KProperty<*>): Double {
            return Hawk.get(key ?: property.name, default)
        }

        override fun setValue(thisRef: Any, property: KProperty<*>, value: Double) {
            Hawk.put(key ?: property.name, value)
        }
    }