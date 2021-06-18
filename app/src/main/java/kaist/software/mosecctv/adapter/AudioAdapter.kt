package kaist.software.mosecctv.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kaist.software.mosecctv.data.AudioData
import kaist.software.mosecctv.databinding.ItemAudioFileBinding

class AudioAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class ViewHolder(binding: ItemAudioFileBinding) : RecyclerView.ViewHolder(binding.root){
        var audioTitle: TextView = binding.audioTitle
    }

    interface OnItemClickListener{
        fun onItemClick(audioData: AudioData)
    }

    private val audioDataList: MutableList<AudioData> = ArrayList()

    lateinit var onItemClickListener: OnItemClickListener

    fun setAudioDataList(audioDataList: List<AudioData>) {
        this.audioDataList.clear()
        this.audioDataList.addAll(audioDataList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            ItemAudioFileBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder: ViewHolder = holder as ViewHolder
        val p = viewHolder.adapterPosition

        val audioData = this.audioDataList[position]

        viewHolder.audioTitle.text = audioData.file_name

        viewHolder.itemView.setOnClickListener {
            if(onItemClickListener!=null){
                onItemClickListener.onItemClick(audioData)
            }
        }
    }

    override fun getItemCount(): Int {
        return audioDataList.size
    }

}