package kaist.software.mosecctv.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kaist.software.mosecctv.data.VisitorData

class MLFaceViewModel : ViewModel() {

    private val _complete = MutableLiveData<Boolean>()

    val complete : LiveData<Boolean> = _complete

    private val db = Firebase.firestore

    init {

    }

    fun createVisitor(visitorData: VisitorData){
        db.collection("Visitor_android")
            .add(visitorData)
            .addOnSuccessListener {
                visitorData.docId = it.id
                db.collection("Visitor_android")
                    .document(visitorData.docId!!)
                    .set(visitorData)
                    .addOnSuccessListener {
                        db.collection("sequence")
                            .document("VisitorId")
                            .get()
                            .addOnSuccessListener {
                                val id= it.get("id") as Long
                                db.collection("sequence")
                                    .document("VisitorId")
                                    .update("id", id+1)
                                    .addOnSuccessListener {
                                        db.collection("cctv")
                                            .document("Control_Train")
                                            .update(hashMapOf<String, Any>("state" to 1, "file_name" to "${visitorData.fileName}"))
                                            .addOnSuccessListener {
                                                _complete.value = true
                                            }
                                    }
                            }
                    }
            }
    }

}