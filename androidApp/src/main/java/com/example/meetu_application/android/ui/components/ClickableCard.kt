package com.example.meetu_application.android.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.meetu_application.android.data.model.Card

@Composable
fun ClickableCard(
    card: Card,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    bottomGradientAlpha: Float = 1f,
    contentAlpha: Float = 1f,
    isSelectable: Boolean = false,
    isSelected: Boolean = false,
    onSelectToggle: ((Card) -> Unit)? = null
) {
    val borderModifier = if (isSelectable && isSelected) {
        Modifier.border(3.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp))
    } else Modifier

    CardView(
        card = card,
        modifier = modifier
            .then(borderModifier)
            .pointerInput(card.id) {
                detectTapGestures(
                    onTap = {
                        if (isSelectable) {
                            onSelectToggle?.invoke(card)
                        } else {
                            navController.navigate("cardDetail/${card.id}")
                        }
                    },
                    onLongPress = {
                        if (!isSelectable) {
                            onSelectToggle?.invoke(card)
                        }
                    }
                )
            },
        bottomGradientAlpha = bottomGradientAlpha,
        contentAlpha = contentAlpha
    )
}

