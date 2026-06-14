package com.munecat.pokemon.domain.model

import androidx.compose.runtime.Stable

@Stable
data class Pokemon(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val cardImageUrl: String,
    val battleBackUrl: String,
    val battleFrontUrl: String,
    val types: List<String>,
    val hp: Int,
    val attack: Int,
    val defense: Int,
    val speed: Int,
    val isInTeam: Boolean = false
)