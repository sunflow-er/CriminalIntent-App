package org.javaapp.criminalintent.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import org.javaapp.criminalintent.Crime
import java.util.UUID

@Dao
interface CrimeDao {
    // 데이터베이스의 모든 범죄 데이터를 반환하는 함수
    @Query("SELECT * FROM crime")
    fun getCrimes() : LiveData<List<Crime>> // 원래의 반환 타입을 포함하는 LiveData 객체 반환

    // 지정된 UUID를 갖는 하나의 범죄 데이터를 반환하는 함수
    @Query("SELECT * FROM crime WHERE id=(:id)")
    fun getCrime(id: UUID) : LiveData<Crime?>
}