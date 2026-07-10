package com.munecat.pokemon.presentation.screen.battle

import com.munecat.pokemon.data.local.TeamPreferences
import com.munecat.pokemon.domain.model.Pokemon
import com.munecat.pokemon.domain.model.battle.BattlePokemon
import com.munecat.pokemon.domain.model.battle.BattleState
import com.munecat.pokemon.domain.repository.PokemonRepository
import com.munecat.pokemon.domain.usecase.battle.CreateBattleUseCase
import com.munecat.pokemon.domain.usecase.battle.ExecuteTurnUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BattleViewModelTest {

    private val createBattleUseCase: CreateBattleUseCase = mockk()
    private val executeTurnUseCase: ExecuteTurnUseCase = mockk()
    private val repository: PokemonRepository = mockk()
    private val teamPreferences: TeamPreferences = mockk()

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        every { repository.getAllPokemon() } returns flowOf(emptyList())
        every { repository.getTeam() } returns flowOf(emptyList())
        coEvery { teamPreferences.getSlots() } returns listOf(-1, -1, -1)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadAllPokemon should update state with pokemon from repository`() = runTest {

        val mockPokemon = listOf(
            Pokemon(
                1,
                "Bulbasaur",
                "",
                "",
                "",
                "",
                listOf("grass"),
                45,
                49,
                49,
                45
            )
        )
        every { repository.getAllPokemon() } returns flowOf(mockPokemon)

        val viewModel = BattleViewModel(
            createBattleUseCase,
            executeTurnUseCase,
            repository,
            teamPreferences
        )
        advanceUntilIdle()

        assertEquals(mockPokemon, viewModel.state.value.allPokemon)
    }
    
    @Test
    fun `confirmSelection should call CreateBattleUseCase and update state`() = runTest {
        val mockPokemon = Pokemon(
            1,
            "Bulbasaur",
            "",
            "",
            "",
            "",
            listOf("grass"),
            45,
            49,
            49,
            45
        )
        val mockTeam = listOf(mockPokemon)
        val mockBattleState = BattleState(
            playerTeam = listOf(BattlePokemon.fromPokemon(mockPokemon)),
            opponentTeam = listOf(BattlePokemon.fromPokemon(mockPokemon)),
            isPlayerTurn = true,
            isBattleOver = true
        )
        
        every { repository.getAllPokemon() } returns flowOf(listOf(mockPokemon))
        every { repository.getTeam() } returns flowOf(mockTeam)
        coEvery { createBattleUseCase(any(), any()) } returns mockBattleState

        val viewModel = BattleViewModel(createBattleUseCase, executeTurnUseCase, repository, teamPreferences)
        advanceUntilIdle()
        
        viewModel.confirmSelection()
        advanceUntilIdle()

        assertEquals(mockBattleState, viewModel.state.value.battleState)
        assertEquals(true, viewModel.state.value.isBattleInProgress)
        assertEquals(false, viewModel.state.value.isTeamSelectionVisible)
    }
}
