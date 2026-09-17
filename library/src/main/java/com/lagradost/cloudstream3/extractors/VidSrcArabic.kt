package com.lagradost.cloudstream3.extractors

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
        callback(newExtractorLink(
            source = this.name,
            name = this.name,
            url = url,
            quality = Qualities.P1080.value
        ))
    }
}
