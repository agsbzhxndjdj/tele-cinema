package com.lagradost.cloudstream3.extractors

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.*

class VidSrcArabic : ExtractorApi() {
    override val name = "VidSrc Arabic"
    override val mainUrl = "https://vidsrc.sbs"
    override val requiresReferer = false

    override suspend fun getUrl(
        url: String,
        referer: String?,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ) {
        callback.invoke(
            ExtractorLink(
                this.name,
                this.name,
                url,
                "",
                Qualities.P1080.value,
                type = INFER_TYPE
            )
        )
    }
}

class TwoEmbedArabic : ExtractorApi() {
    override val name = "2Embed Arabic"
    override val mainUrl = "https://www.2embed.cc"
    override val requiresReferer = false

    override suspend fun getUrl(
        url: String,
        referer: String?,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ) {
        callback.invoke(
            ExtractorLink(
                this.name,
                this.name,
                url,
                "",
                Qualities.P1080.value,
                type = INFER_TYPE
            )
        )
    }
}

class SuperEmbedArabic : ExtractorApi() {
    override val name = "SuperEmbed Arabic"
    override val mainUrl = "https://multiembed.link"
    override val requiresReferer = false

    override suspend fun getUrl(
        url: String,
        referer: String?,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ) {
        callback.invoke(
            ExtractorLink(
                this.name,
                this.name,
                url,
                "",
                Qualities.P1080.value,
                type = INFER_TYPE
            )
        )
    }
}
