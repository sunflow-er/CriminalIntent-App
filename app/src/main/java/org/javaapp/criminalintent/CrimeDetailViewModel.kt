package org.javaapp.criminalintent

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import java.io.File
import java.util.UUID


class CrimeDetailViewModel : ViewModel() {
    private val crimeRepository = CrimeRepository.get() // 리포지토리
    private val crimeIdLiveData = MutableLiveData<UUID>() // CrimeFragment가 화면에 보여준/보여줄 범죄 데이터 ID를 저장한 LiveData 참조
    

    var crimeLiveData : LiveData<Crime?> = // 상세 내역 화면에 보여줄 Crime 객체를 저장한 LiveData 참조
        // CrimeIdLiveData가 변경될 때마다 해당 crimeId에 해당하는 Crime LiveData를 반환하고 이를 관찰한다. (이전 LiveData의 관찰은 중단)
        crimeIdLiveData.switchMap { crimeId ->
        crimeRepository.getCrime(crimeId)
        }

    fun loadCrime(crimeId : UUID) { // CrimeFragment가 생성될 때 onCreate()에서 호출
        crimeIdLiveData.value = crimeId
    }
    
    fun saveCrime(crime : Crime) { // 인자로 받은 Crime 객체를 데이터베이스에 변경
        crimeRepository.updateCrime(crime)
    }

    fun getPhotoFile(crime : Crime) : File {
        return crimeRepository.getPhotoFile(crime)
    }

}