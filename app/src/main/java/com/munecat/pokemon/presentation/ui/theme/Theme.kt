package com.munecat.pokemon.presentation.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.munecat.pokemon.presentation.ui.theme.PokemonColors.DigitalRed
import com.munecat.pokemon.presentation.ui.theme.PokemonColors.PokemonAccent
import com.munecat.pokemon.presentation.ui.theme.PokemonColors.PokemonAccentAlpha30
import com.munecat.pokemon.presentation.ui.theme.PokemonColors.PokemonBackground
import com.munecat.pokemon.presentation.ui.theme.PokemonColors.PokemonPrimary
import com.munecat.pokemon.presentation.ui.theme.PokemonColors.PokemonPrimaryAlpha70
import com.munecat.pokemon.presentation.ui.theme.PokemonColors.PokemonPrimaryDark
import com.munecat.pokemon.presentation.ui.theme.PokemonColors.PokemonPrimaryDarkAlpha30
import com.munecat.pokemon.presentation.ui.theme.PokemonColors.PokemonSecondary
import com.munecat.pokemon.presentation.ui.theme.PokemonColors.PokemonSecondaryDark
import com.munecat.pokemon.presentation.ui.theme.PokemonColors.PokemonSecondaryLight
import com.munecat.pokemon.presentation.ui.theme.PokemonColors.PokemonSurface
import com.munecat.pokemon.presentation.ui.theme.PokemonColors.PokemonSurfaceVariant
import com.munecat.pokemon.presentation.ui.theme.PokemonColors.TextOnAccent
import com.munecat.pokemon.presentation.ui.theme.PokemonColors.TextOnPrimary
import com.munecat.pokemon.presentation.ui.theme.PokemonColors.TextOnSecondary


private val LightColorScheme = lightColorScheme(
    primary = PokemonPrimary.copy(alpha = 0.7f),
    onPrimary = TextOnPrimary,
    onPrimaryFixed = PokemonPrimaryAlpha70,
    onPrimaryFixedVariant = PokemonPrimaryDarkAlpha30,
    primaryContainer = PokemonPrimaryDark.copy(alpha = 0.3f),
    secondary = PokemonSecondary.copy(alpha = 0.5f),
    onSecondary = TextOnSecondary,
    onSecondaryFixed = PokemonSecondaryLight,
    secondaryContainer = PokemonSecondaryDark.copy(alpha = 0.3f),
    tertiary = PokemonAccent.copy(alpha = 0.7f),
    onTertiary = TextOnAccent,
    onTertiaryFixed = PokemonAccentAlpha30,
    background = PokemonBackground,
    surface = PokemonSurface,
    surfaceVariant = PokemonSurfaceVariant,
    error = DigitalRed,
    errorContainer = DigitalRed.copy(alpha = 0.15f),
    onBackground = Color(0xFF212121),
    onSurface = Color(0xFF212121)
)

@Composable
fun PokemonTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = PokemonTypography,
        content = content
    )
}