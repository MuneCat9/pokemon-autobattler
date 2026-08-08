package com.munecat.pokemon

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.munecat.pokemon.presentation.screen.pokelist.PokeListScreen

@Composable
fun App() {
    MaterialTheme {
        PokeListScreen(
            onBackClick = {

            }
        )
    }
}
