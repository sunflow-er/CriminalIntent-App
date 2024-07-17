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
) {
    val photoFileName // 파일 이름을 얻는 연산 속성(computed property) 추가
        get() = "IMG_$id.jpg" // 연산 속성은 다른 속성의 값으로 자신의 값을 산출하므로 값을 저장하는 필드를 갖지 않는다.
}
