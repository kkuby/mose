package kaist.software.mosecctv.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kaist.software.mosecctv.data.LogData
import kotlin.collections.ArrayList

class HomeViewModel : ViewModel() {

    private val _logDataList = MutableLiveData<List<LogData>>()

    val logDataList: LiveData<List<LogData>> = _logDataList

    private val db = Firebase.firestore

    private val list = ArrayList<LogData>()

    init {
        getCCTVHistory()

        val logSnap = db.collection("CCTV_History")
            .addSnapshotListener { value, error ->
                if(value!=null){
                    getCCTVHistory()
                }
            }
    }


    private fun getCCTVHistory(){
        val logDoc = db.collection("CCTV_History")
            .get()
            .addOnSuccessListener { documents ->
                list.clear()
                for(document in documents){
                    val logData = document.toObject<LogData>()
                    if(logData.id == 2L){
                        list.add(0,logData)
                    }
                }

                _logDataList.value = list
            }
    }
}