package org.javaapp.criminalintent

import android.app.Activity
import android.app.DatePickerDialog
import android.app.ProgressDialog.show
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.provider.Settings.System.DATE_FORMAT
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.DatePicker
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import java.util.Date
import java.util.UUID
import android.text.format.DateFormat
import android.widget.ImageButton
import android.widget.ImageView
import androidx.core.content.FileProvider
import androidx.room.util.query
import java.io.File

private const val TAG = "CrimeFragment"
private const val ARG_CRIME_ID = "crime_id" // 인자를 번들에 저장할 때 사용하는 키의 문자열 상수
private const val DIALOG_DATE = "DialogDate" // DatePickerFragment 태그 상수
private const val REQUEST_DATE = 0 // 대상 프래그먼트(target fragment) 요청 코드
private const val REQUEST_CONTACT = 1
private const val REQUEST_PHOTO = 2
private const val DATE_FORMAT = "yyyy년 M월 d일 H시 m분, E요일"

class CrimeFragment : Fragment(), DatePickerFragment.Callbacks {
    private lateinit var crime: Crime
    private lateinit var photoFile : File // 사진 파일의 위치
    private lateinit var photoUri : Uri // FileProvider에 의해 서비스되는 위치

    private lateinit var titleField: EditText
    private lateinit var dateButton: Button
    private lateinit var solvedCheckBox: CheckBox
    private lateinit var reportButton: Button
    private lateinit var suspectButton: Button
    private lateinit var photoButton: ImageButton
    private lateinit var photoView: ImageView

