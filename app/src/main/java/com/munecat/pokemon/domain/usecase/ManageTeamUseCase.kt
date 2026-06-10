package com.munecat.pokemon.domain.usecase

import com.munecat.pokemon.domain.model.Pokemon
import com.munecat.pokemon.domain.repository.PokemonRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ManageTeamUseCase @Inject constructor(
    private val repository: PokemonRepository
) {
    suspend fun addToTeam(pokemon: Pokemon) {
        val currentTeam = repository.getTeam().first()

        if (currentTeam.size >= 3) {
            throw TeamFullException()
        }
        if (currentTeam.any { it.id == pokemon.id }) {
            throw PokemonAlreadyInTeamException()
        }
        repository.updateTeamStatus(pokemon.id, isInTeam = true)
    }

    suspend fun removeFromTeam(pokemonId: Int) {
        repository.updateTeamStatus(pokemonId, isInTeam = false)
    }

    fun getTeam(): Flow<List<Pokemon>> {
        return repository.getTeam()
    }

    suspend fun refreshDataIfEmpty() {
        repository.refreshPokemonData()
    }
}

class TeamFullException : Exception("There are already 3 pokemon in team!")
class PokemonAlreadyInTeamException : Exception("This pokemon is already in team!")