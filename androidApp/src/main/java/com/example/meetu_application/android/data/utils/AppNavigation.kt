package com.example.meetu_application.android.data.utils

import android.util.Log
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.meetu_application.android.data.nfc.NFCWriter
import com.example.meetu_application.android.data.storage.loadCardsFromWallet
import com.example.meetu_application.android.ui.screens.CardDetailScreen
import com.example.meetu_application.android.ui.screens.CardEditScreen
import com.example.meetu_application.android.ui.screens.MainScreen
import com.example.meetu_application.android.ui.screens.NfcReaderScreen
import com.example.meetu_application.android.ui.screens.NfcWriterScreen
import com.example.meetu_application.android.ui.screens.WalletScreen

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalAnimationApi::class)
@Composable
fun AppNavigation(
    navController: NavHostController,
    onVCardReady: (String) -> Unit,
    onWriterReady: (NFCWriter, (String) -> Unit) -> Unit
){
    SharedTransitionLayout {
        NavHost(navController = navController, startDestination = "main") {
            composable("main") { MainScreen(navController) }
            composable("wallet") { WalletScreen(navController) }

            composable("cardDetail/{cardId}") { backStackEntry ->
                val cardId = backStackEntry.arguments?.getString("cardId")
                Log.d("DEBUG", "Clicked card id APP NAVIGATION: ${cardId}")

                val context = LocalContext.current
                val cards = remember { loadCardsFromWallet(context) }
                val card = cards.find{it.id == cardId}

                if (card != null) {
                    CardDetailScreen(card = card, navController = navController,  onVCardReady = onVCardReady)
                } else {
                    Text("Errore: carta non trovata per id $cardId")
                }
            }
            composable("nfcWriter"){NfcWriterScreen(navController,onWriterReady)}
            composable("nfcReader"){NfcReaderScreen(navController)}
            composable("cardEdit/{cardId}") { backStackEntry ->
                val cardId = backStackEntry.arguments?.getString("cardId")

                val context = LocalContext.current
                val cards = remember { loadCardsFromWallet(context) }
                val card = cards.find { it.id == cardId }

                if (card != null) {
                    CardEditScreen(navController = navController, card = card)
                } else {
                    Text("Errore: carta non trovata per id $cardId")
                }
            }
        }
    }
}