    private val crimeDetailViewModel: CrimeDetailViewModel by lazy {  // 뷰모델
        ViewModelProvider(this).get(CrimeDetailViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) { // public, 프래그먼트를 호스팅하는 어떤 액티비티에서도 자동 호출될 것이므로 public이어야만 함
        super.onCreate(savedInstanceState)

        /*
        Fragment.onCreate(Bundle?)에서는 프래그먼트의 뷰를 인플레이트하지 않음
        프래그먼트 인스턴스는 onCreate에서 구성하지만, 프래그먼트의 뷰는 또 다른 생명주기 함수인 onCreateView에서 생성하고 구성한다.
         */

        crime = Crime()

        val crimeId: UUID = arguments?.getSerializable(ARG_CRIME_ID) as UUID // 프래그먼트 인자 가져오기
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
        solvedCheckBox = view.findViewById(R.id.crime_solved) as CheckBox
        reportButton = view.findViewById(R.id.crime_report) as Button
        suspectButton = view.findViewById(R.id.crime_suspect) as Button
        photoButton = view.findViewById(R.id.crime_camera) as ImageButton
        photoView = view.findViewById(R.id.crime_photo) as ImageView

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) { // 프래그먼트에 다시 돌아오면 관찰을 재개
        super.onViewCreated(view, savedInstanceState)

        crimeDetailViewModel.crimeLiveData.observe( // CrimeDetailViewModel의 crimeLiveData가 변경되는지 관찰
            viewLifecycleOwner, // 프래그먼트 뷰의 생명주기에 맞춰서 사용자가 프래그먼트를 떠나면 자동으로 관찰을 중지
            Observer { crime -> // crime : 데이터베이스에 현재 저장된 것을 나타낸다.
                crime?.let { // 새 데이터가 있으면
                    this.crime = crime // this.crime : 프래그먼트가 화면에 나타내는 데이터
                    photoFile = crimeDetailViewModel.getPhotoFile(crime)
                    photoUri = FileProvider.getUriForFile(requireActivity(), "org.javaapp.criminalintent.fileprovider", photoFile) // FileProvider를 통해 사진 파일(photoFile)에 대한 URI 값 얻기
                    updateUI() // UI 변경
                }
            }
        )
    }

    override fun onStart() {
        super.onStart()

        // titleField 리스너 설정
        val titleWatcher = object : TextWatcher { // TextWatcher 인터페이스를 구현하는 익명 클래스 생성
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) { // s : 사용자가 입력한 데이터 값을 가지고 있음
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

        // DateButton 리스너 설정
        dateButton.setOnClickListener {
            // DatePickerFragment 보여주기
            DatePickerFragment.newInstance(crime.date).apply {
                setTargetFragment(this@CrimeFragment, REQUEST_DATE)
                show(
                    this@CrimeFragment.parentFragmentManager,
                    DIALOG_DATE
                ) // show(호스팅 액비티티 프래그먼트 매니저, 프래그먼트 식별 상수)
            }
        }

        // reportButton 리스너 설정
        reportButton.setOnClickListener {
            // 범죄 보고서를 발송
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getCrimeReport()) // 보고서의 텍스트
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject)) // 보고서의 제목
            }.also { intent ->
                val chooserIntent = Intent.createChooser(intent, getString(R.string.send_report))
                startActivity(chooserIntent)
            }
        }

        // suspectButton 리스너 설정
        suspectButton.apply {
            val pickContactIntent =
                Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)

            setOnClickListener {
                startActivityForResult(pickContactIntent, REQUEST_CONTACT)
            }

            // 암시적 인텐트에 해당하는 액티비티(연락처 앱)를 찾을 수 없을 경우
            val packageManager: PackageManager = requireActivity().packageManager // 안드로이드 장치에 설치된 모든 컴포넌트와 이것들의 모든 액티비티를 알고 있음
            val resolvedActivity: ResolveInfo? = packageManager.resolveActivity( // 액티비티를 찾는다. startActivity(Intent)와 비슷
                pickContactIntent, // 인텐트
                PackageManager.MATCH_DEFAULT_ONLY // 플래그 : 해당 인텐트를 처리할 수 있는 액티비티들 중에서 사용자가 기본(MATCH_DEFAULT_ONLY)으로 설정한 앱이나 시스템이 기본으로 인식하는 앱으로 범위를 제한
            )  // 액티비티가 있으면 이것들의 정보를 갖는 ResolveInfo 인스턴스 반환, 없으면 null 반환
            if (resolvedActivity == null) { // 액티비티가 없을 경우
                isEnabled = false // 버튼 비활성화
            }
        }

        photoButton.apply {
            val packageManager : PackageManager = requireActivity().packageManager

            val captureImage = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val resolvedActivity : ResolveInfo? = packageManager.resolveActivity(captureImage, PackageManager.MATCH_DEFAULT_ONLY)

            if (resolvedActivity == null) {
                isEnabled == false
            }

            setOnClickListener {
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, photoUri) // 사진 파일을 저장할 위치를 가리키는 Uri를 값으로 전달

                val cameraActivities : List<ResolveInfo> = packageManager.queryIntentActivities(captureImage,PackageManager.MATCH_DEFAULT_ONLY)

                for (cameraActivity in cameraActivities) {
                    // photoUri가 가리키는 위치에 카메라 앱 엑세스 퍼미션
                    requireActivity().grantUriPermission(
                        cameraActivity.activityInfo.packageName, // Uri에 대한 접근을 허용할 패키지
                        photoUri, // Uri
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION // 접근 모드
                    )
                }

                startActivityForResult(captureImage, REQUEST_PHOTO)
            }
        }

    }

    override fun onStop() { // 프래그먼트가 중단 상태(프래그먼트 화면 전체가 안보이는 상태)일 때 호출
        super.onStop()
        crimeDetailViewModel.saveCrime(crime)
    }

    // DatePickerFragment 콜백 인터페이스 구현
    override fun onDateSelected(date: Date) {
        crime.date = date
        updateUI()
    }

    override fun onDetach() { // 부적합한 응답이 생길 가능성에 대비
        super.onDetach()
        requireActivity().revokeUriPermission(photoUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION) // Uri에 파일을 쓸 수 있는 퍼미션 취소
    }

    private fun updateUI() {
        titleField.setText(crime.title)
        dateButton.text = crime.date.toString()
        solvedCheckBox.apply {
            isChecked = crime.isSolved
        }

        if (crime.suspect.isNotEmpty()) { // 용의자가 선정되었을 때
            suspectButton.text = crime.suspect // suspectButton에 텍스트 설정
        }

        updatePhotoView()
    }

    // ImageView에 Bitmap 업로드
    private fun updatePhotoView() {
        if (photoFile.exists()) {
            val bitmap = getScaledBitmap(photoFile.path, requireActivity())
            photoView.setImageBitmap(bitmap)
        } else {
            photoView.setImageDrawable(null)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when {
            resultCode != Activity.RESULT_OK -> return

            requestCode == REQUEST_CONTACT && data != null -> {
                val contactUri: Uri = data.data ?: return // URI 정보

                // 쿼리에서 값으로 반환할 필드를 지정
                val queryFields = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)

                // 쿼리를 수행
                val cursor = requireActivity().contentResolver.query(
                    contactUri,
                    queryFields,
                    null,
                    null,
                    null
                )

                cursor?.use {
                    // 쿼리 결과 데이터가 있는지 확인
                    if (it.count == 0) {
                        return
                    }

                    // 첫 번째 데이터 행의 첫 번째 열의 값을 가져온다. 이 값이 용의자의 이름이다.
                    it.moveToFirst() // 첫 번째 행으로 이동
                    val suspect = it.getString(0) // 첫 번째 열의 값 가져오기

                    crime.suspect = suspect
                    crimeDetailViewModel.saveCrime(crime) // 데이터베이스 저장 및 업데이트
                    suspectButton.text = suspect
                }
            }

            requestCode == REQUEST_PHOTO -> {
                requireActivity().revokeUriPermission(photoUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION) // Uri에 파일을 쓸 수 있는 퍼미션 취소
                updatePhotoView()
            }
        }
    }

    // 문자열 네 개를 생성하고 결합해 하나의 완전한 보고서 문자열로 반환
    private fun getCrimeReport(): String {
        // 문제 해결 여부
        val solvedString = if (crime.isSolved) {
            getString(R.string.crime_report_solved)
        } else {
            getString(R.string.crime_report_unsolved)
        }

        // 날짜
        val dateString =
            DateFormat.format(org.javaapp.criminalintent.DATE_FORMAT, crime.date).toString()

        // 용의자
        var suspect = if (crime.suspect.isBlank()) {
            getString(R.string.crime_report_no_suspect)
        } else {
            getString(R.string.crime_report_suspect, crime.suspect)
        }

        return getString(R.string.crime_report, crime.title, dateString, solvedString, suspect)
    }

    companion object {
        fun newInstance(crimeId: UUID): CrimeFragment {
            val args = Bundle().apply { // 인자 번들 생성
                putSerializable(ARG_CRIME_ID, crimeId) // 인자 저장
            }

            return CrimeFragment().apply { // 프래그먼트 인스턴스 생성
                arguments = args // 인자 번들 첨부
            }
        }
    }
}