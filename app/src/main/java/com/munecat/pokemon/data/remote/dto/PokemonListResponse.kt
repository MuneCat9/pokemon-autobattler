package com.munecat.pokemon.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PokemonListResponse(
    @SerialName("results")
    val results: List<PokemonResult>
)

@Serializable
data class PokemonResult(
    @SerialName("name")
    val name: String,
    @SerialName("url")
    val url: String
)