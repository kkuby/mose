package kaist.software.mosecctv.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kaist.software.mosecctv.data.CCTVData
import kaist.software.mosecctv.databinding.ItemCctvBinding

class PairingAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class ViewHolder(binding: ItemCctvBinding) : RecyclerView.ViewHolder(binding.root){
        var name: TextView = binding.name
        var ip: TextView = binding.ip
        var connection: TextView = binding.connection
    }

    interface OnItemClickListener {
        fun onItemClick(cctvData: CCTVData)
    }

    private val cctvDataList: MutableList<CCTVData> = ArrayList()

    lateinit var onItemClickListener: OnItemClickListener

    fun setCCTVDataList(cctvDataList: List<CCTVData>){
        this.cctvDataList.clear()
        this.cctvDataList.addAll(cctvDataList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            ItemCctvBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder: ViewHolder = holder as ViewHolder

        val cctvData = this.cctvDataList[position]

        viewHolder.name.text = cctvData.name
        viewHolder.ip.text = cctvData.ip
        viewHolder.connection.text = if(cctvData.connection == true) "ON" else "OFF"

        viewHolder.itemView.setOnClickListener {
            if(onItemClickListener!=null){
                onItemClickListener.onItemClick(cctvData)
            }
        }
    }

    override fun getItemCount(): Int {
        return cctvDataList.size
    }


}