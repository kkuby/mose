package kaist.software.mosecctv.ui.visitor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kaist.software.mosecctv.base.VisitorDialog
import kaist.software.mosecctv.databinding.FragmentDashboardBinding

class VisitorFragment : Fragment() {

    private lateinit var visitorViewModel: VisitorViewModel

    private lateinit var binding: FragmentDashboardBinding
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

        binding = FragmentDashboardBinding.inflate(inflater)
        visitorListView = binding.visitorListView
        visitorListView.setHasFixedSize(true)
        visitorListView.itemAnimator = null
        val linearLayoutManager = LinearLayoutManager(context)
        visitorListView.layoutManager = linearLayoutManager

        visitorAdapter = VisitorAdapter()
        visitorListView.adapter = visitorAdapter
        visitorAdapter.onItemClickListener = object : VisitorAdapter.OnItemClickListener{
            override fun onItemClick(visitorData: VisitorData) {

                val visitorDialog = context?.let { VisitorDialog(it, visitorData) }?.show()

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
            var visitorData = VisitorData()
            visitorData.state = 9
            context?.let { it -> VisitorDialog(it, visitorData) }?.show()
        }

        return binding.root
    }

}