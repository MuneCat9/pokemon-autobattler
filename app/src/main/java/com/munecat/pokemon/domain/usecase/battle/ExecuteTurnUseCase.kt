package com.munecat.pokemon.domain.usecase.battle

import com.munecat.pokemon.domain.model.battle.BattleAction
import com.munecat.pokemon.domain.model.battle.BattleLogEntry
import com.munecat.pokemon.domain.model.battle.BattlePokemon
import com.munecat.pokemon.domain.model.battle.BattleState
import com.munecat.pokemon.domain.model.battle.LogType
import com.munecat.pokemon.domain.model.battle.PokemonType
import com.munecat.pokemon.domain.model.battle.TypeEffectiveness
import javax.inject.Inject

class ExecuteTurnUseCase @Inject constructor() {

    operator fun invoke(state: BattleState): BattleState {
        if (state.isBattleOver) return state

        val (attacker, defender, isPlayerAttacking) = getAttackerAndDefender(state)

        val baseDamage = (attacker.pokemon.attack * 0.2).toInt()

        val attackerTypes = attacker.pokemon.types.mapNotNull { PokemonType.fromString(it) }
        val defenderTypes = defender.pokemon.types.mapNotNull { PokemonType.fromString(it) }

        val typeMultiplier = attackerTypes.maxOfOrNull { attackerType ->
            TypeEffectiveness.getMultiplier(attackerType, defenderTypes)
        } ?: 1f

        val defenseRatio = defender.pokemon.defense / 200f
        val effectiveDefense = when {
            typeMultiplier > 1f -> defenseRatio * 0.5f
            typeMultiplier < 1f -> defenseRatio * 1.5f
            else -> defenseRatio
        }.coerceIn(0f, 0.85f)

        val damageAfterDefense = (baseDamage * typeMultiplier * (1f - effectiveDefense)).toInt().coerceAtLeast(1)

        val speedRatio = attacker.pokemon.speed / 150f
        val variancePercent = ((1f - speedRatio) * 0.30f + 0.05f)
        val variance = (damageAfterDefense * variancePercent).toInt()
        val randomFactor = if (variance > 0) (-variance..variance).random() else 0
        val finalDamage = (damageAfterDefense + randomFactor).coerceAtLeast(1)

        val dodgeChance = calculateDodgeChance(attacker, defender)
        val isDodged = (1..100).random() <= dodgeChance

        if (isDodged) {
            val dodgeEntry = BattleLogEntry(
                message = "${defender.pokemon.name} dodged attack from ${attacker.pokemon.name}",
                type = LogType.DODGED
            )

            return state.copy(
                playerTeam = state.playerTeam,
                opponentTeam = state.opponentTeam,
                battleLog = state.battleLog + dodgeEntry,
                isPlayerTurn = !isPlayerAttacking
            )
        }

        val effectivenessText = when {
            typeMultiplier > 1f -> "It's super effective! "
            typeMultiplier < 1f && typeMultiplier >= 0.75f -> "It's not very effective... "
            typeMultiplier < 0.75f -> "It's barely effective... "
            else -> ""
        }

        val logType = when {
            typeMultiplier > 1f -> LogType.SUPER_EFFECTIVE
            typeMultiplier < 1f -> LogType.NOT_EFFECTIVE
            randomFactor > damageAfterDefense * 0.15 -> LogType.HIGH_ROLL
            randomFactor < -damageAfterDefense * 0.15 -> LogType.LOW_ROLL
            else -> LogType.NEUTRAL
        }

        val logMessage = "$effectivenessText${attacker.pokemon.name} dealt $finalDamage damage to ${defender.pokemon.name}"

        val logEntry = BattleLogEntry(
            message = logMessage,
            type = logType
        )

        val newHp = (defender.currentHp - finalDamage).coerceAtLeast(0)
        val updatedDefender = defender.copy(
            currentHp = newHp,
            isFainted = newHp <= 0
        )

        val action = BattleAction.Attack(
            attacker = attacker,
            defender = updatedDefender,
            damage = finalDamage
        )

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
                updatedDefender, isPlayerAttacking, logEntry
            )
        }

        return state.copy(
            playerTeam = newPlayerTeam,
            opponentTeam = newOpponentTeam,
            battleLog = state.battleLog + logEntry,
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
        logEntry: BattleLogEntry
    ): BattleState {
        val faintedName = fainted.pokemon.name

        val faintedEntry = BattleLogEntry(
            message = "$faintedName fainted!",
            type = LogType.FAINTED
        )

        val allOpponentsFainted = opponentTeam.all { it.isFainted }
        val allPlayersFainted = playerTeam.all { it.isFainted }

        if (allOpponentsFainted || allPlayersFainted) {
            val battleOverEntry = BattleLogEntry(
                message = "Battle over!",
                type = LogType.NEUTRAL
            )
            return state.copy(
                playerTeam = playerTeam,
                opponentTeam = opponentTeam,
                battleLog = state.battleLog + logEntry + faintedEntry + battleOverEntry,
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
            battleLog = state.battleLog + logEntry + faintedEntry,
            isPlayerTurn = nextIsPlayerTurn,
            currentPlayerIndex = nextPlayerIndex,
            currentOpponentIndex = nextOpponentIndex
        )
    }

    private fun calculateDodgeChance(
        attacker: BattlePokemon,
        defender: BattlePokemon
    ): Int {
        val baseChance = 25f
        val attackerSpeed = attacker.pokemon.speed.toFloat()
        val defenderSpeed = defender.pokemon.speed.toFloat()

        val speedRatio = (defenderSpeed / attackerSpeed).coerceIn(0.5f, 1.5f)
        val finalChance = (baseChance * speedRatio).toInt().coerceIn(5, 50)

        return finalChance
    }
}