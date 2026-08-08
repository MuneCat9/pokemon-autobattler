package com.munecat.pokemon.data.local

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import org.koin.core.context.GlobalContext

actual fun getDatabaseBuilder(): RoomDatabase.Builder<PokemonDatabase> {
    val context = GlobalContext.get().get<Context>()
    val dbFile = context.getDatabasePath("pokemon.db")
    return Room.databaseBuilder<PokemonDatabase>(
        context = context,
        name = dbFile.absolutePath
    )
}
