package com.munecat.pokemon.presentation.screen.battle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val repository: PokemonRepository
) : ViewModel() {

    private val _state = MutableStateFlow(BattleUiState())
    val state = _state.asStateFlow()

    fun startBattle(playerTeam: List<Pokemon>) {
        viewModelScope.launch {
            val battleState = createBattleUseCase(playerTeam)
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

    fun loadAndStartBattle() {
        viewModelScope.launch {
            val team = repository.getTeam().first()
            startBattle(team)
        }
    }

    fun confirmResult() {

    }
}

data class BattleUiState(
    val battleState: BattleState? = null,
    val isTeamSelectionVisible: Boolean = true,
    val isBattleInProgress: Boolean = false
)