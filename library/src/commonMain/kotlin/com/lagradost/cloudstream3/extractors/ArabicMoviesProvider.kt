package com.lagradost.cloudstream3

import com.lagradost.cloudstream3.utils.ExtractorLink
import com.lagradost.cloudstream3.utils.Qualities
import com.fasterxml.jackson.annotation.JsonProperty
import com.lagradost.cloudstream3.utils.AppUtils.parseJson
import com.lagradost.cloudstream3.utils.SubtitleFile

class ArabicMoviesProvider : MainAPI() {
    override var name = "أفلام عربية"
    override var mainUrl = "https://api.themoviedb.org"
    override val supportedTypes = setOf(TvType.Movie, TvType.TvSeries)
    override val hasMainView = true
    override val hasDownload = true
    override val hasQuickSearch = false
    override val lang = "ar"

    private val apiKey = "9ba4e29354937364c2202857afcd7f94"
    private val imageUrl = "https://image.tmdb.org/t/p/w500"
    private val originalImageUrl = "https://image.tmdb.org/t/p/original"

    data class TmdbResponse(
        @JsonProperty("results") val results: List<TmdbMovie>,
        @JsonProperty("total_pages") val totalPages: Int
    )

    data class TmdbMovie(
        @JsonProperty("id") val id: Int,
        @JsonProperty("title") val title: String?,
        @JsonProperty("name") val name: String?,
        @JsonProperty("overview") val overview: String?,
        @JsonProperty("poster_path") val posterPath: String?,
        @JsonProperty("backdrop_path") val backdropPath: String?,
        @JsonProperty("release_date") val releaseDate: String?,
        @JsonProperty("first_air_date") val firstAirDate: String?,
        @JsonProperty("vote_average") val voteAverage: Double,
        @JsonProperty("original_language") val originalLanguage: String?
    )

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse {
        val trending = app.get(
            "$mainUrl/3/trending/all/week?api_key=$apiKey&language=ar&page=$page"
        ).parsedSafe<TmdbResponse>()

        val movies = trending?.results?.mapNotNull { it.toSearchResponse() } ?: emptyList()

        return newHomePageResponse(
            HomePageList("الرائج الآن", movies),
            hasNext = true
        )
    }

    private fun TmdbMovie.toSearchResponse(): SearchResponse? {
        val title = this.title ?: this.name ?: return null
        val poster = this.posterPath?.let { "$imageUrl$it" }
        
        return MovieSearchResponse(
            name = title,
            url = "$mainUrl/3/movie/${this.id}?api_key=$apiKey&language=ar",
            apiName = name,
            posterUrl = poster
        )
    }

    override suspend fun search(query: String): List<SearchResponse> {
        val response = app.get(
            "$mainUrl/3/search/multi?api_key=$apiKey&language=ar&query=$query"
        ).parsedSafe<TmdbResponse>()

        return response?.results?.mapNotNull { it.toSearchResponse() } ?: emptyList()
    }

    override suspend fun load(url: String): LoadResponse {
        val data = app.get(url).parsedSafe<TmdbMovie>() 
            ?: throw ErrorLoadingException("لا يمكن جلب بيانات الفيلم")

        val title = data.title ?: data.name ?: "فيلم"
        val poster = data.posterPath?.let { "$originalImageUrl$it" }
        val backdrop = data.backdropPath?.let { "$originalImageUrl$it" }
        val plot = data.overview ?: ""
        val year = (data.releaseDate ?: data.firstAirDate)?.take(4)

        return MovieLoadResponse(
            name = title,
            url = url,
            apiName = name,
            dataUrl = data.id.toString(),
            posterUrl = poster,
            year = year?.toIntOrNull(),
            plot = plot,
            backgroundPosterUrl = backdrop,
            rating = (data.voteAverage * 10000).toInt()
        )
    }

    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ): Boolean {
        // روابط بث مباشرة من VidSrc و 2Embed و SuperEmbed
        val tmdbId = data
        
        callback.invoke(
            ExtractorLink(
                "VidSrc",
                "VidSrc - عربي",
                "https://vidsrc.sbs/embed/movie/$tmdbId",
                "",
                Qualities.P1080.value,
                type = INFER_TYPE
            )
        )
        
        callback.invoke(
            ExtractorLink(
                "2Embed",
                "2Embed - عربي",
                "https://www.2embed.cc/embed/$tmdbId",
                "",
                Qualities.P1080.value,
                type = INFER_TYPE
            )
        )
        
        callback.invoke(
            ExtractorLink(
                "SuperEmbed",
                "SuperEmbed - عربي",
                "https://multiembed.link/?video_id=$tmdbId&tmdb=1",
                "",
                Qualities.P1080.value,
                type = INFER_TYPE
            )
        )

        return true
    }
}
