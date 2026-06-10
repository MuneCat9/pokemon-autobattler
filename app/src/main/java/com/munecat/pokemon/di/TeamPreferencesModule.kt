package com.munecat.pokemon.di

import android.content.Context
import com.munecat.pokemon.data.local.TeamPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PreferencesModule {

    @Provides
    @Singleton
    fun provideTeamPreferences(@ApplicationContext context: Context): TeamPreferences {
        return TeamPreferences(context)
    }
}