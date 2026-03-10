package com.obsidian.aegis.db

import androidx.room.TypeConverter
import com.obsidian.aegis.models.IndicatorType

class Converters {

    @TypeConverter
    fun fromIndicatorType(indicatorType: IndicatorType): String {
        return indicatorType.name
    }

    @TypeConverter
    fun toIndicatorType(indicatorType: String): IndicatorType {
        return IndicatorType.valueOf(indicatorType)
    }
}