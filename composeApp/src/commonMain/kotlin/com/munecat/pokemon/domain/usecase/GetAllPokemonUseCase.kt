package com.munecat.pokemon.domain.usecase

import com.munecat.pokemon.domain.model.Pokemon
import com.munecat.pokemon.domain.repository.PokemonRepository
import kotlinx.coroutines.flow.Flow

class GetAllPokemonUseCase(
    private val repository: PokemonRepository
) {
    operator fun invoke(): Flow<List<Pokemon>> {
        return repository.getAllPokemon()
    }
}
