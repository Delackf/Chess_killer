package com.chessoverlay

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var permissionButton: Button
    private lateinit var startOverlayButton: Button
    private lateinit var stopOverlayButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setupClickListeners()
        updateButtonStates()
    }

    private fun initViews() {
        permissionButton = findViewById(R.id.permissionButton)
        startOverlayButton = findViewById(R.id.startOverlayButton)
        stopOverlayButton = findViewById(R.id.stopOverlayButton)
    }

    private fun setupClickListeners() {
        permissionButton.setOnClickListener {
            requestOverlayPermission()
        }

        startOverlayButton.setOnClickListener {
            if (hasOverlayPermission()) {
                startOverlayService()
            } else {
                Toast.makeText(this, getString(R.string.permission_required), Toast.LENGTH_LONG).show()
            }
        }

        stopOverlayButton.setOnClickListener {
            stopOverlayService()
        }
    }

    private fun hasOverlayPermission(): Boolean {
        return Settings.canDrawOverlays(this)
    }

    private fun requestOverlayPermission() {
        if (!hasOverlayPermission()) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            startActivityForResult(intent, OVERLAY_PERMISSION_REQUEST_CODE)
        } else {
            Toast.makeText(this, "الإذن ممنوح بالفعل", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startOverlayService() {
        val intent = Intent(this, OverlayService::class.java)
        startForegroundService(intent)
        Toast.makeText(this, "تم تشغيل الطبقة العائمة", Toast.LENGTH_SHORT).show()
        updateButtonStates()
    }

    private fun stopOverlayService() {
        val intent = Intent(this, OverlayService::class.java)
        stopService(intent)
        Toast.makeText(this, "تم إيقاف الطبقة العائمة", Toast.LENGTH_SHORT).show()
        updateButtonStates()
    }

    private fun updateButtonStates() {
        val hasPermission = hasOverlayPermission()
        permissionButton.isEnabled = !hasPermission
        startOverlayButton.isEnabled = hasPermission
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == OVERLAY_PERMISSION_REQUEST_CODE) {
            updateButtonStates()
            if (hasOverlayPermission()) {
                Toast.makeText(this, "تم منح الإذن بنجاح", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateButtonStates()
    }

    companion object {
        private const val OVERLAY_PERMISSION_REQUEST_CODE = 1001
    }
}
