package kaist.software.mosecctv.ui.visitor

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kaist.software.mosecctv.activity.MLFaceActivity
import kaist.software.mosecctv.adapter.VisitorAdapter
import kaist.software.mosecctv.base.VisitorDialog
import kaist.software.mosecctv.data.VisitorData
import kaist.software.mosecctv.databinding.FragmentVisitorBinding

class VisitorFragment : Fragment() {

    private lateinit var visitorViewModel: VisitorViewModel

    private lateinit var binding: FragmentVisitorBinding
    private lateinit var visitorListView: RecyclerView
    private lateinit var visitorAdapter: VisitorAdapter
    private lateinit var addVisitor: Button

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        visitorViewModel =
                ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(VisitorViewModel::class.java)

        binding = FragmentVisitorBinding.inflate(inflater)
        visitorListView = binding.visitorListView
        visitorListView.setHasFixedSize(true)
        visitorListView.itemAnimator = null
        val linearLayoutManager = LinearLayoutManager(context)
        visitorListView.layoutManager = linearLayoutManager

        visitorAdapter = VisitorAdapter()
        visitorListView.adapter = visitorAdapter
        visitorAdapter.onItemClickListener = object : VisitorAdapter.OnItemClickListener{
            //update
            override fun onItemClick(visitorData: VisitorData) {

                val visitorDialog = context?.let { VisitorDialog(it, visitorData) }
                if (visitorDialog != null) {
                    visitorDialog.onCreateClickListener = object: VisitorDialog.OnCreateClickListener{
                        override fun onCreateClick(visitorData: VisitorData) {
                            visitorViewModel.updateVisitor(visitorData)
                        }

                    }
                    visitorDialog.show()
                }

//                if(visitorData.state==9L){
//                    val intent = Intent(context, MLFaceActivity::class.java)
//                    intent.putExtra("visitorDocId", visitorData.docId)
//                    startActivity(intent)
//                }else{
//                    val visitorDialog = context?.let { VisitorDialog(it, visitorData) }?.show()
//                }
            }
        }

        visitorViewModel.visitorDataList.observe(viewLifecycleOwner, {
            visitorAdapter.setVisitorDataList(it)
        })

        addVisitor = binding.addVisitor
        addVisitor.setOnClickListener {
            visitorViewModel.getVisitorId()
        }

        visitorViewModel.visitorId.observe(viewLifecycleOwner, {
            var visitorData = VisitorData()
            visitorData.visitorID = it
            visitorData.state = 0
            val visitorDialog = context?.let { it -> VisitorDialog(it, visitorData) }
            //create
            if (visitorDialog != null) {
                visitorDialog.onCreateClickListener = object : VisitorDialog.OnCreateClickListener{
                    override fun onCreateClick(visitorData: VisitorData) {
                        val intent = Intent(context, MLFaceActivity::class.java)
                        intent.putExtra("visitorId", visitorData.visitorID)
                        intent.putExtra("visitorName", visitorData.name)
                        startActivity(intent)
                        visitorDialog.dismiss()
                    }

                }
                visitorDialog.show()
            }
        })

        return binding.root
    }

}