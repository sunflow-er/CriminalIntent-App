package org.javaapp.criminalintent.database

import androidx.room.Database
import androidx.room.RoomDatabase
import org.javaapp.criminalintent.Crime

@Database (entities = [ Crime::class ], version = 1)
abstract class CrimeDatabase : RoomDatabase() {
}