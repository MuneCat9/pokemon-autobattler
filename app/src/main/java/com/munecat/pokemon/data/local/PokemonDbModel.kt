package com.munecat.pokemon.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pokemon")
data class PokemonDbModel(
    @PrimaryKey
    val id: Int,
    val name: String,
    val imageUrl: String,
    val cardImageUrl: String,
    val battleBackUrl: String,
    val battleFrontUrl: String,
    val types: String,
    val hp: Int,
    val attack: Int,
    val defense: Int,
    val speed: Int,
    val isInTeam: Boolean = false
)
