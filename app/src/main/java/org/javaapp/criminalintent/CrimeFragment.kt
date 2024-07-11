package org.javaapp.criminalintent

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.fragment.app.Fragment
import java.util.UUID

private const val ARG_CRIME_ID = "crime_id" // 인자를 번들에 저장할 때 사용하는 키의 문자열 상수

class CrimeFragment : Fragment() {
    private lateinit var crime : Crime

    private lateinit var titleField: EditText
    private lateinit var dateButton : Button
    private lateinit var solvedCheckBox : CheckBox

    override fun onCreate(savedInstanceState: Bundle?) { // public, 프래그먼트를 호스팅하는 어떤 액티비티에서도 자동 호출될 것이므로 public이어야만 함
        super.onCreate(savedInstanceState)

        /*
        Fragment.onCreate(Bundle?)에서는 프래그먼트의 뷰를 인플레이트하지 않음
        프래그먼트 인스턴스는 onCreate에서 구성하지만, 프래그먼트의 뷰는 또 다른 생명주기 함수인 onCreateView에서 생성하고 구성한다.
         */

        crime = Crime()

        
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crime, container, false)

        // 참조값 가져오기
        titleField = view.findViewById(R.id.crime_title) as EditText
        dateButton = view.findViewById(R.id.crime_date) as Button
        dateButton.apply {
            text = crime.date.toString()
            isEnabled = false // 버튼 비활성화
        }
        solvedCheckBox = view.findViewById(R.id.crime_solved) as CheckBox

        return view
    }

    override fun onStart() {
        super.onStart()

        // titleField 리스너 설정
        val titleWatcher = object: TextWatcher { // TextWatcher 인터페이스를 구현하는 익명 클래스 생성
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { // s : 사용자가 입력한 데이터 값을 가지고 있음
                crime.title = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {

            }
        }
        titleField.addTextChangedListener(titleWatcher)

        // solvedCheckBox 리스너 설정
        solvedCheckBox.apply { 
            setOnCheckedChangeListener { _, isChecked ->
                crime.isSolved = isChecked
            }
        }
    }

    companion object {
        fun newInstance(crimeId : UUID) : CrimeFragment {
            val args = Bundle().apply { // 인자 번들 생성
                putSerializable(ARG_CRIME_ID, crimeId) // 인자 저장
            }
            
            return CrimeFragment().apply { // 프래그먼트 인스턴스 생성
                arguments = args // 인자 번들 첨부
            }
        }
    }
}