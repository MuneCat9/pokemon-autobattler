package com.munecat.pokemon.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PokemonDetailResponse(
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String,
    @SerialName("sprites")
    val sprites: Sprites,
    @SerialName("stats")
    val stats: List<Stat>,
    @SerialName("types")
    val types: List<TypeSlot>
) {
    @Serializable
    data class Sprites(
        @SerialName("other")
        val other: Other,
        @SerialName("front_default")
        val frontDefault: String? = null,
        @SerialName("versions")
        val versions: Versions? = null
    ) {
        @Serializable
        data class Other(
            @SerialName("dream_world")
            val dreamWorld: DreamWorld,
            @SerialName("showdown")
            val showdown: Showdown? = null
        ) {
            @Serializable
            data class DreamWorld(
                @SerialName("front_default")
                val frontDefault: String?
            )
            @Serializable
            data class Showdown(
                @SerialName("front_default")
                val frontDefault: String?
            )
        }
    }

    @Serializable
    data class Versions(
        @SerialName("generation-v")
        val generationV: GenerationV? = null
    ) {
        @Serializable
        data class GenerationV(
            @SerialName("black-white")
            val blackWhite: BlackWhite? = null
        ) {
            @Serializable
            data class BlackWhite(
                @SerialName("animated")
                val animated: Animated? = null
            ) {
                @Serializable
                data class Animated(
                    @SerialName("front_default")
                    val frontDefault: String?,
                    @SerialName("back_default")
                    val backDefault: String?
                )
            }
        }
    }

    @Serializable
    data class Stat(
        @SerialName("base_stat")
        val baseStat: Int,
        @SerialName("stat")
        val stat: StatInfo
    ) {
        @Serializable
        data class StatInfo(
            @SerialName("name")
            val name: String
        )
    }

    @Serializable
    data class TypeSlot(
        @SerialName("type")
        val type: TypeInfo
    ) {
        @Serializable
        data class TypeInfo(
            @SerialName("name")
            val name: String
        )
    }
}