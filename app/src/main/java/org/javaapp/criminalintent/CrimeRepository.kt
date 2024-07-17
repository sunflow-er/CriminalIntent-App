package org.javaapp.criminalintent

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import org.javaapp.criminalintent.database.CrimeDao
import org.javaapp.criminalintent.database.CrimeDatabase
import org.javaapp.criminalintent.database.migration_1_2
import java.io.File
import java.util.UUID
import java.util.concurrent.Executors

private const val DATABASE_NAME = "crime-database"

class CrimeRepository private constructor(context : Context) { // 생성자를 private으로 설정

    // 데이터베이스 객체를 참조하는 속성 추가
    private val database : CrimeDatabase = Room.databaseBuilder(
        context.applicationContext,
        CrimeDatabase::class.java,
        DATABASE_NAME
    ).addMigrations(migration_1_2) // Migration 객체는 데이터베이스를 생성할 때 제공
        .build()

    private val crimeDao : CrimeDao = database.crimeDao() // DAO 객체를 참조하는 속성 추가
    private val executor = Executors.newSingleThreadExecutor() // 새로운 스레드를 참조하는 executor
    private val filesDir = context.applicationContext.filesDir // 파일이 저장되는 디렉터리의 절대 경로

    // DAO의 데이터베이스 액세스 함수들을 사용하기 위한 함수 추가
    fun getCrimes() : LiveData<List<Crime>> = crimeDao.getCrimes()
    fun getCrime(id: UUID) : LiveData<Crime?> = crimeDao.getCrime(id)
    
    // executor(백그라운드 스레드)를 이용하여 데이터 변경 및 추가
    fun updateCrime(crime : Crime) {
        executor.execute {
            crimeDao.updateCrime(crime)
        }
    }
    fun addCrime(crime : Crime) {
        executor.execute {
            crimeDao.addCrime(crime)
        }
    }

    fun getPhotoFile(crime : Crime) : File = File(filesDir, crime.photoFileName) // filesDir 경로 디렉터리에 있는 crime.photoFileName 파일 인스턴스를 생성하여 반환


    companion object {
        private var INSTANCE : CrimeRepository? = null // 인스턴스
        fun inittialize(context: Context) { // 인스턴스를 생성하는 함수
            if (INSTANCE == null) {
                INSTANCE = CrimeRepository(context)
            }
        }

        fun get() : CrimeRepository { // 인스턴스를 반환하는 함수
            return INSTANCE
                ?: throw IllegalStateException("CrimeRepository must be initialized") // 앱이 시작될 때, CrimeRepository 인스턴스를 생성해야 한다.
        }
    }
}