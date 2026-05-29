package com.munecat.pokemon.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [PokemonDbModel::class],
    version = 1,
    exportSchema = false
)
abstract class PokemonDatabase : RoomDatabase() {
    abstract fun pokemonDao(): PokemonDao
}