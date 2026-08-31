package com.thiago.streamhub

/**
 * Representa um serviço de streaming.
 * packageName = pacote do app oficial no Android.
 * playStoreId = id usado para montar o link da Play Store.
 * webFallbackUrl = site oficial, caso o usuário não tenha Play Store (ex: TV Box genérica).
 */
data class StreamingService(
    val name: String,
    val packageName: String,
    val playStoreId: String,
    val webFallbackUrl: String
)

object StreamingCatalog {
    val services = listOf(
        StreamingService(
            name = "Netflix",
            packageName = "com.netflix.mediaclient",
            playStoreId = "com.netflix.mediaclient",
            webFallbackUrl = "https://www.netflix.com"
        ),
        StreamingService(
            name = "Prime Video",
            packageName = "com.amazon.avod.thirdpartyclient",
            playStoreId = "com.amazon.avod.thirdpartyclient",
            webFallbackUrl = "https://www.primevideo.com"
        ),
        StreamingService(
            name = "Disney+",
            packageName = "com.disney.disneyplus",
            playStoreId = "com.disney.disneyplus",
            webFallbackUrl = "https://www.disneyplus.com"
        ),
        StreamingService(
            name = "HBO Max",
            packageName = "com.hbo.hbonow",
            playStoreId = "com.hbo.hbonow",
            webFallbackUrl = "https://www.max.com"
        ),
        StreamingService(
            name = "YouTube",
            packageName = "com.google.android.youtube",
            playStoreId = "com.google.android.youtube",
            webFallbackUrl = "https://www.youtube.com"
        ),
        StreamingService(
            name = "Globoplay",
            packageName = "com.globo.globotv",
            playStoreId = "com.globo.globotv",
            webFallbackUrl = "https://globoplay.globo.com"
        ),
        StreamingService(
            name = "Spotify",
            packageName = "com.spotify.music",
            playStoreId = "com.spotify.music",
            webFallbackUrl = "https://www.spotify.com"
        ),
        StreamingService(
            name = "Paramount+",
            packageName = "com.cbs.ca",
            playStoreId = "com.cbs.ca",
            webFallbackUrl = "https://www.paramountplus.com"
        )
    )
}
