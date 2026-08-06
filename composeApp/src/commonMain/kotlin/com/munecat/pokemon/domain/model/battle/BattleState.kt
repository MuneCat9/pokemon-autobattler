package com.munecat.pokemon.domain.model.battle

data class BattleState(
    val playerTeam: List<BattlePokemon>,
    val opponentTeam: List<BattlePokemon>,
    val currentPlayerIndex: Int = 0,
    val currentOpponentIndex: Int = 0,
    val battleLog: List<BattleLogEntry> = emptyList(),
    val isPlayerTurn: Boolean = true,
    val isBattleOver: Boolean = false,
    val playerWon: Boolean? = null
)

data class BattleLogEntry(
    val message: String,
    val type: LogType = LogType.NEUTRAL
)

enum class LogType {
    NEUTRAL,
    HIGH_ROLL,
    LOW_ROLL,
    SUPER_EFFECTIVE,
    NOT_EFFECTIVE,
    FAINTED,
    DODGED
}