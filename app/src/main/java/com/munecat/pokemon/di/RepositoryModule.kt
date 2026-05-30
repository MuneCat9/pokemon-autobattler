package com.munecat.pokemon.di

import com.munecat.pokemon.data.repository.PokemonRepositoryImpl
import com.munecat.pokemon.domain.repository.PokemonRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindsPokemonRepository(
        impl: PokemonRepositoryImpl
    ): PokemonRepository
}