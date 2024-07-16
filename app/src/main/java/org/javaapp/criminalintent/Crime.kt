package org.javaapp.criminalintent

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

@Entity
data class Crime(
    @PrimaryKey val id: UUID = UUID.randomUUID(), // Universally Unique Identifier, 임의의 고유한 ID값을 생성
    var title: String = "",
    var date: Date = Date(), // 현재 일자로 초기화
    var isSolved: Boolean = false,
    var suspect: String = "" // 용의자 이름
)
