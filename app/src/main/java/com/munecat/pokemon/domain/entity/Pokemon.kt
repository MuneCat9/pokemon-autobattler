package com.munecat.pokemon.domain.entity

data class Pokemon(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val types: List<String>,
    val hp: Int,
    val attack: Int,
    val defense: Int,
    val speed: Int,
    val isInTeam: Boolean = false
)