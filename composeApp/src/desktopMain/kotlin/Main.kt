package com.munecat.pokemon

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() {
    // Пробуем OpenGL, если SOFTWARE не помог. 
    // Если и это не поможет, попробуй удалить папку C:\Users\kotme_\.skiko
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
