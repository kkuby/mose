package kaist.software.mosecctv.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kaist.software.mosecctv.data.VisitorData
import kaist.software.mosecctv.databinding.ItemVisitorBinding

class VisitorAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    class ViewHolder(binding: ItemVisitorBinding) : RecyclerView.ViewHolder(binding.root){
        var thumbnail: ImageView = binding.thumbnail
        var group: TextView = binding.group
        var name: TextView = binding.name
        var schedule: TextView = binding.schedule
        var state: TextView = binding.state
    }

    lateinit var context:Context

    interface OnItemClickListener{
        fun onItemClick(visitorData: VisitorData)
    }

    private val visitorDataList: MutableList<VisitorData> = ArrayList()

    lateinit var onItemClickListener: OnItemClickListener

    fun setVisitorDataList(visitorDataList: List<VisitorData>) {
        this.visitorDataList.clear()
        this.visitorDataList.addAll(visitorDataList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return ViewHolder(ItemVisitorBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder: ViewHolder = holder as ViewHolder
        val p = viewHolder.adapterPosition

        val visitorData = this.visitorDataList[position]

        viewHolder.name.text = visitorData.name
        viewHolder.schedule.text = "${visitorData.schedule}시 이전 방문 예정"

        when(visitorData.group){
            0L->viewHolder.group.text="기본 그룹"
            1L->viewHolder.group.text="블랙리스트"
        }
        when(visitorData.state){
            0L-> viewHolder.state.text="얼굴 인식 대기"
            1L->viewHolder.state.text="얼굴 인식 완료"
        }
        viewHolder.itemView.setOnClickListener {
            onItemClickListener.onItemClick(visitorData)
        }

        if(visitorData.thumbnail!=null){
            var ref = Firebase.storage.reference
                .child("user_dataset/${visitorData.thumbnail}")
                .downloadUrl.addOnSuccessListener {
                    Glide.with(context).load(it).into(viewHolder.thumbnail)
                }

        }
    }

    override fun getItemCount(): Int {
        return visitorDataList.size
    }
}