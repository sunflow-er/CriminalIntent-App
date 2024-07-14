package org.javaapp.criminalintent

import android.app.DatePickerDialog
import android.app.Dialog
import android.icu.util.Calendar
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.util.Date
import java.util.GregorianCalendar

private const val ARG_DATE = "date"

class DatePickerFragment : DialogFragment() {

    // 콜백 인터페이스
    interface Callbacks {
        fun onDateSelected(date: Date)
    }

    // DialogFragment를 보여주려고 호스팅 액티비티의 FragmentManager가 이 함수를 호출
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // DatePickerDialog 리스너 설정
        val dateListener =
            DatePickerDialog.OnDateSetListener { _: DatePicker, year: Int, month: Int, day: Int ->
                val resultDate : Date = GregorianCalendar(year, month, day).time // .time : 캘린더 정보에 해당되는 Date 객체 반환

                // 콜백 함수 호출
                targetFragment?.let { fragment ->
                    (fragment as Callbacks).onDateSelected(resultDate)
                }
            }


        val date = arguments?.getSerializable(ARG_DATE) as Date // 프래그먼트 인자로부터 Date 객체 정보 가져오기

        /*
        Date 객체는 타임스탬프 형식이므로 월, 일, 년 값을 바로 추출할 수 없다.
        따라서 Date 객체를 사용해서 Calendar 객체를 생성 후, 이 Calendar 객체로부터 필요한 값을 추출한다.
         */
        val calendar = Calendar.getInstance()
        calendar.time = date // calendar의 현재 시간을 주어진 date 객체로 맞춘다.


        val initialYear = calendar.get(Calendar.YEAR)
        val initialMonth = calendar.get(Calendar.MONTH)
        val initialDay = calendar.get(Calendar.DAY_OF_MONTH)

        return DatePickerDialog(requireContext(), dateListener, initialYear, initialMonth, initialDay)
    }

    companion object {
        fun newInstance(date: Date): DatePickerFragment {
            val args = Bundle().apply {
                putSerializable(ARG_DATE, date)
            }

            return DatePickerFragment().apply {
                arguments = args
            }
        }
    }
}