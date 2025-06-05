package com.aimindshield

import android.app.Service
import android.content.Intent
import android.net.VpnService
import android.os.IBinder
import android.util.Log

class VPNService : VpnService() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i("VPNService", "VPN Service Started - Firewall shield active")
        // Stub VPN setup for demo (no real blocking implemented here)
        return Service.START_STICKY
    }

    override fun onDestroy() {
        Log.i("VPNService", "VPN Service Destroyed")
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
