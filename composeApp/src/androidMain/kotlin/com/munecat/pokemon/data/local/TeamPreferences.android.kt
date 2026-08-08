package com.munecat.pokemon.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath
import org.koin.core.context.GlobalContext

actual fun createDataStore(): DataStore<Preferences> {
    val context = GlobalContext.get().get<Context>()
    return PreferenceDataStoreFactory.createWithPath(
        produceFile = {
            context.filesDir.resolve("datastore/team_prefs.preferences_pb").absolutePath.toPath()
        }
    )
}
