package com.munecat.pokemon

import android.app.Application
import com.munecat.pokemon.di.initKoin
import org.koin.android.ext.koin.androidContext

class PokemonApp : Application() {
    override fun onCreate() {
        super.onCreate()
        
        initKoin {
            androidContext(this@PokemonApp)
        }
    }
}
