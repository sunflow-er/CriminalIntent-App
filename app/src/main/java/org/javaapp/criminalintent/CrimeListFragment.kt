package org.javaapp.criminalintent

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import org.w3c.dom.Text
import java.util.UUID

private const val TAG = "CrimeListFragment"

class CrimeListFragment : Fragment() {
    /**
     * 호스팅 액티비티에서 구현할 인터페이스
     */
    interface Callbacks {
        fun onCrimeSelected(crimeId: UUID)
    }

    private var callbacks: Callbacks? = null // Callbacks 인터페이스를 구현하는 객체를 참조하기 위한 속성

    private lateinit var crimeRecyclerView: RecyclerView
    private var adapter: CrimeAdapter? = CrimeAdapter(emptyList())

    // 뷰모델 연결
    private val crimeListViewModel: CrimeListViewModel by lazy {
        ViewModelProvider(this).get(CrimeListViewModel::class.java)
    }

    // callbacks 속성을 설정
    override fun onAttach(context: Context) { // 프래그먼트가 호스팅 액티비티에 연결될 때 호출
        super.onAttach(context)
        
        /*
        호스팅 액티비티 인스턴스인 Context 객체를 Callbacks 인터페이스 타입으로 캐스팅
        이렇게 함으로써 프래그먼트는 Callbacks 인터페이스를 구현하는 액티비티의 인스턴스에 접근할 수 있게 됨
        이 인스턴스를 통해 프래그먼트는 액티비티에 정의된 메서드를 호출할 수 있음
         */
        callbacks = context as Callbacks
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crime_list, container, false)

        crimeRecyclerView = view.findViewById(R.id.crime_recycler_view) as RecyclerView
        crimeRecyclerView.layoutManager = LinearLayoutManager(context) // LayoutManager 바로 설정
        crimeRecyclerView.adapter = adapter

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // crimeListViewModel의 crimeListLiveData를 관찰하고 변경될 때마다 UI 업데이트
        crimeListViewModel.crimeListLiveData.observe(
            viewLifecycleOwner, // 프래그먼트의 뷰와 관련된 생명주기 이벤트를 관찰하는 데 사용, 프래그먼트의 뷰가 화면에 있을 때만 업데이트가 수행
            Observer { crimes -> // Observer 객체를 익명클래스로 생성, 관찰되는 데이터가 변경될 때 호출될 콜백 함수 정의
                crimes?.let {
                    Log.i(TAG, "Got crimes ${crimes.size}")
                    updateUI(crimes)
                }
            }
        )
    }

    // callbacks 속성을 설정 해제
    override fun onDetach() { // 프래그먼트가 액티비티에서 분리될 때 호출
        super.onDetach()
        callbacks = null
    }

    private fun updateUI(crimes: List<Crime>) {
        adapter = CrimeAdapter(crimes)
        crimeRecyclerView.adapter = adapter
    }

    // ViewHolder
    private inner class CrimeHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        private lateinit var crime : Crime

        // view(item view)의 TextView 참조
        private val titleTextView: TextView = itemView.findViewById(R.id.crime_title)
        private val dateTextView: TextView = itemView.findViewById(R.id.crime_date)
        private val solvedImageView : ImageView = itemView.findViewById(R.id.crime_solved)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(crime : Crime) {
            this.crime = crime
            titleTextView.text = this.crime.title
            dateTextView.text = this.crime.date.toString()
            solvedImageView.visibility = if (crime.isSolved) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }

        override fun onClick(v: View?) {
            callbacks?.onCrimeSelected(crime.id)
        }
    }

    // RecyclerView Adapter
    private inner class CrimeAdapter(var crimes: List<Crime>) :
        RecyclerView.Adapter<CrimeHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeHolder {
            val view = layoutInflater.inflate(R.layout.list_item_crime, parent, false)

            return CrimeHolder(view)
        }

        override fun getItemCount(): Int {
            return crimes.size
        }

        override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
            val crime = crimes[position]
            holder.bind(crime)
        }

    }

    companion object {
        fun newInstance(): CrimeListFragment {
            return CrimeListFragment()
        }
    }

}