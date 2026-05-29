package com.munecat.pokemon.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PokemonDao {
    @Query("SELECT * FROM pokemon")
    fun getAllPokemon(): Flow<List<PokemonDbModel>>

    @Query("SELECT * FROM pokemon WHERE isInTeam = 1")
    fun getTeam(): Flow<List<PokemonDbModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(pokemon: List<PokemonDbModel>)

    @Query("UPDATE pokemon SET isInTeam = :isInTeam WHERE id = :pokemonId")
    suspend fun updateTeamStatus(pokemonId: Int, isInTeam: Boolean)
}