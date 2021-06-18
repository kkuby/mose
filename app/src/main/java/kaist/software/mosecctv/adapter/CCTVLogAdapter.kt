package kaist.software.mosecctv.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kaist.software.mosecctv.R
import kaist.software.mosecctv.databinding.ItemLogBinding
import kaist.software.mosecctv.data.LogData

abstract class CCTVLogAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class ViewHolder(binding: ItemLogBinding) : RecyclerView.ViewHolder(binding.root){
        var logTitle: TextView = binding.logTitle
        var logMessage: TextView = binding.logMessage
        var logDate: TextView = binding.logDate
    }

    interface OnItemClickListener{
        fun onItemClick(logData: LogData)
    }

    private val logDataList: MutableList<LogData> = ArrayList()

    lateinit var onItemClickListener: OnItemClickListener

    fun setLogDataList(logDataList: List<LogData>) {
        this.logDataList.clear()
        this.logDataList.addAll(logDataList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(ItemLogBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder: ViewHolder = holder as ViewHolder
        val p = viewHolder.adapterPosition

        val logData = this.logDataList[position]
        if(logData.visitorID ==null){
            viewHolder.logTitle.text = "방문자 미인식"
        }else{
            viewHolder.logTitle.text = logData.visitorID.toString()
        }
        viewHolder.logTitle.text = logData.visitorID.toString()
        viewHolder.logMessage.text = logData.comment
        viewHolder.logDate.text = logData.dt

        if(logData.read==null || logData.read==false){
            viewHolder.itemView.background = viewHolder.itemView.context.resources.getDrawable(R.drawable.item_log_warning)
        }else{
            viewHolder.itemView.background = viewHolder.itemView.context.resources.getDrawable(R.drawable.item_log_normal)
        }

        viewHolder.itemView.setOnClickListener {
            if(onItemClickListener!=null){
                onItemClickListener.onItemClick(logData)
                viewHolder.itemView.background = viewHolder.itemView.context.resources.getDrawable(R.drawable.item_log_normal)
            }
        }
    }

    override fun getItemCount(): Int {
        return logDataList.size
    }


}