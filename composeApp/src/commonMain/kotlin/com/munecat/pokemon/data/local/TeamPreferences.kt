package com.munecat.pokemon.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.first

expect fun createDataStore(): DataStore<Preferences>

class TeamPreferences(private val dataStore: DataStore<Preferences>) {

    companion object {
        val SLOT_0 = intPreferencesKey("slot_0")
        val SLOT_1 = intPreferencesKey("slot_1")
        val SLOT_2 = intPreferencesKey("slot_2")
        const val EMPTY_SLOT = -1
    }

    suspend fun getSlots(): List<Int> {
        val prefs = dataStore.data.first()
        return listOf(
            prefs[SLOT_0] ?: EMPTY_SLOT,
            prefs[SLOT_1] ?: EMPTY_SLOT,
            prefs[SLOT_2] ?: EMPTY_SLOT
        )
    }

    suspend fun saveSlot(index: Int, pokemonId: Int) {
        val key = when (index) {
            0 -> SLOT_0
            1 -> SLOT_1
            2 -> SLOT_2
            else -> return
        }
        dataStore.edit { prefs ->
            prefs[key] = pokemonId
        }
    }

    suspend fun clearSlot(index: Int) {
        saveSlot(index, EMPTY_SLOT)
    }

    suspend fun clearAll() {
        dataStore.edit { prefs ->
            prefs[SLOT_0] = EMPTY_SLOT
            prefs[SLOT_1] = EMPTY_SLOT
            prefs[SLOT_2] = EMPTY_SLOT
        }
    }
}
