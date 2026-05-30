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

    init {
        loadPokemonList()
        observeTeam()
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
                    currentState.copy(team = team)
                }
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