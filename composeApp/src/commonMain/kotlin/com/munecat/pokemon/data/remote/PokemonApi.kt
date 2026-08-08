package com.munecat.pokemon.data.remote

import com.munecat.pokemon.data.remote.dto.PokemonDetailResponse
import com.munecat.pokemon.data.remote.dto.PokemonListResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class PokemonApi(private val client: HttpClient) {

    suspend fun fetchPokemonList(limit: Int = 251): PokemonListResponse {
        return client.get("pokemon?limit=$limit").body()
    }

    suspend fun fetchPokemonDetail(name: String): PokemonDetailResponse {
        return client.get("pokemon/$name").body()
    }

    companion object {
        private const val BASE_URL = "https://pokeapi.co/api/v2/"
        
        fun createHttpClient(): HttpClient {
            return HttpClient {
                install(ContentNegotiation) {
                    json(Json {
                        ignoreUnknownKeys = true
                        prettyPrint = true
                        isLenient = true
                    })
                }
                defaultRequest {
                    url(BASE_URL)
                }
            }
        }
    }
}
