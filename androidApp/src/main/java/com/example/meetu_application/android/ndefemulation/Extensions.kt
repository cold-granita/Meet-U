package com.example.meetu_application.android.ndefemulation

internal fun Byte.toPositiveInt() = toInt() and 0xFF

internal fun defineByteArrayOf(vararg byte: Int) = byte.map { it.toByte() }.toByteArray()
