package com.programmersbox.yugiohcalculator

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() = runBlocking {
        val baseUrl = "https://db.ygoprodeck.com/api/v7/cardinfo.php"

        val client = HttpClient {
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

        val r = client.get(baseUrl).body<Base>()

        println(r)
    }

    @Serializable
    data class Base(val data: List<CardInfo>?)

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

}