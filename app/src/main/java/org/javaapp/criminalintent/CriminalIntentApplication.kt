package org.javaapp.criminalintent

import android.app.Application

class CriminalIntentApplication : Application() { // Application의 서브 클래스 생성

    override fun onCreate() {
        super.onCreate()
        CrimeRepository.inittialize(this) // 앱이 시작될 때 리포지터리인 CrimeRepository 인스턴스 생성
    }
}