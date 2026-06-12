package com.munecat.pokemon.presentation.screen.battle

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.munecat.pokemon.R

private val iconMap = mapOf(
    "normal" to R.drawable.icon_normal,
    "fighting" to R.drawable.icon_fighting,
    "flying" to R.drawable.icon_flying,
    "poison" to R.drawable.icon_poison,
    "ground" to R.drawable.icon_ground,
    "rock" to R.drawable.icon_rock,
    "bug" to R.drawable.icon_bug,
    "ghost" to R.drawable.icon_ghost,
    "steel" to R.drawable.icon_steel,
    "fire" to R.drawable.icon_fire,
    "water" to R.drawable.icon_water,
    "grass" to R.drawable.icon_grass,
    "electric" to R.drawable.icon_electric,
    "psychic" to R.drawable.icon_psychic,
    "ice" to R.drawable.icon_ice,
    "dragon" to R.drawable.icon_dragon,
    "dark" to R.drawable.icon_dark,
    "fairy" to R.drawable.icon_fairy
)

private val smallIconMap = mapOf(
    "normal" to R.drawable.icon_normal_small,
    "fighting" to R.drawable.icon_fighting_small,
    "flying" to R.drawable.icon_flying_small,
    "poison" to R.drawable.icon_poison_small,
    "ground" to R.drawable.icon_ground_small,
    "rock" to R.drawable.icon_rock_small,
    "bug" to R.drawable.icon_bug_small,
    "ghost" to R.drawable.icon_ghost_small,
    "steel" to R.drawable.icon_steel_small,
    "fire" to R.drawable.icon_fire_small,
    "water" to R.drawable.icon_water_small,
    "grass" to R.drawable.icon_grass_small,
    "electric" to R.drawable.icon_electric_small,
    "psychic" to R.drawable.icon_psychic_small,
    "ice" to R.drawable.icon_ice_small,
    "dragon" to R.drawable.icon_dragon_small,
    "dark" to R.drawable.icon_dark_small,
    "fairy" to R.drawable.icon_fairy_small
)

fun getTypeIcon(type: String): Int = iconMap[type.lowercase()] ?: R.drawable.icon_normal
fun getTypeSmallIcon(type: String): Int = smallIconMap[type.lowercase()] ?: R.drawable.icon_normal_small

@Composable
fun TypeBadge(type: String, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(getTypeSmallIcon(type)),
        contentDescription = type,
        modifier = modifier.size(24.dp)
    )
}

@Composable
fun TypeBadgeRow(types: List<String>, modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
        types.forEach { type ->
            TypeBadge(type = type)
        }
    }
}