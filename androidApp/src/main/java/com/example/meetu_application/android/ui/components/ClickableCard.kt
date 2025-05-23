package com.example.meetu_application.android.ui.components

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.navigation.NavHostController
import com.example.meetu_application.android.data.model.Card

@Composable
fun ClickableCard(
    card: Card,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    bottomGradientAlpha: Float = 1f,
    contentAlpha: Float = 1f
) {
    CardView(
        card = card,
        modifier = modifier.pointerInput(card.id) {
            detectTapGestures(
                onTap = {
                    navController.navigate("cardDetail/${card.id}")
                    //(navController as? NavHostController)?.navigate("cardDetail/${card.id}")
                },
                onLongPress = {
                    navController.navigate("cardEdit/${card.id}")
                    //(navController as? NavHostController)?.navigate("cardDetail/${card.id}")
                }
            )
        },
        bottomGradientAlpha = bottomGradientAlpha,
        contentAlpha = contentAlpha
    )
}
