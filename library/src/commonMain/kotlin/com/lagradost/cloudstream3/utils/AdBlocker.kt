package com.lagradost.cloudstream3.utils

object AdBlocker {
    
    // قائمة بمواقع الإعلانات المعروفة
    private val adDomains = listOf(
        "doubleclick.net",
        "googlesyndication.com",
        "adservice.google.com",
        "googleadservices.com",
        "googletagmanager.com",
        "googletagservices.com",
        "popads.net",
        "popcash.net",
        "propellerads.com",
        "adsterra.com",
        "adcash.com",
        "clickadu.com",
        "hilltopads.com",
        "exoclick.com",
        "juicyads.com",
        "trafficjunky.com",
        "traffichaus.com",
        "adskeeper.co.uk",
        "mgid.com",
        "revcontent.com",
        "taboola.com",
        "outbrain.com",
        "zedo.com",
        "adnxs.com",
        "advertising.com",
        "pubmatic.com",
        "rubiconproject.com",
        "openx.net",
        "yieldmanager.com",
        "criteo.com",
        "smartadserver.com",
        "adform.net",
        "admob.com",
        "adcolony.com",
        "unityads.unity3d.com",
        "chartboost.com",
        "inmobi.com",
        "mopub.com"
    )
    
    // قائمة بـ patterns للإعلانات
    private val adPatterns = listOf(
        "/ads/",
        "/ad/",
        "/banner/",
        "/popup/",
        "/popunder/",
        "pop.js",
        "ads.js",
        "ad.js",
        "banner.js",
        "tracking.js",
        "analytics.js",
        "pixel.",
        "beacon.",
        "tracker.",
        "utm_source",
        "utm_medium",
        "utm_campaign"
    )
    
    /**
     * تحقق مما إذا كان الرابط إعلاناً
     */
    fun isAd(url: String): Boolean {
        val lowerUrl = url.lowercase()
        
        // فحص النطاقات
        for (domain in adDomains) {
            if (lowerUrl.contains(domain)) {
                return true
            }
        }
        
        // فحص الأنماط
        for (pattern in adPatterns) {
            if (lowerUrl.contains(pattern)) {
                return true
            }
        }
        
        return false
    }
    
    /**
     * تنظيف URL من متتبعات الإعلانات
     */
    fun cleanUrl(url: String): String {
        var cleaned = url
        
        // إزالة utm parameters
        val utmPattern = Regex("[?&](utm_[^&]+)")
        cleaned = cleaned.replace(utmPattern, "")
        
        // إزالة tracking parameters
        val trackingPattern = Regex("[?&](fbclid|gclid|msclkid|ref|clickid|tracking)=[^&]*")
        cleaned = cleaned.replace(trackingPattern, "")
        
        // إزالة && المكررة
        cleaned = cleaned.replace("&&", "&")
        cleaned = cleaned.replace("?&", "?")
        
        // إزالة ? في النهاية
        if (cleaned.endsWith("?")) {
            cleaned = cleaned.dropLast(1)
        }
        
        return cleaned
    }
}
