package com.munecat.pokemon.presentation.screen.pokelist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.munecat.pokemon.data.local.TeamPreferences
import com.munecat.pokemon.domain.model.Pokemon
import com.munecat.pokemon.domain.usecase.GetAllPokemonUseCase
import com.munecat.pokemon.domain.usecase.ManageTeamUseCase
import com.munecat.pokemon.domain.usecase.PokemonAlreadyInTeamException
import com.munecat.pokemon.domain.usecase.TeamFullException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PokelistViewModel @Inject constructor(
    private val manageTeamUseCase: ManageTeamUseCase,
    private val getAllPokemonUseCase: GetAllPokemonUseCase,
    private val teamPreferences: TeamPreferences
) : ViewModel() {
    private val _state = MutableStateFlow(PokelistState())
    val state = _state.asStateFlow()

    init {
        loadPokemonList()
        observeTeam()
        refreshDataIfNeeded()
    }

    private fun loadPokemonList() {
        viewModelScope.launch {
            getAllPokemonUseCase().collect { pokemonList ->
                _state.update { currentState ->
                    currentState.copy(
                        allPokemon = pokemonList,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun observeTeam() {
        viewModelScope.launch {
            manageTeamUseCase.getTeam().collect { team ->
                _state.update { currentState ->
                    // Строим команду по слотам из DataStore
                    val slots = teamPreferences.getSlots()
                    val sortedTeam = slots.mapNotNull { slotId ->
                        if (slotId == TeamPreferences.EMPTY_SLOT) null
                        else team.find { it.id == slotId }
                    }
                    // Добавляем покемонов из БД, которых нет в слотах (первая загрузка)
                    val teamFromDb = team.filter { pokemon ->
                        pokemon.id !in slots
                    }
                    // Если слоты пусты — заполняем их текущей командой
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

    private fun refreshDataIfNeeded() {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true)
            }
            try {
                manageTeamUseCase.refreshDataIfEmpty()
            } catch (e: Exception) {
                _state.update { it.copy(error = "Network error. Pull to retry.") }
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    fun processCommand(command: PokelistCommand) {
        when (command) {
            is PokelistCommand.AddToTeam -> addToTeam(command.pokemon)
            is PokelistCommand.RemoveFromTeam -> removeFromTeam(command.pokemonId)
        }
    }

    private fun addToTeam(pokemon: Pokemon) {
        viewModelScope.launch {
            try {
                manageTeamUseCase.addToTeam(pokemon)
                _state.update { it.copy(error = null) }
                val slots = teamPreferences.getSlots()
                val emptyIndex = slots.indexOf(TeamPreferences.EMPTY_SLOT)
                if (emptyIndex != -1) {
                    teamPreferences.saveSlot(emptyIndex, pokemon.id)
                }
            } catch (e: TeamFullException) {
                _state.update {
                    it.copy(
                        error = "Team is full!",
                        errorTimestamp = System.currentTimeMillis()
                    )
                }
            } catch (e: PokemonAlreadyInTeamException) { }
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    private fun removeFromTeam(pokemonId: Int) {
        viewModelScope.launch {
            manageTeamUseCase.removeFromTeam(pokemonId)
            val slots = teamPreferences.getSlots()
            val index = slots.indexOf(pokemonId)
            if (index != -1) {
                teamPreferences.clearSlot(index)
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _state.update { it.copy(searchQuery = query) }
    }
}

sealed interface PokelistCommand {
    data class AddToTeam(val pokemon: Pokemon) : PokelistCommand
    data class RemoveFromTeam(val pokemonId: Int) : PokelistCommand
}

data class PokelistState(
    val allPokemon: List<Pokemon> = emptyList(),
    val team: List<Pokemon> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val errorTimestamp: Long = 0L,
    val searchQuery: String = ""
)