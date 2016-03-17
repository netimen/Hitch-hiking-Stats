package ru.netimen.hitch_hikingstats

import java.util.*
import kotlin.properties.Delegates
import kotlin.properties.ObservableProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Copyright (c) 2016 Bookmate.
 * All Rights Reserved.
 *
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   17.03.16
 */

fun <T, R> T?.onNull(blockIfNull: () -> R) = this?.let {} ?: blockIfNull

fun String?.notEmpty() = if (isNullOrEmpty()) null else this


open class CountedObservableProperty<T>(initialValue: T) : ObservableProperty<T>(initialValue) {
    var setCount = 0
        private set

    override fun afterChange(property: KProperty<*>, oldValue: T, newValue: T) {
        afterChange(property, oldValue, newValue, setCount++)
    }

    protected open fun afterChange (property: KProperty<*>, oldValue: T, newValue: T, setCount: Int): Unit {}
}

inline fun <T> Delegates.countedObservable(initialValue: T, crossinline onChange: (property: KProperty<*>, oldValue: T, newValue: T, setCount: Int) -> Unit):
        ReadWriteProperty<Any?, T> = object : CountedObservableProperty<T>(initialValue) {
    override fun afterChange(property: KProperty<*>, oldValue: T, newValue: T, setCount: Int) = onChange(property, oldValue, newValue, setCount)
}

class AddFieldDelegate<T, I>(private val defaultValue: I) {
    private val fieldMap = HashMap<T, I>()

    operator fun getValue(t: T, property: KProperty<*>) = fieldMap[t]?.apply { } ?: defaultValue

    operator fun setValue(t: T, property: KProperty<*>, any: I) = fieldMap.put(t, any)
}

