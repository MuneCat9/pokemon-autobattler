package com.munecat.pokemon.data.mapper

import com.munecat.pokemon.data.local.PokemonDbModel
import com.munecat.pokemon.data.remote.dto.PokemonDetailResponse
import com.munecat.pokemon.domain.model.Pokemon
import kotlinx.serialization.json.Json

fun PokemonDetailResponse.toDbModel(): PokemonDbModel {
    val getStat: (String) -> Int = { statName ->
        stats.first { it.stat.name == statName }.baseStat
    }
    val fallbackPng = sprites.frontDefault
    val svgUrl = sprites.other.dreamWorld.frontDefault
    val imageUrl = svgUrl ?: fallbackPng ?: ""

    return PokemonDbModel(
        id = id,
        name = name.replaceFirstChar { it.uppercase() },
        imageUrl = imageUrl,
        types = Json.encodeToString(types.map { it.type.name }),
        hp = getStat("hp"),
        attack = getStat("attack"),
        defense = getStat("defense"),
        speed = getStat("speed"),
        isInTeam = false
    )
}

fun PokemonDbModel.toModel(): Pokemon {
    val typesList: List<String> = try {
        Json.decodeFromString(types)
    } catch (e: Exception) {
        emptyList()
    }

    return Pokemon(
        id = id,
        name = name,
        imageUrl = imageUrl,
        types = typesList,
        hp = hp,
        attack = attack,
        defense = defense,
        speed = speed,
        isInTeam = isInTeam
    )
}