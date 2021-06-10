package kaist.software.mosecctv.activity

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kaist.software.mosecctv.base.CCTVCmd
import kaist.software.mosecctv.databinding.ActivityRealtimeBinding
import java.util.*

class RealtimeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRealtimeBinding

    private lateinit var audio: Button
    private lateinit var update: Button
    private lateinit var notification: Button
    private lateinit var connectionState: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRealtimeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        audio = binding.audio
        update = binding.update
        notification = binding.notification
        connectionState = binding.connectionState

        val database = Firebase.database

        audio.setOnClickListener {
            //var cctvCmd = CCTVCmd(0, 1, "test.wav")
            //database.getReference("cctv_cmd").child(UUID.randomUUID().toString()).setValue(cctvCmd)
        }

        update.setOnClickListener {
            //var cctvCmd = CCTVCmd(0,2,"visitor pic update",)
            //database.getReference("cctv_cmd").child().setValue(cctvCmd)
        }

        val connectedRef = database.getReference(".info/connected")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    val connected = snapshot.getValue(Boolean::class.java)!!
                    if(connected){
                        connectionState.text = "connected"
                    }else{
                        connectionState.text = "disconnected"
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

    }

}