package com.example.meetu_application.android.data.storage

import android.content.Context
import com.example.meetu_application.android.data.model.Card
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

fun saveCardsToWallet(context: Context, cards: List<Card>) {
    val sharedPref = context.getSharedPreferences("wallet", Context.MODE_PRIVATE)
    val editor = sharedPref.edit()
    val json = Gson().toJson(cards)
    editor.putString("saved_cards", json)
    editor.apply()
}

fun loadCardsFromWallet(context: Context): List<Card> {
    val sharedPref = context.getSharedPreferences("wallet", Context.MODE_PRIVATE)
    val json = sharedPref.getString("saved_cards", null)
    return if (json != null) {
        val type = object : TypeToken<List<Card>>() {}.type
        Gson().fromJson(json, type)
    } else {
        emptyList()
    }
}
