package com.example.meetu_application.android.data.storage

import android.content.Context
import com.example.meetu_application.android.data.model.Card
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

const val PREFS_NAME = "wallet"
const val PREFS_KEY = "saved_cards"


fun saveCardsToWallet(context: Context, cards: List<Card>) {
    val prefs = getSecurePrefs(context)
    val json = Gson().toJson(cards)
    prefs.edit().putString(PREFS_KEY, json).apply()
}


fun loadCardsFromWallet(context: Context): List<Card> {
    //migratePrefsToEncrypted(context) se cambio cripting
    val prefs = getSecurePrefs(context)
    val json = prefs.getString(PREFS_KEY, null)
    return if (json != null) {
        val type = object : TypeToken<List<Card>>() {}.type
        Gson().fromJson(json, type)
    } else emptyList()
}

fun saveUpdatedCard(context: Context, updatedCard: Card) {
    val prefs = getSecurePrefs(context)
    val cards = loadCardsFromWallet(context).toMutableList()
    val index = cards.indexOfFirst { it.id == updatedCard.id }
    if (index != -1) {
        cards[index] = updatedCard
        val json = Gson().toJson(cards)
        prefs.edit().putString(PREFS_KEY, json).apply()
    }
}

fun deleteCard(context: Context, cardToDelete: Card) {
    val prefs = getSecurePrefs(context)
    val cards = loadCardsFromWallet(context).filterNot { it == cardToDelete }
    prefs.edit().putString(PREFS_KEY, Gson().toJson(cards)).apply()
}
/*
fun migratePrefsToEncrypted(context: Context) {
    val normalPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val encryptedPrefs = getSecurePrefs(context)

    val json = normalPrefs.getString(PREFS_KEY, null)
    Log.d("MIGRATE","Dati normali: $json")

    if (json != null) {
        encryptedPrefs.edit().putString(PREFS_KEY, json).apply()
        Log.d("MIGRATE","Dati migrati!")
        normalPrefs.edit().remove(PREFS_KEY).apply()
    } else {
        Log.d("MIGRATE","Nessun dato trovato nei prefs normali")
    }
}
*/