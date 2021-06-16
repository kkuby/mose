package kaist.software.mosecctv

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import kaist.software.mosecctv.activity.WebViewActivity
import kaist.software.mosecctv.databinding.ActivityMainBinding
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var mainViewModel: MainViewModel

    private lateinit var binding: ActivityMainBinding
    private lateinit var connectionState: TextView

    private lateinit var outputDirectory: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment)
        navView.setupWithNavController(navController)

        connectionState = binding.connectionState
        connectionState.setOnClickListener {
            if(connectionState.isSelected){
                val intent = Intent(this, WebViewActivity::class.java)
                startActivity(intent)
            }else{
                Toast.makeText(this, "CCTV 연결을 확인해주세요.", Toast.LENGTH_SHORT).show()
            }
         }

        mainViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(MainViewModel::class.java)
        mainViewModel.connection.observe(this, {
            when(it){
                0L -> {
                    connectionState.isSelected = false
                    connectionState.text = "CCTV OFF"
                }
                1L -> {
                    connectionState.isSelected = true
                    connectionState.text = "CCTV ON"
                }

            }
        })

        Firebase.messaging.subscribeToTopic("log")
            .addOnCompleteListener { it ->
                Log.d("notification test", it.toString())
            }

        outputDirectory = getOutputDirectory()

    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() } }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }
}