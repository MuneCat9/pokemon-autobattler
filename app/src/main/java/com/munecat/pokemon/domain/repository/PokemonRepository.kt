package com.munecat.pokemon.domain.repository

import com.munecat.pokemon.domain.model.Pokemon
import kotlinx.coroutines.flow.Flow

interface PokemonRepository {

    fun getAllPokemon(): Flow<List<Pokemon>>

    suspend fun updateTeamStatus(pokemonId: Int, isInTeam: Boolean)

    fun getTeam(): Flow<List<Pokemon>>

    suspend fun refreshPokemonData()
}