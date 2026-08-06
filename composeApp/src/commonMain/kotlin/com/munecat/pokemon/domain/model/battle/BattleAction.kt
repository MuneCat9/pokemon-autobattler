package com.munecat.pokemon.domain.model.battle

sealed interface BattleAction {
    data class Attack(
        val attacker: BattlePokemon,
        val defender: BattlePokemon,
        val damage: Int,
        val isEffective: Boolean = false
    ) : BattleAction

    data class Fainted(
        val pokemon: BattlePokemon
    ) : BattleAction
}