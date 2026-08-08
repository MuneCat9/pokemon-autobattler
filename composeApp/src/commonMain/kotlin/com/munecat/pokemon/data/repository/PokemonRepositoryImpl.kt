package com.munecat.pokemon.data.repository

import com.munecat.pokemon.data.local.PokemonDao
import com.munecat.pokemon.data.mapper.toDbModel
import com.munecat.pokemon.data.mapper.toModel
import com.munecat.pokemon.data.remote.PokemonApi
import com.munecat.pokemon.domain.model.Pokemon
import com.munecat.pokemon.domain.repository.PokemonRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class PokemonRepositoryImpl constructor(
    private val apiService: PokemonApi,
    private val pokemonDao: PokemonDao
) : PokemonRepository {

    override fun getAllPokemon(): Flow<List<Pokemon>> {
        return pokemonDao.getAllPokemon().map { entities ->
            entities.map { it.toModel() }
        }
    }

    override suspend fun updateTeamStatus(pokemonId: Int, isInTeam: Boolean) {
        pokemonDao.updateTeamStatus(pokemonId, isInTeam)
    }

    override fun getTeam(): Flow<List<Pokemon>> {
        return pokemonDao.getTeam().map { entities ->
            entities.map { it.toModel() }
        }
    }

    override suspend fun refreshPokemonData() {
        val existingCount = pokemonDao.getAllPokemon().first().size
        if (existingCount >= 251) {
            return
        }

        val listResponse = apiService.fetchPokemonList(limit = 251)
        val existingPokemon = pokemonDao.getAllPokemon().first().associateBy { it.id }

        val newEntities = listResponse.results.map { result ->
            val id = result.url.trimEnd('/').split('/').last()

            val detail = apiService.fetchPokemonDetail(id)
            val entityDto = detail.toDbModel()

            val existing = existingPokemon[id.toInt()]
            if (existing != null) {
                entityDto.copy(isInTeam = existing.isInTeam)
            } else {
                entityDto
            }
        }
        pokemonDao.insertAll(newEntities)
    }
}
