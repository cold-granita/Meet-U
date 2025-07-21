package com.example.meetu_application.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.meetu_application.android.data.utils.AppNavigation
import com.example.meetu_application.android.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            MyApplicationTheme {
                AppNavigation(
                    navController = navController,
                    onWriterReady = { nfcWriter, statusCallback ->
                        statusCallback("Writer NFC pronto")
                      })
            }

        }
    }
}

