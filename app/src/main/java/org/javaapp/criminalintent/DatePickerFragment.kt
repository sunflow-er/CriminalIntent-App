package org.javaapp.criminalintent

import android.app.DatePickerDialog
import android.app.Dialog
import android.icu.util.Calendar
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.util.Date

private const val ARG_DATE = "date"

class DatePickerFragment : DialogFragment() {

    // DialogFragment를 보여주려고 호스팅 액티비티의 FragmentManager가 이 함수를 호출
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar = Calendar.getInstance()
        val initialYear = calendar.get(Calendar.YEAR)
        val initialMonth = calendar.get(Calendar.MONTH)
        val initialDay = calendar.get(Calendar.DAY_OF_MONTH)

        return DatePickerDialog(requireContext(), null, initialYear, initialMonth, initialDay)
    }

    companion object {
        fun newInstance(date : Date) : DatePickerFragment {
            val args = Bundle().apply {
                putSerializable(ARG_DATE, date)
            }

            return DatePickerFragment().apply {
                arguments = args
            }
        }
    }
}