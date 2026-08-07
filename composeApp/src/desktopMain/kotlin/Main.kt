package com.munecat.pokemon

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.munecat.pokemon.di.initKoin

fun main() {

    initKoin()

    System.setProperty("skiko.renderApi", "OPENGL")
    
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Pokemon KMP Desktop",
        ) {
            App()
        }
    }
}
