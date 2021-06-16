package kaist.software.mosecctv.viewmodel

import android.util.Log
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kaist.software.mosecctv.base.AudioData

class WebViewViewModel : ViewModel() {
    private val _url = MutableLiveData<String>()
    private val _audioData = MutableLiveData<AudioData>()
    private val _audioDataList = MutableLiveData<List<AudioData>>()

    val url: LiveData<String> = _url
    val audioData: LiveData<AudioData> = _audioData
    val audioDataList: LiveData<List<AudioData>> = _audioDataList

    val db = Firebase.firestore

    val collection = db.collection("cctv")

    val connectionDoc = collection.document("Reference")
        .addSnapshotListener { value, error ->
            if(error!=null){
                return@addSnapshotListener
            }

            if(value!=null && value.exists()){
                _url.value = value.get("URL") as String?
            }
        }

    val audioDoc = collection.document("Control_Audio")
        .addSnapshotListener { value, error ->
            if(error!=null){
                return@addSnapshotListener
            }

            if(value!=null && value.exists()){
                _audioData.value = value.toObject<AudioData>()
            }
        }

    val storage = Firebase.storage
    var ref = storage.reference

    val audioList = ref.child("audio").listAll()
        .addOnSuccessListener {
            val list = ArrayList<AudioData>()
            it.items.forEach {
                item->
                    val audioData = AudioData()
                    audioData.file_name = item.name
                    audioData.cmd = true
                    audioData.state = false
                    list.add(audioData)
            }
            _audioDataList.value = list
        }

    fun playAudio(audioData: AudioData){
        val audioDoc = collection.document("Control_Audio")
            .set(audioData)
    }

}