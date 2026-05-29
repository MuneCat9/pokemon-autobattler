package com.munecat.pokemon.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.munecat.pokemon.data.remote.dto.PokemonDetailResponse

@Entity(tableName = "pokemon")
data class PokemonDbModel(
    @PrimaryKey
    val id: Int,
    val name: String,
    val imageUrl: String,
    val types: String,
    val hp: Int,
    val attack: Int,
    val defense: Int,
    val speed: Int,
    val isInTeam: Boolean = false
)
