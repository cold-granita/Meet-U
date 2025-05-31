package com.example.meetu_application.android.data.storage

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

object PreferenceManager {

    val PREFERRED_CARD_ID = stringPreferencesKey("preferred_card_id")

    suspend fun setPreferredCardId(context: Context, cardId: String) {
        context.dataStore.edit { prefs ->
            prefs[PREFERRED_CARD_ID] = cardId
        }
    }

    fun getPreferredCardId(context: Context): Flow<String?> {
        return context.dataStore.data.map { prefs ->
            prefs[PREFERRED_CARD_ID]
        }
    }
}
