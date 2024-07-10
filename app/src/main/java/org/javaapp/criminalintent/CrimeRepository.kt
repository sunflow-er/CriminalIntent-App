package org.javaapp.criminalintent

import android.content.Context
import androidx.room.Room
import org.javaapp.criminalintent.database.CrimeDao
import org.javaapp.criminalintent.database.CrimeDatabase
import java.util.UUID

private const val DATABASE_NAME = "crime-database"

class CrimeRepository private constructor(context : Context) { // 생성자를 private으로 설정

    // 데이터베이스 객체를 참조하는 속성 추가
    private val database : CrimeDatabase = Room.databaseBuilder(
        context.applicationContext,
        CrimeDatabase::class.java,
        DATABASE_NAME
    ).build()

    // DAO 객체를 참조하는 속성 추가
    private val crimeDao : CrimeDao = database.crimeDao()

    // DAO의 데이터베이스 액세스 함수들을 사용하기 위한 함수 추가
    fun getCrimes() : List<Crime> = crimeDao.getCrimes()
    fun getCrime(id: UUID) : Crime? = crimeDao.getCrime(id)

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