package com.example.meetu_application.android.data.storage

import android.content.Context
import com.example.meetu_application.android.data.model.Card
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

const val PREFS_NAME = "wallet"
const val PREFS_KEY = "saved_cards"


fun saveCardsToWallet(context: Context, cards: List<Card>) {
    val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val editor = sharedPref.edit()
    val json = Gson().toJson(cards)
    editor.putString(PREFS_KEY, json)
    editor.apply()
}

fun loadCardsFromWallet(context: Context): List<Card> {
    val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val json = sharedPref.getString(PREFS_KEY, null)
    return if (json != null) {
        val type = object : TypeToken<List<Card>>() {}.type
        Gson().fromJson(json, type)
    } else {
        emptyList()
    }
}

fun saveUpdatedCard(context: Context, updatedCard: Card) {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val cards = loadCardsFromWallet(context).toMutableList()
    val index = cards.indexOfFirst { it.id == updatedCard.id }
    if (index != -1) {
        cards[index] = updatedCard
        val json = Gson().toJson(cards)
        prefs.edit().putString(PREFS_KEY, json).apply()
    }
}

fun deleteCard(context: Context, cardToDelete: Card) {
    val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val gson = Gson()
    val json = sharedPreferences.getString(PREFS_KEY, null)
    val type = object : TypeToken<List<Card>>() {}.type
    val cards: List<Card> = gson.fromJson(json, type) ?: emptyList()

    val updatedCards = cards.filterNot { it == cardToDelete }
    val updatedJson = gson.toJson(updatedCards)

    sharedPreferences.edit().putString(PREFS_KEY, updatedJson).apply()
}
