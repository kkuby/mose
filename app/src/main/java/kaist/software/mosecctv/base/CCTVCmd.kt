package kaist.software.mosecctv.base

import com.google.firebase.Timestamp
import java.util.*

class CCTVCmd(state: Int, type: Int, message: String, timestamp: Timestamp) {

    var state: Int =state
    var type: Int =type
    var message: String=message
    lateinit var timestamps: Timestamp
}