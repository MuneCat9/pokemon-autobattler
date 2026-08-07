package com.munecat.pokemon.di

import com.munecat.pokemon.domain.usecase.GetAllPokemonUseCase
import com.munecat.pokemon.domain.usecase.ManageTeamUseCase
import com.munecat.pokemon.domain.usecase.battle.CreateBattleUseCase
import com.munecat.pokemon.domain.usecase.battle.ExecuteTurnUseCase
import org.koin.dsl.module

val domainModule = module {
    factory { GetAllPokemonUseCase(get()) }
    factory { ManageTeamUseCase(get()) }
    factory { CreateBattleUseCase(get()) }
    factory { ExecuteTurnUseCase() }
}
