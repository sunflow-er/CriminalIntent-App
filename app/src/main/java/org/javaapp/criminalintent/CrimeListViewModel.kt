package org.javaapp.criminalintent

import androidx.lifecycle.ViewModel

class CrimeListViewModel : ViewModel() {
    
    val crimes = mutableListOf<Crime>() // Crime 객체들을 담기 위한 리스트

    
    init { // 모의데이터 채우기 
        for (i in 0 until 100) {
            val crime = Crime()
            crime.title = "Crime #$i"
            crime.isSolved = i % 2 == 0
            crimes += crime
        }
    }
}