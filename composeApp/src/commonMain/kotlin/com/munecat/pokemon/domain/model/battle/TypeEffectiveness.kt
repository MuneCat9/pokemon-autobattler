package com.munecat.pokemon.domain.model.battle

import com.munecat.pokemon.domain.model.battle.PokemonType.BUG
import com.munecat.pokemon.domain.model.battle.PokemonType.DARK
import com.munecat.pokemon.domain.model.battle.PokemonType.DRAGON
import com.munecat.pokemon.domain.model.battle.PokemonType.ELECTRIC
import com.munecat.pokemon.domain.model.battle.PokemonType.FAIRY
import com.munecat.pokemon.domain.model.battle.PokemonType.FIGHTING
import com.munecat.pokemon.domain.model.battle.PokemonType.FIRE
import com.munecat.pokemon.domain.model.battle.PokemonType.FLYING
import com.munecat.pokemon.domain.model.battle.PokemonType.GHOST
import com.munecat.pokemon.domain.model.battle.PokemonType.GRASS
import com.munecat.pokemon.domain.model.battle.PokemonType.GROUND
import com.munecat.pokemon.domain.model.battle.PokemonType.ICE
import com.munecat.pokemon.domain.model.battle.PokemonType.NORMAL
import com.munecat.pokemon.domain.model.battle.PokemonType.POISON
import com.munecat.pokemon.domain.model.battle.PokemonType.PSYCHIC
import com.munecat.pokemon.domain.model.battle.PokemonType.ROCK
import com.munecat.pokemon.domain.model.battle.PokemonType.STEEL
import com.munecat.pokemon.domain.model.battle.PokemonType.WATER

enum class PokemonType {
    NORMAL, FIGHTING, FLYING, POISON, GROUND, ROCK,
    BUG, GHOST, STEEL, FIRE, WATER, GRASS,
    ELECTRIC, PSYCHIC, ICE, DRAGON, DARK, FAIRY;

    companion object {
        fun fromString(type: String): PokemonType? {
            return try {
                valueOf(type.uppercase())
            } catch (e: IllegalArgumentException) {
                null
            }
        }
    }
}

object TypeEffectiveness {
    private fun eff(vararg pairs: Pair<PokemonType, Float>) = mapOf(*pairs)

    private val chart: Map<PokemonType, Map<PokemonType, Float>> = mapOf(
        NORMAL   to eff(ROCK to 0.75f, STEEL to 0.75f, GHOST to 0.5f),
        FIGHTING to eff(NORMAL to 1.5f, ROCK to 1.5f, STEEL to 1.5f, ICE to 1.5f, DARK to 1.5f, FLYING to 0.75f, POISON to 0.75f, BUG to 0.75f, PSYCHIC to 0.75f, FAIRY to 0.75f, GHOST to 0.5f),
        FLYING   to eff(FIGHTING to 1.5f, BUG to 1.5f, GRASS to 1.5f, ROCK to 0.75f, STEEL to 0.75f, ELECTRIC to 0.75f),
        POISON   to eff(GRASS to 1.5f, FAIRY to 1.5f, POISON to 0.75f, GROUND to 0.75f, ROCK to 0.75f, GHOST to 0.75f, STEEL to 0.5f),
        GROUND   to eff(POISON to 1.5f, ROCK to 1.5f, STEEL to 1.5f, FIRE to 1.5f, ELECTRIC to 1.5f, GRASS to 0.75f, BUG to 0.75f, FLYING to 0.5f),
        ROCK     to eff(FLYING to 1.5f, BUG to 1.5f, FIRE to 1.5f, ICE to 1.5f, FIGHTING to 0.75f, GROUND to 0.75f, STEEL to 0.75f),
        BUG      to eff(GRASS to 1.5f, PSYCHIC to 1.5f, DARK to 1.5f, FIGHTING to 0.75f, FLYING to 0.75f, POISON to 0.75f, GHOST to 0.75f, STEEL to 0.75f, FIRE to 0.75f, FAIRY to 0.75f),
        GHOST    to eff(PSYCHIC to 1.5f, GHOST to 1.5f, DARK to 0.75f, NORMAL to 0.5f),
        STEEL    to eff(ROCK to 1.5f, ICE to 1.5f, FAIRY to 1.5f, STEEL to 0.75f, FIRE to 0.75f, WATER to 0.75f, ELECTRIC to 0.75f),
        FIRE     to eff(GRASS to 1.5f, ICE to 1.5f, BUG to 1.5f, STEEL to 1.5f, FIRE to 0.75f, WATER to 0.75f, ROCK to 0.75f, DRAGON to 0.75f),
        WATER    to eff(FIRE to 1.5f, GROUND to 1.5f, ROCK to 1.5f, WATER to 0.75f, GRASS to 0.75f, DRAGON to 0.75f),
        GRASS    to eff(WATER to 1.5f, GROUND to 1.5f, ROCK to 1.5f, FIRE to 0.75f, GRASS to 0.75f, POISON to 0.75f, FLYING to 0.75f, BUG to 0.75f, DRAGON to 0.75f, STEEL to 0.75f),
        ELECTRIC to eff(WATER to 1.5f, FLYING to 1.5f, GRASS to 0.75f, ELECTRIC to 0.75f, DRAGON to 0.75f, GROUND to 0.5f),
        PSYCHIC  to eff(FIGHTING to 1.5f, POISON to 1.5f, PSYCHIC to 0.75f, STEEL to 0.75f, DARK to 0.5f),
        ICE      to eff(GRASS to 1.5f, GROUND to 1.5f, FLYING to 1.5f, DRAGON to 1.5f, FIRE to 0.75f, WATER to 0.75f, ICE to 0.75f, STEEL to 0.75f),
        DRAGON   to eff(DRAGON to 1.5f, STEEL to 0.75f, FAIRY to 0.5f),
        DARK     to eff(PSYCHIC to 1.5f, GHOST to 1.5f, FIGHTING to 0.75f, DARK to 0.75f, FAIRY to 0.75f),
        FAIRY    to eff(FIGHTING to 1.5f, DRAGON to 1.5f, DARK to 1.5f, FIRE to 0.75f, POISON to 0.75f, STEEL to 0.75f)
    )

    fun getMultiplier(attackingType: PokemonType, defendingType: PokemonType): Float {
        return chart[attackingType]?.get(defendingType) ?: 1f
    }

    fun getMultiplier(attackingType: PokemonType, defendingTypes: List<PokemonType>): Float {
        if (defendingTypes.isEmpty()) return 1f
        return defendingTypes.fold(1f) { acc, type -> acc * getMultiplier(attackingType, type) }
    }
}
