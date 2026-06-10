package com.munecat.pokemon.presentation.screen.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.munecat.pokemon.data.local.TeamPreferences
import com.munecat.pokemon.domain.model.Pokemon
import com.munecat.pokemon.domain.repository.PokemonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: PokemonRepository,
    private val teamPreferences: TeamPreferences
) : ViewModel() {
    private val _state = MutableStateFlow(MainState())
    val state = _state.asStateFlow()

    init {
        observeTeam()
    }

    private fun observeTeam() {
        viewModelScope.launch {
            repository.getTeam().collect { team ->
                _state.update { currentState ->
                    val slots = teamPreferences.getSlots()
                    val sortedTeam = slots.mapNotNull { slotId ->
                        if (slotId == TeamPreferences.EMPTY_SLOT) null
                        else team.find { it.id == slotId }
                    }
                    val teamFromDb = team.filter { it.id !in slots }
                    if (slots.all { it == TeamPreferences.EMPTY_SLOT } && team.isNotEmpty()) {
                        team.forEachIndexed { index, pokemon ->
                            teamPreferences.saveSlot(index, pokemon.id)
                        }
                        currentState.copy(team = team)
                    } else {
                        currentState.copy(team = sortedTeam + teamFromDb)
                    }
                }
            }
        }
    }

    data class MainState(
        val team: List<Pokemon> = emptyList(),
        val isBattleEnabled: Boolean = false
    ) {
        val isStartEnabled: Boolean
            get() = team.size == 3
    }

}