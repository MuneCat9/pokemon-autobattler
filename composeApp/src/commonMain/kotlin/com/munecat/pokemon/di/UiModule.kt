package com.munecat.pokemon.di

import com.munecat.pokemon.presentation.screen.pokelist.PokelistViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val uiModule = module {
    viewModel { PokelistViewModel(get(), get(), get()) }
}
