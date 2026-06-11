package com.munecat.pokemon.presentation.screen.battle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.munecat.pokemon.data.local.TeamPreferences
import com.munecat.pokemon.domain.model.Pokemon
import com.munecat.pokemon.domain.model.battle.BattleState
import com.munecat.pokemon.domain.repository.PokemonRepository
import com.munecat.pokemon.domain.usecase.battle.CreateBattleUseCase
import com.munecat.pokemon.domain.usecase.battle.ExecuteTurnUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BattleViewModel @Inject constructor(
    private val createBattleUseCase: CreateBattleUseCase,
    private val executeTurnUseCase: ExecuteTurnUseCase,
    private val repository: PokemonRepository,
    private val teamPreferences: TeamPreferences
) : ViewModel() {

    private val _state = MutableStateFlow(BattleUiState())
    val state = _state.asStateFlow()

    init {
        loadAllPokemon()
        loadTeamAndGenerateOpponent()
    }

    fun loadAllPokemon() {
        viewModelScope.launch {
            val allPokemon = repository.getAllPokemon().first()
            _state.update {
                it.copy(allPokemon = allPokemon)
            }
        }
    }

    private fun loadTeamAndGenerateOpponent() {
        viewModelScope.launch {
            val playerTeam = repository.getTeam().first()
            val allPokemon = repository.getAllPokemon().first()

            // Получаем порядок из DataStore
            val slots = teamPreferences.getSlots()
            val orderedTeam = slots.mapNotNull { slotId ->
                if (slotId == TeamPreferences.EMPTY_SLOT) null
                else playerTeam.find { it.id == slotId }
            }
            // Добавляем покемонов, которых нет в слотах (на всякий случай)
            val completeTeam = orderedTeam + playerTeam.filter { it.id !in slots }

            val opponentTeam = allPokemon.shuffled().take(3)

            _state.update {
                it.copy(
                    teamOrder = completeTeam.map { pokemon -> pokemon.id },
                    opponentOrder = opponentTeam.map { pokemon -> pokemon.id }
                )
            }
        }
    }

    fun swapTeamSlots(index1: Int, index2: Int) {
        _state.update { currentState ->
            val newOrder = currentState.teamOrder.toMutableList()
            if (index1 < newOrder.size && index2 < newOrder.size) {
                val temp = newOrder[index1]
                newOrder[index1] = newOrder[index2]
                newOrder[index2] = temp
            }
            currentState.copy(teamOrder = newOrder)
        }
    }

    fun confirmSelection() {
        viewModelScope.launch {
            val allPokemon = repository.getAllPokemon().first()
            val playerTeam = _state.value.teamOrder.mapNotNull { id ->
                allPokemon.find { it.id == id }
            }

            val battleState = createBattleUseCase(
                playerTeam = playerTeam,
                opponentIds = _state.value.opponentOrder
            )

            _state.update {
                it.copy(
                    battleState = battleState,
                    isTeamSelectionVisible = false,
                    isBattleInProgress = true
                )
            }
            runAutoBattle()
        }
    }

    private fun runAutoBattle() {
        viewModelScope.launch {
            while (!_state.value.battleState?.isBattleOver!!) {
                delay(1500)
                val currentState = _state.value.battleState ?: break
                val newState = executeTurnUseCase(currentState)
                _state.update { it.copy(battleState = newState) }
            }
        }
    }

    fun confirmResult() {

    }
}

data class BattleUiState(
    val battleState: BattleState? = null,
    val isTeamSelectionVisible: Boolean = true,
    val isBattleInProgress: Boolean = false,
    val teamOrder: List<Int> = emptyList(),
    val opponentOrder: List<Int> = emptyList(),
    val allPokemon: List<Pokemon> = emptyList()
)