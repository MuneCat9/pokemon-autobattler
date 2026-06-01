package com.munecat.pokemon.presentation.screen.pokelist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val getAllPokemonUseCase: GetAllPokemonUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(PokelistState())
    val state = _state.asStateFlow()

    private val teamOrder = mutableListOf<Int>()

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
                    if (teamOrder.isEmpty() && team.isNotEmpty()) {
                        teamOrder.addAll(team.map { it.id })
                    }
                    val sortedTeam = teamOrder.mapNotNull { orderId ->
                        team.find { it.id == orderId }
                    }
                    currentState.copy(team = sortedTeam)
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
                if (pokemon.id !in teamOrder) {
                    teamOrder.add(pokemon.id)
                }
            } catch (e: TeamFullException) {
                _state.update { it.copy(error = "Team is full! (max 3)") }
            } catch (e: PokemonAlreadyInTeamException) {
                _state.update { it.copy(error = "Already in team!") }
            }
        }
    }

    private fun removeFromTeam(pokemonId: Int) {
        viewModelScope.launch {
            manageTeamUseCase.removeFromTeam(pokemonId)
            teamOrder.remove(pokemonId)
        }
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
    val error: String? = null
)