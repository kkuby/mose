package kaist.software.mosecctv.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import io.reactivex.Observable
import kaist.software.mosecctv.R
import kaist.software.mosecctv.data.VisitorData
import kaist.software.mosecctv.databinding.ActivityMlFaceBinding
import kaist.software.mosecctv.viewmodel.MLFaceViewModel
import java.io.File
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.concurrent.schedule

class MLFaceActivity : AppCompatActivity() {

    private var imageCapture: ImageCapture? = null
    private var videoCapture: VideoCapture? = null

    private lateinit var mlFaceViewModel: MLFaceViewModel

    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService

    private lateinit var binding: ActivityMlFaceBinding
    private lateinit var previewView: PreviewView

    private lateinit var circle: View
    private lateinit var progressBar: ProgressBar
    private lateinit var processText: TextView

    private var _state = -1
    private var _process = 1
    private var _progress = 0

    private var storage: FirebaseStorage? = null
    var ref : StorageReference? = null

    private lateinit var visitorData: VisitorData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val auth = FirebaseAuth.getInstance()
        storage = Firebase.storage
        ref = storage!!.reference

        if(intent.hasExtra("visitorId")){
            visitorData = VisitorData()
            visitorData.visitorID = intent.getLongExtra("visitorId",-1)
            visitorData.name = intent.getStringExtra("visitorName")
        }

        mlFaceViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(MLFaceViewModel::class.java)

        mlFaceViewModel.complete.observe(this, {
            if(it){
                finish()
            }
        })

        binding = ActivityMlFaceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }
        previewView = binding.viewFinder
        circle = binding.circle
        progressBar = binding.progressBar
        processText = binding.processText

        outputDirectory = getOutputDirectory()

        cameraExecutor = Executors.newSingleThreadExecutor()

    }

    private fun detectFaceProcess(bounds:Rect, rotY:Float, rotZ:Float){

        if(bounds.left==0 &&bounds.top==0 &&bounds.right==0 &&bounds.bottom==0){
            circle.setBackgroundResource(R.drawable.circle_red)
            processText.text = "원 안에 얼굴을 인식해 주세요."
            return
        }

        startVideo()

    }

    @SuppressLint("RestrictedApi")
    private fun takeVideo() {
        val videoCapture = videoCapture?:return

        val fileName = "${visitorData.visitorID}.${visitorData.name}.mp4"

        val videoFile = File(
            outputDirectory,
            fileName
        )

        val outputOptions = VideoCapture.OutputFileOptions.Builder(videoFile).build()

        videoCapture.startRecording(outputOptions,
            ContextCompat.getMainExecutor(this),
            object: VideoCapture.OnVideoSavedCallback{
                override fun onVideoSaved(outputFileResults: VideoCapture.OutputFileResults) {

                    val savedUri = Uri.fromFile(videoFile)

                    val msg = "Video recording succeeded: $savedUri"
                    //Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)

                    Log.d(TAG, outputFileResults.toString())

                    val uploadTask = ref?.child("user_dataset/${fileName}")?.putFile(savedUri)
                    visitorData.fileName = fileName
                    mlFaceViewModel.createVisitor(visitorData)
                }

                override fun onError(videoCaptureError: Int, message: String, cause: Throwable?) {
                    Log.e(TAG, "Video recording failed: ${message}")
                    startCamera()
                }

            })

        Timer("recordStop", false).schedule(3000){
            videoCapture.stopRecording()
        }

        val source = Observable.interval(90L, TimeUnit.MILLISECONDS).map {
            _progress+=3
            progressBar.progress = _progress
        }

    }

    private fun startVideo(){
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(Runnable {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            //video
            videoCapture = VideoCapture.Builder()
                .build()

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, videoCapture
                )

                Timer("recordStart", false).schedule(1000){
                    takeVideo()
                }

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(Runnable {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .build()

            val imageAnalysis = ImageAnalysis.Builder()
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, YourImageAnalyzer { bounds, rotY, rotZ -> detectFaceProcess(bounds, rotY, rotZ) })
                }

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            //video
            videoCapture = VideoCapture.Builder()
                .build()

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture, imageAnalysis
                )

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() } }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "CameraXBasic"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(
                    this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }



    private class YourImageAnalyzer(private val listener: (bounds:Rect, rotY:Float, rotZ:Float)->Unit) : ImageAnalysis.Analyzer {

        private val ANALYSIS_DELAY_MS = 500
        private val INVALID_TIME = -1
        private var lastAnalysisTime = INVALID_TIME.toLong()

        @SuppressLint("UnsafeOptInUsageError")
        override fun analyze(imageProxy: ImageProxy) {

            val mediaImage = imageProxy.image
            if (mediaImage != null) {
                val image = InputImage.fromMediaImage(
                    mediaImage,
                    imageProxy.imageInfo.rotationDegrees
                )
                // Pass image to an ML Kit Vision API
                // ...

                //Log.d(TAG, "image")

                // High-accuracy landmark detection and face classification
                val highAccuracyOpts = FaceDetectorOptions.Builder()
                    .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                    .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                    .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                    .build()

                // Real-time contour detection
                val realTimeOpts = FaceDetectorOptions.Builder()
                    .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
                    .build()

                val detector = FaceDetection.getClient(highAccuracyOpts)

                val now = SystemClock.uptimeMillis()

                if(lastAnalysisTime!=INVALID_TIME.toLong() && (now-lastAnalysisTime < ANALYSIS_DELAY_MS)){
                    imageProxy.close()
                }else{
                    detector.process(image)
                            .addOnSuccessListener { faces ->
                                for (face in faces) {
                                    val bounds = face.boundingBox
                                    val rotY = face.headEulerAngleY // Head is rotated to the right rotY degrees
                                    val rotZ = face.headEulerAngleZ // Head is tilted sideways rotZ degrees

                                    listener(bounds, rotY, rotZ)

                                    // If face tracking was enabled:
                                    if (face.trackingId != null) {
                                        val id = face.trackingId
                                    }

                                }
                                lastAnalysisTime = now
                            }
                            .addOnFailureListener { e ->
                                //listener(Rect(0,0,0,0), 0f, 0f)
                            }
                    imageProxy.close()
                }
            }
        }
    }
}