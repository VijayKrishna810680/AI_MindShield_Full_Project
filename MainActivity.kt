package com.aimindshield

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.util.Log
import android.widget.Toast
import androidx.camera.view.PreviewView
import java.io.File
import kotlin.concurrent.fixedRateTimer
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private val CAMERA_PERMISSION_CODE = 100
    private lateinit var previewView: PreviewView
    private val emotions = arrayOf("happy", "sad", "neutral", "angry")
    private lateinit var logFile: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        previewView = PreviewView(this)
        setContentView(previewView)

        logFile = File(filesDir, "emotion_logs.csv")
        if (!logFile.exists()) {
            logFile.writeText("timestamp,emotion\n")
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
        } else {
            startCamera()
            startVPNService()
            startEmotionLogging()
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview)
            } catch (e: Exception) {
                Log.e("CameraX", "Use case binding failed", e)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun startVPNService() {
        val intent = android.content.Intent(this, VPNService::class.java)
        startService(intent)
    }

    private fun startEmotionLogging() {
        fixedRateTimer("emotionLogger", false, 0L, 10000L) {
            val currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            val randomEmotion = emotions.random()
            val logLine = "$currentTime,$randomEmotion\n"
            logFile.appendText(logLine)
            runOnUiThread {
                Toast.makeText(this@MainActivity, "Logged emotion: $randomEmotion", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                startCamera()
                startVPNService()
                startEmotionLogging()
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
