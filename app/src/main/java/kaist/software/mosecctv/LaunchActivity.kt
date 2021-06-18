package kaist.software.mosecctv

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.Observable
import kaist.software.mosecctv.activity.InitActivity
import kaist.software.mosecctv.databinding.ActivityLaunchBinding
import java.util.*
import kotlin.concurrent.schedule

class LaunchActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLaunchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLaunchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPref = getPreferences(Context.MODE_PRIVATE)

        val isLogin = sharedPref.getBoolean("isLogin", false)

        val intent: Intent = if(isLogin){
            Intent(this, MainActivity::class.java)
        }else{
            Intent(this, InitActivity::class.java)
        }

        Timer("launch",false).schedule(2000){


            startActivity(intent)
        }

    }

}