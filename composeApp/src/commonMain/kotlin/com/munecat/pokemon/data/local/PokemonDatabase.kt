package com.munecat.pokemon.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor

@Database(entities = [PokemonDbModel::class], version = 1)
abstract class PokemonDatabase : RoomDatabase() {
    abstract fun pokemonDao(): PokemonDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object PokemonDatabaseConstructor : RoomDatabaseConstructor<PokemonDatabase>
