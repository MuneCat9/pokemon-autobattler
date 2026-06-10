package com.munecat.pokemon.domain.usecase.battle

import com.munecat.pokemon.domain.model.battle.BattleAction
import com.munecat.pokemon.domain.model.battle.BattlePokemon
import com.munecat.pokemon.domain.model.battle.BattleState
import javax.inject.Inject

class ExecuteTurnUseCase @Inject constructor() {

    operator fun invoke(state: BattleState): BattleState {
        if (state.isBattleOver) return state

        val (attacker, defender, isPlayerAttacking) = getAttackerAndDefender(state)

        val baseDamage = (attacker.pokemon.attack * 0.8).toInt()
        val defenseReduction = (defender.pokemon.defense * 0.5).toInt()
        val damage = (baseDamage - defenseReduction).coerceAtLeast(1)

        val newHp = (defender.currentHp - damage).coerceAtLeast(0)
        val updatedDefender = defender.copy(
            currentHp = newHp,
            isFainted = newHp <= 0
        )

        val action = BattleAction.Attack(
            attacker = attacker,
            defender = updatedDefender,
            damage = damage
        )

        val logMessage = "${attacker.pokemon.name} dealt $damage damage to ${defender.pokemon.name}"

        val newPlayerTeam = if (isPlayerAttacking) {
            state.playerTeam.toMutableList().also { it[state.currentPlayerIndex] = attacker }
        } else {
            state.playerTeam.toMutableList().also {
                it[state.currentPlayerIndex] = updatedDefender
            }
        }

        val newOpponentTeam = if (!isPlayerAttacking) {
            state.opponentTeam.toMutableList().also { it[state.currentOpponentIndex] = attacker }
        } else {
            state.opponentTeam.toMutableList().also {
                it[state.currentOpponentIndex] = updatedDefender
            }
        }

        if (updatedDefender.isFainted) {
            return handleFaintedPokemon(
                state, newPlayerTeam, newOpponentTeam,
                updatedDefender, isPlayerAttacking, logMessage
            )
        }

        // Следующий ход
        return state.copy(
            playerTeam = newPlayerTeam,
            opponentTeam = newOpponentTeam,
            battleLog = state.battleLog + logMessage,
            isPlayerTurn = !isPlayerAttacking
        )
    }

    private fun getAttackerAndDefender(state: BattleState): Triple<BattlePokemon, BattlePokemon, Boolean> {
        return if (state.isPlayerTurn) {
            Triple(
                state.playerTeam[state.currentPlayerIndex],
                state.opponentTeam[state.currentOpponentIndex],
                true
            )
        } else {
            Triple(
                state.opponentTeam[state.currentOpponentIndex],
                state.playerTeam[state.currentPlayerIndex],
                false
            )
        }
    }

    private fun handleFaintedPokemon(
        state: BattleState,
        playerTeam: List<BattlePokemon>,
        opponentTeam: List<BattlePokemon>,
        fainted: BattlePokemon,
        wasPlayerAttacking: Boolean,
        logMessage: String
    ): BattleState {
        val faintedName = fainted.pokemon.name
        val faintedLog = "$faintedName fainted!"

        val allOpponentsFainted = opponentTeam.all { it.isFainted }
        val allPlayersFainted = playerTeam.all { it.isFainted }

        if (allOpponentsFainted || allPlayersFainted) {
            return state.copy(
                playerTeam = playerTeam,
                opponentTeam = opponentTeam,
                battleLog = state.battleLog + logMessage + faintedLog + "Battle over!",
                isBattleOver = true,
                playerWon = allOpponentsFainted
            )
        }

        val nextPlayerIndex = if (!wasPlayerAttacking) {
            (state.currentPlayerIndex + 1..2).firstOrNull { i -> !playerTeam[i].isFainted }
                ?: state.currentPlayerIndex
        } else {
            state.currentPlayerIndex
        }

        val nextOpponentIndex = if (wasPlayerAttacking) {
            (state.currentOpponentIndex + 1..2).firstOrNull { i -> !opponentTeam[i].isFainted }
                ?: state.currentOpponentIndex
        } else {
            state.currentOpponentIndex
        }

        val nextPlayerPokemon = playerTeam[nextPlayerIndex]
        val nextOpponentPokemon = opponentTeam[nextOpponentIndex]
        val nextIsPlayerTurn = nextPlayerPokemon.pokemon.speed >= nextOpponentPokemon.pokemon.speed

        return state.copy(
            playerTeam = playerTeam,
            opponentTeam = opponentTeam,
            battleLog = state.battleLog + logMessage + faintedLog,
            isPlayerTurn = nextIsPlayerTurn,
            currentPlayerIndex = nextPlayerIndex,
            currentOpponentIndex = nextOpponentIndex
        )
    }
}