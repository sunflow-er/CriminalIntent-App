package org.javaapp.criminalintent

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point

// 파일 수준 함수 : 코틀린 파일 내에서 클래스 외부에 정의된 함수이며, 앱의 어떤 코드에서도 사용 가능함
fun getScaledBitmap(path : String, destWidth : Int, destHeight : Int) : Bitmap {
    // 1. 이미지 파일의 크기를 읽는다.
    var options = BitmapFactory.Options() // 비트맵을 디코드할 때 사용할 다양한 설정 포함
    options.inJustDecodeBounds = true // 비트맵을 메모리에 로드하지 않고, 이미지의 원본 크기만을 읽어온다. (메모리 사용을 최소화하면서 이미지의 크기를 알아내기 위해)
    BitmapFactory.decodeFile(path, options) // 지정된 파일 경로(path)에서 이미지 파일을 디코드

    val srcWidth = options.outWidth.toFloat() // 디코드 과정에서 얻은 이미지의 원본 너비
    val srcHeight = options.outHeight.toFloat() // 이미지의 원본 높이
    
    // 2. 크기를 얼마나 줄일지 파악
    var inSampleSize = 1 // 샘플링 비율, 1 (이미지의 크기를 변경하지 않음)
    if (srcHeight > destHeight || srcWidth > destWidth) { // 원본이 목표 크기보다 큰 경우
        // 스케일 계산
        val heightScale = srcHeight / destHeight
        val widthScale = srcWidth / destWidth

        val sampleScale = if (heightScale > widthScale) { // 높이와 너비 스케일 중 더 큰 값을 샘플링 스케일로 선택
            heightScale
        } else {
            widthScale
        }

        inSampleSize = Math.round(sampleScale) // 샘플링 스케일을 반올림하여 정수값으로 만든다.
    }

    options = BitmapFactory.Options() // 새로운 비트맵 옵션 객체 생성
    options.inSampleSize = inSampleSize // 샘플링 스케일 할당, 원본 이미지의 크기를 얼마나 줄일지 결정
    // 예를 들어, inSampleSize가 2이면, 이미지의 너비와 높이가 각각 원본의 1/2로 줄어든다.

    // 3. 최종 Bitmap을 생성한다.
    return BitmapFactory.decodeFile(path, options)
}

// 액티비티의 화면 크기에 맞춰 Bitmap의 크기 조정
fun getScaledBitmap(path : String, activity : Activity) : Bitmap {
    val size = Point() // 포인트 객체 : 화면의 너비와 높이를 저장하는 데 사용

    @Suppress("DEPRECATION")
    activity.windowManager.defaultDisplay.getSize(size) // 현재 액티비티의 화면 크기를 가져와 size 객체에 저장

    return getScaledBitmap(path, size.x, size.y)
}