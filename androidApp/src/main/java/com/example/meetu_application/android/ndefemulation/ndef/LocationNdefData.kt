package com.example.meetu_application.android.ndefemulation.ndef

data class LocationNdefData (
    val latitude: Double,
    val longitude: Double,
) : NdefData()