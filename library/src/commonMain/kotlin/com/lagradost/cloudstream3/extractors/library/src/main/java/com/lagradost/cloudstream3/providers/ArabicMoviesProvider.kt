package com.lagradost.cloudstream3.providers

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.*
import com.fasterxml.jackson.annotation.JsonProperty

class ArabicMoviesProvider : MainAPI() {
    override var name = "أفلام عربية"
    override val lang = "ar"
    override var mainUrl = "https://api.themoviedb.org"
    override val supportedTypes = setOf(TvType.Movie)

    private val apiKey = "9ba4e29354937364c2202857afcd7f94"
    private val imageUrl = "https://image.tmdb.org/t/p/w500"

    data class TmdbResponse(
        @JsonProperty("results") val results: List<TmdbMovie>
    )

    data class TmdbMovie(
        @JsonProperty("id") val id: Int,
        @JsonProperty("title") val title: String?,
        @JsonProperty("overview") val overview: String?,
        @JsonProperty("poster_path") val posterPath: String?,
        @JsonProperty("release_date") val releaseDate: String?,
        @JsonProperty("vote_average") val voteAverage: Double
    )

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse {
        val url = "$mainUrl/3/trending/movie/week?api_key=$apiKey&language=ar&page=$page"
        val response = app.get(url).parsedSafe<TmdbResponse>()
        val movies = response?.results?.mapNotNull { movie ->
            val title = movie.title ?: return@mapNotNull null
            newMovieSearchResponse(
                name = title,
                url = movie.id.toString(),
                type = TvType.Movie
            ) {
                posterUrl = movie.posterPath?.let { "$imageUrl$it" }
            }
        } ?: emptyList()
        
        return newHomePageResponse(
            HomePageList("الرائج الآن", movies),
            hasNext = true
        )
    }

    override suspend fun search(query: String): List<SearchResponse> {
        val url = "$mainUrl/3/search/movie?api_key=$apiKey&language=ar&query=$query"
        val response = app.get(url).parsedSafe<TmdbResponse>()
        return response?.results?.mapNotNull { movie ->
            val title = movie.title ?: return@mapNotNull null
            newMovieSearchResponse(
                name = title,
                url = movie.id.toString(),
                type = TvType.Movie
            ) {
                posterUrl = movie.posterPath?.let { "$imageUrl$it" }
            }
        } ?: emptyList()
    }

    override suspend fun load(url: String): LoadResponse {
        val movieId = url
        val url2 = "$mainUrl/3/movie/$movieId?api_key=$apiKey&language=ar"
        val movie = app.get(url2).parsedSafe<TmdbMovie>() 
            ?: throw ErrorLoadingException("تعذر جلب الفيلم")
        
        val title = movie.title ?: "فيلم"
        return newMovieLoadResponse(
            name = title,
            url = url,
            dataUrl = movieId,
            type = TvType.Movie
        ) {
            posterUrl = movie.posterPath?.let { "$imageUrl${movie.posterPath}" }
            plot = movie.overview
            year = movie.releaseDate?.take(4)?.toIntOrNull()
            rating = (movie.voteAverage * 1000).toInt()
        }
    }

    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ): Boolean {
        val tmdbId = data
        
        callback(newExtractorLink(
            source = "VidSrc",
            name = "VidSrc - عربي",
            url = "https://vidsrc.sbs/embed/movie/$tmdbId",
            quality = Qualities.P1080.value
        ))
        
        callback(newExtractorLink(
            source = "2Embed",
            name = "2Embed - عربي",
            url = "https://www.2embed.cc/embed/$tmdbId",
            quality = Qualities.P1080.value
        ))
        
        callback(newExtractorLink(
            source = "SuperEmbed",
            name = "SuperEmbed - عربي",
            url = "https://multiembed.link/?video_id=$tmdbId&tmdb=1",
            quality = Qualities.P1080.value
        ))
        
        return true
    }
}
