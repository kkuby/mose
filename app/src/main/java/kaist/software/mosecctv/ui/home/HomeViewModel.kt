package kaist.software.mosecctv.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import java.util.*
import java.util.stream.Collectors
import kotlin.collections.ArrayList
import kotlin.math.log

class HomeViewModel : ViewModel() {

    private val _logDataList = MutableLiveData<List<LogData>>()

    val logDataList: LiveData<List<LogData>> = _logDataList

    private val db = Firebase.firestore

    private val list = ArrayList<LogData>()

    val logDoc = db.collection("CCTV_History")
        .get()
        .addOnSuccessListener { documents ->
            for(document in documents){
                val logData = document.toObject<LogData>()
                logData.docId = document.id
                list.add(0,logData)
            }

            _logDataList.value = list
        }
}