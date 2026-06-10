package com.munecat.pokemon.domain.model.battle

data class BattleState(
    val playerTeam: List<BattlePokemon>,
    val opponentTeam: List<BattlePokemon>,
    val currentPlayerIndex: Int = 0,
    val currentOpponentIndex: Int = 0,
    val battleLog: List<String> = emptyList(),
    val isPlayerTurn: Boolean = true,
    val isBattleOver: Boolean = false,
    val playerWon: Boolean? = null
)
