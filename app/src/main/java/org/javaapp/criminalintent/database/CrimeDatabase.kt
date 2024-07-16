package org.javaapp.criminalintent.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import org.javaapp.criminalintent.Crime

@Database (entities = [ Crime::class ], version = 2)
@TypeConverters(CrimeTypeConverters::class)
abstract class CrimeDatabase : RoomDatabase() {
    abstract fun crimeDao() : CrimeDao
}

// Migration 객체를 생성해 데이터베이스를 업데이트
val migration_1_2 = object : Migration(1,2) { // (업데이트 전의 데이터베이스 버전, 업데이트할 버전)
    // 인자로 전달된 테이터베이스를 사용해서 테이블을 업그레이드하는 데 필요한 SQL 명령 실행
    override fun migrate(database : SupportSQLiteDatabase) {
        database.execSQL(
            "ALTER TABLE Crime ADD COLUMN suspect TEXT NOT NULL DEFAULT ''" // suspect 열을 Crime 테이블에 추가
        )
    }
}