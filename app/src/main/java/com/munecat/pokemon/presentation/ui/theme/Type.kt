package com.munecat.pokemon.presentation.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.munecat.pokemon.R


val PokemonSolid = FontFamily(
    Font(R.font.pokemon_solid, FontWeight.Normal)
)
val PokemonHollow = FontFamily(
    Font(R.font.pokemon_hollow, FontWeight.Normal)
)
val Ketchum = FontFamily(
    Font(R.font.ketchum, FontWeight.Normal)
)

val PokemonTypography = Typography(
    headlineLarge = TextStyle(
        fontFamily = PokemonSolid,
        fontSize = 28.sp,
        color = PokemonColors.TextPrimary
    ),
    headlineMedium = TextStyle(
        fontFamily = PokemonSolid,
        fontSize = 24.sp,
        color = PokemonColors.TextPrimary
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontSize = 16.sp,
        color = PokemonColors.TextPrimary
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontSize = 14.sp,
        color = PokemonColors.TextPrimary
    ),
    labelLarge = TextStyle(
        fontFamily = PokemonSolid,
        fontSize = 18.sp,
        color = PokemonColors.TextOnPrimary
    )
)