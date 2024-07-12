package org.javaapp.criminalintent

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import java.util.UUID

private const val TAG = "CrimeFragment"
private const val ARG_CRIME_ID = "crime_id" // 인자를 번들에 저장할 때 사용하는 키의 문자열 상수

class CrimeFragment : Fragment() {
    private lateinit var crime : Crime

    private lateinit var titleField: EditText
    private lateinit var dateButton : Button
    private lateinit var solvedCheckBox : CheckBox

    private  val crimeDetailViewModel : CrimeDetailViewModel by lazy {  // 뷰모델
        ViewModelProvider(this).get(CrimeDetailViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) { // public, 프래그먼트를 호스팅하는 어떤 액티비티에서도 자동 호출될 것이므로 public이어야만 함
        super.onCreate(savedInstanceState)

        /*
        Fragment.onCreate(Bundle?)에서는 프래그먼트의 뷰를 인플레이트하지 않음
        프래그먼트 인스턴스는 onCreate에서 구성하지만, 프래그먼트의 뷰는 또 다른 생명주기 함수인 onCreateView에서 생성하고 구성한다.
         */

        crime = Crime()

        val crimeId : UUID = arguments?.getSerializable(ARG_CRIME_ID) as UUID // 프래그먼트 인자 가져오기
        crimeDetailViewModel.loadCrime(crimeId) // CrimeFragment를 CrimeDetailViewModel과 연결
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) { // 프래그먼트에 다시 돌아오면 관찰을 재개
        super.onViewCreated(view, savedInstanceState)
        
        crimeDetailViewModel.crimeLiveData.observe( // CrimeDetailViewModel의 crimeLiveData가 변경되는지 관찰
            viewLifecycleOwner, // 프래그먼트 뷰의 생명주기에 맞춰서 사용자가 프래그먼트를 떠나면 자동으로 관찰을 중지
            Observer { crime -> // crime : 데이터베이스에 현재 저장된 것을 나타낸다.
                crime?.let { // 새 데이터가 있으면
                    this.crime = crime // this.crime : 프래그먼트가 화면에 나타내는 데이터
                    updateUI() // UI 변경
                }
            }
        )
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

    private fun updateUI() {
        titleField.setText(crime.title)
        dateButton.text = crime.date.toString()
        solvedCheckBox.isChecked = crime.isSolved
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