package com.munecat.pokemon.data.local

import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers

expect fun getDatabaseBuilder(): RoomDatabase.Builder<PokemonDatabase>

fun createDatabase(builder: RoomDatabase.Builder<PokemonDatabase>): PokemonDatabase {
    return builder
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}
