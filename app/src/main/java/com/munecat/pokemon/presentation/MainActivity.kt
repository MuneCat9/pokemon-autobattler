package com.munecat.pokemon.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.munecat.pokemon.presentation.screen.pokelist.PokeListScreen
import com.munecat.pokemon.presentation.ui.theme.PokemonTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PokemonTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PokeListScreen(
                        onBackClick = { finish() }
                    )
                }
            }
        }
    }
}
