package org.javaapp.criminalintent

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import org.w3c.dom.Text

private const val TAG = "CrimeListFragment"

class CrimeListFragment : Fragment() {

    private lateinit var crimeRecyclerView: RecyclerView

    private val crimeListViewModel : CrimeListViewModel by lazy {
        ViewModelProvider(this).get(CrimeListViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "Total crimes : ${crimeListViewModel.crimes.size}")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crime_list, container, false)

        crimeRecyclerView = view.findViewById(R.id.crime_recycler_view) as RecyclerView
        crimeRecyclerView.layoutManager = LinearLayoutManager(context) // LayoutManager 바로 설정

        return view
    }

    private inner class CrimeHolder(view : View) : RecyclerView.ViewHolder(view) {
        // view(item view)의 TextView 참조
        val titleTextView : TextView = itemView.findViewById(R.id.crime_title)
        val dateTextView : TextView = itemView.findViewById(R.id.crime_date)
    }

    private inner class CrimeAdapter(var crimes : List<Crime>) : RecyclerView.Adapter<CrimeHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeHolder {
            val view = layoutInflater.inflate(R.layout.list_item_crime, parent, false)

            return CrimeHolder(view)
        }

        override fun getItemCount(): Int {
            return crimes.size
        }

        override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
            val crime = crimes[position]
            holder.apply {
                titleTextView.text = crime.title
                dateTextView.text = crime.date.toString()
            }
        }

    }

    companion object {
        fun newInstance() : CrimeListFragment {
            return CrimeListFragment()
        }
    }

}