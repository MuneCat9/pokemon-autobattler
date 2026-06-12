package com.munecat.pokemon.domain.model.battle

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
    private val chart: Map<PokemonType, Map<PokemonType, Float>> = mapOf(
        PokemonType.NORMAL to mapOf(
            PokemonType.ROCK to 0.75f, PokemonType.STEEL to 0.75f, PokemonType.GHOST to 0.5f
        ),
        PokemonType.FIGHTING to mapOf(
            PokemonType.NORMAL to 1.5f, PokemonType.ROCK to 1.5f, PokemonType.STEEL to 1.5f,
            PokemonType.ICE to 1.5f, PokemonType.DARK to 1.5f,
            PokemonType.FLYING to 0.75f, PokemonType.POISON to 0.75f, PokemonType.BUG to 0.75f,
            PokemonType.PSYCHIC to 0.75f, PokemonType.FAIRY to 0.75f, PokemonType.GHOST to 0.5f
        ),
        PokemonType.FLYING to mapOf(
            PokemonType.FIGHTING to 1.5f, PokemonType.BUG to 1.5f, PokemonType.GRASS to 1.5f,
            PokemonType.ROCK to 0.75f, PokemonType.STEEL to 0.75f, PokemonType.ELECTRIC to 0.75f
        ),
        PokemonType.POISON to mapOf(
            PokemonType.GRASS to 1.5f, PokemonType.FAIRY to 1.5f,
            PokemonType.POISON to 0.75f, PokemonType.GROUND to 0.75f,
            PokemonType.ROCK to 0.75f, PokemonType.GHOST to 0.75f, PokemonType.STEEL to 0.5f
        ),
        PokemonType.GROUND to mapOf(
            PokemonType.POISON to 1.5f, PokemonType.ROCK to 1.5f, PokemonType.STEEL to 1.5f,
            PokemonType.FIRE to 1.5f, PokemonType.ELECTRIC to 1.5f,
            PokemonType.GRASS to 0.75f, PokemonType.BUG to 0.75f, PokemonType.FLYING to 0.5f
        ),
        PokemonType.ROCK to mapOf(
            PokemonType.FLYING to 1.5f, PokemonType.BUG to 1.5f, PokemonType.FIRE to 1.5f, PokemonType.ICE to 1.5f,
            PokemonType.FIGHTING to 0.75f, PokemonType.GROUND to 0.75f, PokemonType.STEEL to 0.75f
        ),
        PokemonType.BUG to mapOf(
            PokemonType.GRASS to 1.5f, PokemonType.PSYCHIC to 1.5f, PokemonType.DARK to 1.5f,
            PokemonType.FIGHTING to 0.75f, PokemonType.FLYING to 0.75f, PokemonType.POISON to 0.75f,
            PokemonType.GHOST to 0.75f, PokemonType.STEEL to 0.75f, PokemonType.FIRE to 0.75f, PokemonType.FAIRY to 0.75f
        ),
        PokemonType.GHOST to mapOf(
            PokemonType.PSYCHIC to 1.5f, PokemonType.GHOST to 1.5f,
            PokemonType.DARK to 0.75f, PokemonType.NORMAL to 0.5f
        ),
        PokemonType.STEEL to mapOf(
            PokemonType.ROCK to 1.5f, PokemonType.ICE to 1.5f, PokemonType.FAIRY to 1.5f,
            PokemonType.STEEL to 0.75f, PokemonType.FIRE to 0.75f, PokemonType.WATER to 0.75f, PokemonType.ELECTRIC to 0.75f
        ),
        PokemonType.FIRE to mapOf(
            PokemonType.GRASS to 1.5f, PokemonType.ICE to 1.5f, PokemonType.BUG to 1.5f, PokemonType.STEEL to 1.5f,
            PokemonType.FIRE to 0.75f, PokemonType.WATER to 0.75f, PokemonType.ROCK to 0.75f, PokemonType.DRAGON to 0.75f
        ),
        PokemonType.WATER to mapOf(
            PokemonType.FIRE to 1.5f, PokemonType.GROUND to 1.5f, PokemonType.ROCK to 1.5f,
            PokemonType.WATER to 0.75f, PokemonType.GRASS to 0.75f, PokemonType.DRAGON to 0.75f
        ),
        PokemonType.GRASS to mapOf(
            PokemonType.WATER to 1.5f, PokemonType.GROUND to 1.5f, PokemonType.ROCK to 1.5f,
            PokemonType.FIRE to 0.75f, PokemonType.GRASS to 0.75f, PokemonType.POISON to 0.75f,
            PokemonType.FLYING to 0.75f, PokemonType.BUG to 0.75f, PokemonType.DRAGON to 0.75f, PokemonType.STEEL to 0.75f
        ),
        PokemonType.ELECTRIC to mapOf(
            PokemonType.WATER to 1.5f, PokemonType.FLYING to 1.5f,
            PokemonType.GRASS to 0.75f, PokemonType.ELECTRIC to 0.75f, PokemonType.DRAGON to 0.75f, PokemonType.GROUND to 0.5f
        ),
        PokemonType.PSYCHIC to mapOf(
            PokemonType.FIGHTING to 1.5f, PokemonType.POISON to 1.5f,
            PokemonType.PSYCHIC to 0.75f, PokemonType.STEEL to 0.75f, PokemonType.DARK to 0.5f
        ),
        PokemonType.ICE to mapOf(
            PokemonType.GRASS to 1.5f, PokemonType.GROUND to 1.5f, PokemonType.FLYING to 1.5f, PokemonType.DRAGON to 1.5f,
            PokemonType.FIRE to 0.75f, PokemonType.WATER to 0.75f, PokemonType.ICE to 0.75f, PokemonType.STEEL to 0.75f
        ),
        PokemonType.DRAGON to mapOf(
            PokemonType.DRAGON to 1.5f,
            PokemonType.STEEL to 0.75f, PokemonType.FAIRY to 0.5f
        ),
        PokemonType.DARK to mapOf(
            PokemonType.PSYCHIC to 1.5f, PokemonType.GHOST to 1.5f,
            PokemonType.FIGHTING to 0.75f, PokemonType.DARK to 0.75f, PokemonType.FAIRY to 0.75f
        ),
        PokemonType.FAIRY to mapOf(
            PokemonType.FIGHTING to 1.5f, PokemonType.DRAGON to 1.5f, PokemonType.DARK to 1.5f,
            PokemonType.FIRE to 0.75f, PokemonType.POISON to 0.75f, PokemonType.STEEL to 0.75f
        )
    )

    fun getMultiplier(attackingType: PokemonType, defendingType: PokemonType): Float {
        return chart[attackingType]?.get(defendingType) ?: 1f
    }

    fun getMultiplier(attackingType: PokemonType, defendingTypes: List<PokemonType>): Float {
        if (defendingTypes.isEmpty()) return 1f
        return defendingTypes.fold(1f) { acc, type -> acc * getMultiplier(attackingType, type) }
    }
}