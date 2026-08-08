package com.munecat.pokemon.di

import com.munecat.pokemon.data.local.TeamPreferences
import com.munecat.pokemon.data.local.createDataStore
import com.munecat.pokemon.data.local.createDatabase
import com.munecat.pokemon.data.local.getDatabaseBuilder
import com.munecat.pokemon.data.remote.PokemonApi
import com.munecat.pokemon.data.repository.PokemonRepositoryImpl
import com.munecat.pokemon.domain.repository.PokemonRepository
import org.koin.dsl.module

val dataModule = module {
    single { PokemonApi.createHttpClient() }
    single { PokemonApi(get()) }

    single { createDataStore() }
    single { TeamPreferences(get()) }

    single { createDatabase(getDatabaseBuilder()) }
    single { get<com.munecat.pokemon.data.local.PokemonDatabase>().pokemonDao() }
    single<PokemonRepository> { PokemonRepositoryImpl(get(), get()) }
}
