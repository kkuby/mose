package kaist.software.mosecctv.base

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.VideoView
import androidx.appcompat.app.AppCompatDialog
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kaist.software.mosecctv.databinding.DialogLogViewBinding
import kaist.software.mosecctv.data.LogData

class LogViewDialog(context: Context, private var logData: LogData) : AppCompatDialog(context) {
    private var binding: DialogLogViewBinding
    private var visitor: TextView
    private var dt: TextView
    private var comment: TextView
    private var imageView: ImageView
    private var videoView: VideoView
    private var progressBar: ProgressBar

    init {
        setCancelable(true)
        binding = DialogLogViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val layoutParams = window?.attributes
        if (layoutParams != null) {
            layoutParams.width = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                300F, context.resources.displayMetrics))

            var height = if(logData.id == 2L || logData.id == 3L){
                400F
            }else{
                200F
            }

            layoutParams.height = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                height, context.resources.displayMetrics))
            window?.attributes = layoutParams
        }

        visitor = binding.visitor
        dt = binding.dt
        comment = binding.comment
        imageView = binding.imageView
        videoView = binding.videoView
        progressBar = binding.progressBar

        visitor.text = logData.visitorID.toString()
        dt.text = logData.dt
        comment.text = logData.comment

        val storage = Firebase.storage
        var ref = storage.reference

        logData.read = true
        val db = Firebase.firestore
        val up = logData.docId?.let {
            db.collection("CCTV_History")
                .document(it)
                .set(logData)
        }

        if(logData.id==2L){
            imageView.visibility = View.VISIBLE
            videoView.visibility = View.GONE

            val image = ref.child("captureImages/${logData.file_name}")
                .downloadUrl.addOnSuccessListener {
                    Glide.with(context).load(it).into(imageView)
                    progressBar.visibility = View.GONE
                }
                .addOnFailureListener {
                    progressBar.visibility = View.GONE
                }

        }else if(logData.id==3L){
            imageView.visibility = View.GONE
            videoView.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
        }else{
            imageView.visibility = View.GONE
            videoView.visibility = View.GONE
            progressBar.visibility = View.GONE
        }


    }


}

