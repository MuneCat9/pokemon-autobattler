package com.munecat.pokemon.data.local

import androidx.room.Room
import androidx.room.RoomDatabase
import java.io.File

actual fun getDatabaseBuilder(): RoomDatabase.Builder<PokemonDatabase> {
    val dbFile = File(System.getProperty("java.io.tmpdir"), "pokemon.db")
    return Room.databaseBuilder<PokemonDatabase>(
        name = dbFile.absolutePath
    )
}