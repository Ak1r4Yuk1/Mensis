package com.mensis.app.data.db

import androidx.room.TypeConverter
import java.time.LocalDate

class Converters {
    @TypeConverter
    fun fromDate(date: LocalDate?): String? = date?.toString()

    @TypeConverter
    fun toDate(value: String?): LocalDate? = value?.let { LocalDate.parse(it) }
}
