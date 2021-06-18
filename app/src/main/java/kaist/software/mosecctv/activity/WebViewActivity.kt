package kaist.software.mosecctv.activity

import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kaist.software.mosecctv.adapter.AudioAdapter
import kaist.software.mosecctv.data.AudioData
import kaist.software.mosecctv.databinding.ActivityWebViewBinding
import kaist.software.mosecctv.viewmodel.WebViewViewModel

class WebViewActivity : AppCompatActivity() {

    private lateinit var webViewViewModel: WebViewViewModel

    private lateinit var binding: ActivityWebViewBinding
    private lateinit var webView: WebView
    private lateinit var progressBar: ProgressBar
    private lateinit var audioListView: RecyclerView
    private lateinit var audioAdapter: AudioAdapter
    private lateinit var processText: TextView

    private var audioFlag = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        webView = binding.webView
        progressBar = binding.progressBar
        processText = binding.processText

        webView.apply {
            settings.javaScriptEnabled = true
            webViewClient = MyWebViewClient()
            webChromeClient = WebChromeClient()
        }

        webViewViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(WebViewViewModel::class.java)
        webViewViewModel.url.observe(this, {
            webView.loadUrl(it)
        })

        audioListView = binding.audioListView
        audioListView.setHasFixedSize(true)
        audioListView.itemAnimator = null
        val linearLayoutManager = LinearLayoutManager(this)
        audioListView.layoutManager = linearLayoutManager
        audioAdapter = AudioAdapter()
        audioListView.adapter = audioAdapter
        audioAdapter.onItemClickListener = object : AudioAdapter.OnItemClickListener{
            override fun onItemClick(audioData: AudioData) {
                if(audioFlag){
                    webViewViewModel.playAudio(audioData)
                }else{
                    Toast.makeText(baseContext, "음원이 재생중입니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        webViewViewModel.audioData.observe(this, {
            if(it.state == true){
                processText.text = "현재 ${it.file_name} 재생중 입니다."
                audioFlag = false
            }else{
                processText.text = "재생할 음원을 선택해주세요."
                audioFlag = true
            }
        })

        webViewViewModel.audioDataList.observe(this, {
            audioAdapter.setAudioDataList(it)
        })

    }

    inner class MyWebViewClient() : WebViewClient() {
        override fun onPageCommitVisible(view: WebView?, url: String?) {
            super.onPageCommitVisible(view, url)
            progressBar.visibility = View.GONE
        }
    }

}