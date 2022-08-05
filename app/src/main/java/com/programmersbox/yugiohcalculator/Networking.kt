package com.programmersbox.yugiohcalculator

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

object Networking {
    private const val baseUrl = "https://db.ygoprodeck.com/api/v7/cardinfo.php"

    private val client by lazy {
        HttpClient {
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        prettyPrint = true
                        isLenient = true
                    }
                )
            }
            defaultRequest {
                accept(ContentType.Application.Json)
            }
        }
    }

    suspend fun loadCards(networkLoadingState: (NetworkLoadingState) -> Unit): List<CardInfo> {
        return runCatching { client.get(baseUrl).body<YugiohCards>().also { println(it) } }
            .fold(
                onSuccess = {
                    networkLoadingState(NetworkLoadingState.Success)
                    it.data.orEmpty()
                },
                onFailure = {
                    networkLoadingState(NetworkLoadingState.Failure)
                    emptyList()
                }
            )
    }
}

enum class NetworkLoadingState { Loading, Success, Failure }

@Serializable
data class YugiohCards(val data: List<CardInfo>? = emptyList())

@Serializable
data class CardImages(
    val id: String?,
    val image_url: String?,
    val image_url_small: String?
)

@Serializable
data class CardInfo(
    val id: String?,
    val name: String?,
    val type: String?,
    val desc: String?,
    val atk: Int? = 0,
    val def: Int? = 0,
    val level: Int? = 0,
    val race: String?,
    val attribute: String? = "",
    val card_images: List<CardImages>?,
)