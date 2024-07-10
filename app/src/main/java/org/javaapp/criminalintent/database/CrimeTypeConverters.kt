package org.javaapp.criminalintent.database

import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.util.Date
import java.util.UUID

class CrimeTypeConverters {
    // Date 타입의 변환 처리
    @TypeConverter
    fun fromDate(date : Date?) : Long? {
        return date?.time
    }
    @TypeConverter
    fun toDate(millisSinceEpoch: Long?) : Date? {
        return millisSinceEpoch?.let {
            Date(it)
        }
    }

    // UUID 타입의 변환 처리
    @TypeConverter
    fun fromUUID(uuid : UUID?) : String? {
        return uuid?.toString()
    }
    @TypeConverter
    fun toUUID(uuid : String?) : UUID? {
        return UUID.fromString(uuid)
    }
}