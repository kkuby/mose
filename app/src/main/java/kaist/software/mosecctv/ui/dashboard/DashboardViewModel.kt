package kaist.software.mosecctv.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase

class DashboardViewModel : ViewModel() {

    private val _visitorDataList = MutableLiveData<List<VisitorData>>()

    val visitorDataList: LiveData<List<VisitorData>> = _visitorDataList

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

    fun getVisitors(){
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

}