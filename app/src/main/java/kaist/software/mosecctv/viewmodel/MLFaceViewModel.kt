package kaist.software.mosecctv.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kaist.software.mosecctv.ui.dashboard.VisitorData

class MLFaceViewModel : ViewModel() {

    private val _complete = MutableLiveData<Boolean>()

    val complete : LiveData<Boolean> = _complete

    private val db = Firebase.firestore

    fun finishFace(visitorDocId:String){
        val visitorDoc = db.collection("Visitor_android")
            .document(visitorDocId)
            .get()
            .addOnSuccessListener {

                if(it != null){
                    var vDoc = it.toObject<VisitorData>()
                    if (vDoc != null) {
                        vDoc.thumbnail = "${vDoc.docId}01.jpg"
                        val again = vDoc.docId?.let { it1 ->
                            db.collection("Visitor_android")
                                .document(it1)
                                .set(vDoc)
                                .addOnSuccessListener {
                                    val cctvDoc = db.collection("cctv")
                                        .document("Control_Train")
                                        .update("state", 1)
                                    _complete.value = true
                                }
                        }
                    }
                }

            }
    }
}