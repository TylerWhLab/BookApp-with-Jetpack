package com.kjk.booksearchapp.data.db

import androidx.room.TypeConverter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class OrmConverter {
    @TypeConverter
    fun fromList(value: List<String>) = Json.encodeToString(value)
    // List<String>이 입력되면 String으로 인코딩

    @TypeConverter
    fun toList(value: String) = Json.decodeFromString<List<String>>(value)
    // String이 입력되면 List<String>으로 디코딩
}