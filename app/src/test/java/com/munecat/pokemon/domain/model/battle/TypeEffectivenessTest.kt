package com.munecat.pokemon.domain.model.battle

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class TypeEffectivenessTest {

    @Test
    fun `fromString should return correct type regardless of case`() {
        assertEquals(PokemonType.FIRE, PokemonType.fromString("FIRE"))
        assertEquals(PokemonType.FIRE, PokemonType.fromString("fire"))
        assertEquals(PokemonType.FIRE, PokemonType.fromString("Fire"))
    }

    @Test
    fun `fromString should return null for invalid types`() {
        assertNull(PokemonType.fromString("INVALID"))
        assertNull(PokemonType.fromString(""))
        assertNull(PokemonType.fromString("agumon"))
    }

    @Test
    fun `getMultiplier should return correct value for single type effectiveness`() {
        assertEquals(1.5f, TypeEffectiveness.getMultiplier(PokemonType.FIRE, PokemonType.GRASS))
        assertEquals(1.5f, TypeEffectiveness.getMultiplier(PokemonType.WATER, PokemonType.FIRE))

        assertEquals(0.75f, TypeEffectiveness.getMultiplier(PokemonType.FIRE, PokemonType.WATER))
        assertEquals(0.75f, TypeEffectiveness.getMultiplier(PokemonType.GRASS, PokemonType.FIRE))

        assertEquals(0.5f, TypeEffectiveness.getMultiplier(PokemonType.NORMAL, PokemonType.GHOST))

        assertEquals(1.0f, TypeEffectiveness.getMultiplier(PokemonType.NORMAL, PokemonType.FIRE))
    }

    @Test
    fun `getMultiplier should return correct value for dual type defending`() {
        val defendingTypes = listOf(PokemonType.BUG, PokemonType.GRASS)
        assertEquals(2.25f, TypeEffectiveness.getMultiplier(PokemonType.FLYING, defendingTypes))

        val defendingTypes2 = listOf(PokemonType.WATER, PokemonType.GROUND)
        assertEquals(0.75f, TypeEffectiveness.getMultiplier(PokemonType.FIRE, defendingTypes2))
    }

    @Test
    fun `getMultiplier should return 1_0 when defending types list is empty`() {
        assertEquals(1.0f, TypeEffectiveness.getMultiplier(PokemonType.FIRE, emptyList()))
    }
}
