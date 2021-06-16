package kaist.software.mosecctv.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kaist.software.mosecctv.R
import kaist.software.mosecctv.base.LogViewDialog
import kaist.software.mosecctv.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var logListView: RecyclerView
    private lateinit var cctvLogAdapter: CCTVLogAdapter

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
                ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(HomeViewModel::class.java)

        binding = FragmentHomeBinding.inflate(inflater)
        logListView = binding.logListView
        logListView.setHasFixedSize(true)
        logListView.itemAnimator = null
        val linealLayoutManager = LinearLayoutManager(context)
        logListView.layoutManager = linealLayoutManager
        cctvLogAdapter = object: CCTVLogAdapter(){}
        logListView.adapter = cctvLogAdapter
        cctvLogAdapter.onItemClickListener = object : CCTVLogAdapter.OnItemClickListener{
            override fun onItemClick(logData: LogData) {
                val logViewDialog = context?.let { LogViewDialog(it, logData) }?.show()
            }
        }

        homeViewModel.logDataList.observe(viewLifecycleOwner, Observer {t ->
            cctvLogAdapter.setLogDataList(t)
        })

        return binding.root
    }
}