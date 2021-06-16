package kaist.software.mosecctv.ui.home

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.VideoView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kaist.software.mosecctv.R
import kaist.software.mosecctv.base.LogViewDialog
import kaist.software.mosecctv.databinding.FragmentHomeBinding
import java.io.File

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var logListView: RecyclerView
    private lateinit var cctvLogAdapter: CCTVLogAdapter

    private lateinit var videoView: VideoView

    var ref : StorageReference? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
                ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(HomeViewModel::class.java)

        ref = Firebase.storage.reference

        binding = FragmentHomeBinding.inflate(inflater)

        videoView = binding.videoView

        logListView = binding.logListView
        logListView.setHasFixedSize(true)
        logListView.itemAnimator = null
        val linealLayoutManager = LinearLayoutManager(context)
        logListView.layoutManager = linealLayoutManager
        cctvLogAdapter = object: CCTVLogAdapter(){}
        logListView.adapter = cctvLogAdapter
        cctvLogAdapter.onItemClickListener = object : CCTVLogAdapter.OnItemClickListener{
            override fun onItemClick(logData: LogData) {
                //val logViewDialog = context?.let { LogViewDialog(it, logData) }?.show()

                if(logData.id==2L && logData.file_name!!.endsWith(".mp4", false) ){

                    videoView.stopPlayback()

                    val video = ref!!.child("recordVideos/${logData.file_name}")
                        .downloadUrl.addOnSuccessListener {
                            videoView.setVideoURI(it)
                            videoView.requestFocus()
                            videoView.start()
                        }

                }
            }
        }

        homeViewModel.logDataList.observe(viewLifecycleOwner, Observer {t ->
            cctvLogAdapter.setLogDataList(t)
        })

        return binding.root
    }



}