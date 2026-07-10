package com.munecat.pokemon.domain.usecase.battle

import com.munecat.pokemon.domain.model.Pokemon
import com.munecat.pokemon.domain.model.battle.BattlePokemon
import com.munecat.pokemon.domain.model.battle.BattleState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExecuteTurnUseCaseTest {

    private val executeTurnUseCase = ExecuteTurnUseCase()

    private fun createMockPokemon(id: Int, name: String, hp: Int, attack: Int, type: String): Pokemon {
        return Pokemon(
            id,
            name,
            "",
            "",
            "",
            "",
            listOf(type),
            hp,
            attack,
            50,
            50
        )
    }

    @Test
    fun `attacking should decrease defender HP`() {

        val playerPokemon = createMockPokemon(1, "Attacker", 100, 50, "fire")
        val opponentPokemon = createMockPokemon(2, "Defender", 100, 10, "grass")
        
        val initialState = BattleState(
            playerTeam = listOf(BattlePokemon.fromPokemon(playerPokemon)),
            opponentTeam = listOf(BattlePokemon.fromPokemon(opponentPokemon)),
            isPlayerTurn = true
        )

        val newState = executeTurnUseCase(initialState)

        val defenderAfter = newState.opponentTeam[0]
        assertTrue("HP should decrease after attack", defenderAfter.currentHp < 100)
        assertEquals("It should be opponent's turn now", false, newState.isPlayerTurn)
    }

    @Test
    fun `fire attack against grass should be super effective`() {

        val firePokemon = createMockPokemon(1, "Fire", 100, 50, "fire")
        val grassPokemon = createMockPokemon(2, "Grass", 100, 10, "grass")
        val normalPokemon = createMockPokemon(3, "Normal", 100, 10, "normal")

        val stateVsGrass = BattleState(
            playerTeam = listOf(BattlePokemon.fromPokemon(firePokemon)),
            opponentTeam = listOf(BattlePokemon.fromPokemon(grassPokemon)),
            isPlayerTurn = true
        )
        
        val stateVsNormal = BattleState(
            playerTeam = listOf(BattlePokemon.fromPokemon(firePokemon)),
            opponentTeam = listOf(BattlePokemon.fromPokemon(normalPokemon)),
            isPlayerTurn = true
        )

        val resultVsGrass = executeTurnUseCase(stateVsGrass)
        val resultVsNormal = executeTurnUseCase(stateVsNormal)

        val damageToGrass = 100 - resultVsGrass.opponentTeam[0].currentHp
        val damageToNormal = 100 - resultVsNormal.opponentTeam[0].currentHp

        assertTrue("Damage to grass ($damageToGrass) should be higher than to normal ($damageToNormal)", 
            damageToGrass > damageToNormal)
        assertTrue(resultVsGrass.battleLog.last().message.contains("super effective", ignoreCase = true))
    }


    //@FlakyTest - может падать с шансом 25% из-за механики dodge, оставил как есть чтобы не усложнять.
    // can be failed with 25% chance because of dodge mechanics
    @Test
    fun `battle should end when all opponent pokemon faint`() {

        val strongPokemon = createMockPokemon(1, "Strong", 100, 999, "fire")
        val weakPokemon = createMockPokemon(2, "Weak", 1, 1, "grass")

        val initialState = BattleState(
            playerTeam = listOf(BattlePokemon.fromPokemon(strongPokemon)),
            opponentTeam = listOf(BattlePokemon.fromPokemon(weakPokemon)),
            isPlayerTurn = true
        )

        val newState = executeTurnUseCase(initialState)

        assertTrue("Battle should be over", newState.isBattleOver)
        assertEquals("Player should be winner", true, newState.playerWon)
        assertTrue(newState.battleLog.any { it.message.contains("Battle over") })
    }
}
