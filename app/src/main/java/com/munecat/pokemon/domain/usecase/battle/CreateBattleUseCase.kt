package com.munecat.pokemon.domain.usecase.battle

import com.munecat.pokemon.domain.model.Pokemon
import com.munecat.pokemon.domain.model.battle.BattlePokemon
import com.munecat.pokemon.domain.model.battle.BattleState
import com.munecat.pokemon.domain.repository.PokemonRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class CreateBattleUseCase @Inject constructor(
    private val repository: PokemonRepository
) {
    suspend operator fun invoke(
        playerTeam: List<Pokemon>,
        opponentIds: List<Int>? = null
    ): BattleState {
        val allPokemon = repository.getAllPokemon().first()

        val opponentTeam = if (opponentIds != null) {
            opponentIds.mapNotNull { id -> allPokemon.find { it.id == id } }
        } else {
            allPokemon.shuffled().take(3)
        }.map { BattlePokemon.fromPokemon(it) }

        val playerBattleTeam = playerTeam.map { BattlePokemon.fromPokemon(it) }

        return BattleState(
            playerTeam = playerBattleTeam,
            opponentTeam = opponentTeam,
            isPlayerTurn = playerBattleTeam.first().pokemon.speed >= opponentTeam.first().pokemon.speed
        )
    }
}