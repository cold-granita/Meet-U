package com.example.meetu_application.android.ndefemulation.ndef
data class WifiNetworkNdefData(
    val wifiName: String,
    val wifiProtection: WifiNetworkNdefDataProtectionType,
    val wifiPassword: String? = null,
) : NdefData()