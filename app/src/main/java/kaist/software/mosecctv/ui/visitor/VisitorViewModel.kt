package kaist.software.mosecctv.ui.visitor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kaist.software.mosecctv.data.VisitorData

class VisitorViewModel : ViewModel() {

    private val _visitorDataList = MutableLiveData<List<VisitorData>>()

    val visitorDataList: LiveData<List<VisitorData>> = _visitorDataList

    private val _visitorId = MutableLiveData<Long>()

    val visitorId: LiveData<Long> = _visitorId

    private val db = Firebase.firestore
    private var list = ArrayList<VisitorData>()

    init {
        getVisitors()

        val visitorDoc2 = db.collection("Visitor_android")
            .addSnapshotListener { value, error ->
                if(error!=null){
                    return@addSnapshotListener
                }

                if(value!=null){
                    getVisitors()
                }
            }

    }

    private fun getVisitors(){
        val visitorDoc = db.collection("Visitor_android")
            .get()
            .addOnSuccessListener { documents ->
                list.clear()
                for(document in documents){
                    val visitorData = document.toObject<VisitorData>()
                    list.add(visitorData)
                }
                _visitorDataList.value = list
            }
    }

    fun getVisitorId(){
        db.collection("sequence")
            .document("VisitorId")
            .get()
            .addOnSuccessListener {
                var id = it.get("id") as Long?
                if(id == null){
                    db.collection("sequence")
                        .document("VisitorId")
                        .set(hashMapOf("id" to 11))
                        .addOnSuccessListener {
                            _visitorId.value = 11
                        }
                }else{
                    _visitorId.value = id!!
                }
            }

    }

    fun updateVisitor(visitorData: VisitorData){
        db.collection("Visitor_android")
            .document(visitorData.docId!!)
            .set(visitorData)
    }

}