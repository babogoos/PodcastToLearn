package com.fabirt.podcastapp.data.database.typeconverter

import androidx.room.TypeConverter
import com.google.gson.Gson

/**
 * Created by dion on 2023/05/14.
 */
class Converters {

    @TypeConverter
    fun listToJson(value: List<String>?) = Gson().toJson(value)

    @TypeConverter
    fun jsonToList(value: String) = Gson().fromJson(value, Array<String>::class.java).toList()
}