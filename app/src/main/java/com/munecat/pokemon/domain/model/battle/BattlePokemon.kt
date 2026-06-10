package com.munecat.pokemon.domain.model.battle

import com.munecat.pokemon.domain.model.Pokemon

data class BattlePokemon(
    val pokemon: Pokemon,
    val currentHp: Int,
    val maxHp: Int,
    val isFainted: Boolean = false
) {
    companion object {
        fun fromPokemon(pokemon: Pokemon): BattlePokemon {
            return BattlePokemon(
                pokemon = pokemon,
                currentHp = pokemon.hp,
                maxHp = pokemon.hp,
                isFainted = false
            )
        }
    }
}
