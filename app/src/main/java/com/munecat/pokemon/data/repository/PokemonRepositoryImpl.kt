package com.munecat.pokemon.data.repository

import com.munecat.pokemon.data.local.PokemonDao
import com.munecat.pokemon.data.mapper.toDbModel
import com.munecat.pokemon.data.mapper.toModel
import com.munecat.pokemon.data.remote.PokeApiService
import com.munecat.pokemon.domain.model.Pokemon
import com.munecat.pokemon.domain.repository.PokemonRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PokemonRepositoryImpl @Inject constructor(
    private val apiService: PokeApiService,
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
        val listResponse = apiService.getPokemonList(limit = 151)
        listResponse.results.forEach { result ->
            val id = result.url.trimEnd('/').split('/').last().toInt()
            val detail = apiService.getPokemonDetail(id)
            val entityDto = detail.toDbModel()
            pokemonDao.insertAll(listOf(entityDto))
        }
    }
}