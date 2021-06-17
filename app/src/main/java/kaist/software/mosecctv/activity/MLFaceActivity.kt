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
import android.view.WindowManager
import android.widget.Button
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
import com.google.mlkit.vision.face.FaceContour
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.face.FaceLandmark
import io.reactivex.Observable
import io.reactivex.observers.DisposableObserver
import kaist.software.mosecctv.R
import kaist.software.mosecctv.databinding.ActivityMlFaceBinding
import kaist.software.mosecctv.viewmodel.MLFaceViewModel
import java.io.File
import java.text.SimpleDateFormat
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

    private var docId = ""
    private var storage: FirebaseStorage? = null
    var ref : StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val auth = FirebaseAuth.getInstance()
        storage = Firebase.storage
        ref = storage!!.reference

        if(intent.hasExtra("visitorDocId")){
            docId = intent.getStringExtra("visitorDocId").toString()
            if(docId == null){
                docId = "test"
            }
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

//
//        if(_progress>=15){
//            finishFace()
//            return
//        }
//
//        circle.setBackgroundResource(R.drawable.circle_green)
//
//        var location = 0
//
//        if(rotY<-5){
//            location = 1
//        }else if(rotY>5){
//            location = 2
//        }else{
//            location = 0
//        }
//
//        if(_progress<5){
//            if(location==0){
//                processText.text = "얼굴 인식 중 입니다."
//                circle.setBackgroundResource(R.drawable.circle_green)
//                _progress++
//                progressBar.progress = _progress*3
//                takePhoto()
//            }else{
//                processText.text = "정면을 바라봐 주세요."
//                circle.setBackgroundResource(R.drawable.circle_red)
//            }
//        }else if(_progress>=10){
//            processText.text = "얼굴 인식 중 입니다."
//            if(location==2){
//                circle.setBackgroundResource(R.drawable.circle_green)
//                _progress++
//                progressBar.progress = _progress*3
//                takePhoto()
//            }else{
//                processText.text = "고개를 왼쪽으로 돌려주세요"
//                circle.setBackgroundResource(R.drawable.circle_red)
//            }
//        }else{
//            processText.text = "얼굴 인식 중 입니다."
//            if(location==1){
//                circle.setBackgroundResource(R.drawable.circle_green)
//                _progress++
//                progressBar.progress = _progress*3
//                takePhoto()
//            }else{
//                processText.text = "고개를 오른쪽으로 돌려주세요."
//                circle.setBackgroundResource(R.drawable.circle_red)
//            }
//        }
    }

    fun finishFace(){
        if(intent.hasExtra("visitorDocId")){
            intent.getStringExtra("visitorDocId")?.let { mlFaceViewModel.finishFace(it) }
        }
    }

    @SuppressLint("RestrictedApi")
    private fun takeVideo() {
        val videoCapture = videoCapture?:return

        val fileName = "4.test.mp4"

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
                }

                override fun onError(videoCaptureError: Int, message: String, cause: Throwable?) {
                    Log.e(TAG, "Video recording failed: ${message}")
                }

            })

        Timer("recordStop", false).schedule(3000){
            videoCapture.stopRecording()
        }

        val source = Observable.interval(100L, TimeUnit.MILLISECONDS).map {

        }

    }

    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time-stamped output file to hold the image
//        val photoFile = File(
//            outputDirectory,
//            SimpleDateFormat(
//                FILENAME_FORMAT, Locale.KOREA
//            ).format(System.currentTimeMillis()) + ".jpg"
//        )

        var fileName = docId
        if(_progress<10){
            fileName+= "0$_progress.jpg"
        }else{
            fileName+= "$_progress.jpg"
        }

        val photoFile = File(
            outputDirectory,
            fileName
        )


        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    val msg = "Photo capture succeeded: $savedUri"
                    //Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)
                    val storage = Firebase.storage
                    var ref = storage.reference

                    val uploadTask = ref.child("user_dataset/${fileName}").putFile(savedUri)

                }
            })
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
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
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

                                    //Log.d(TAG, "bounds = $bounds, rotY = $rotY, rotZ = $rotZ")


                                    // If landmark detection was enabled (mouth, ears, eyes, cheeks, and
                                    // nose available):
//                                    val leftEar = face.getLandmark(FaceLandmark.LEFT_EAR)
//                                    leftEar?.let {
//                                        val leftEarPos = leftEar.position
//                                        Log.d(TAG, "leftEar = $leftEar")
//                                    }
//
//                                    // If contour detection was enabled:
//                                    val leftEyeContour = face.getContour(FaceContour.LEFT_EYE)?.points
//                                    val upperLipBottomContour = face.getContour(FaceContour.UPPER_LIP_BOTTOM)?.points
//
//                                    // If classification was enabled:
//                                    if (face.smilingProbability != null) {
//                                        val smileProb = face.smilingProbability
//                                    }
//                                    if (face.rightEyeOpenProbability != null) {
//                                        val rightEyeOpenProb = face.rightEyeOpenProbability
//                                    }

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