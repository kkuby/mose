package kaist.software.mosecctv

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainViewModel : ViewModel() {

    private val _connection = MutableLiveData<Long>()

    val connection: LiveData<Long> = _connection

    val db = Firebase.firestore

    val collection = db.collection("cctv")

    val connectionDoc = collection.document("Connect")
    .addSnapshotListener { value, error ->
        if(error!=null){
            return@addSnapshotListener
        }

        if(value!=null && value.exists()){
            _connection.value = value.get("state") as Long?
            Log.d("test", "${value.get("state")}")
        }
     }

}