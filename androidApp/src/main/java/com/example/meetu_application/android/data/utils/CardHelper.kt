package com.example.meetu_application.android.data.utils

import com.example.meetu_application.android.data.model.Card

fun setPreferredCard(cards: List<Card>, preferred: Card): List<Card> {
    return cards.map {
        if (it == preferred) it.copy(isPreferred = true)
        else it.copy(isPreferred = false)
    }
}
