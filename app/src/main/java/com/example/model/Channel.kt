package com.example.model

data class Channel(
    val id: String,
    val name: String,
    val streamUrl: String,
    val type: String, // e.g., "beIN Sports", "Al Kass", "KSA", "Arabic", "Quran"
    val resolution: String = "1080p",
    val logoUrl: String = ""
)

data class ChannelCategory(
    val id: String,
    val nameArabic: String,
    val nameEnglish: String,
    val isMultiQuality: Boolean = false,
    val iconName: String = "tv"
)

data class MatchEvent(
    val id: String,
    val teamHome: String,
    val teamAway: String,
    val scoreHome: String? = null,
    val scoreAway: String? = null,
    val logoHomeUrl: String = "",
    val logoAwayUrl: String = "",
    val tournament: String, // e.g., "دوري أبطال أوروبا"
    val time: String, // e.g., "21:00"
    val channelName: String,
    val isLive: Boolean = false,
    val streamUrl: String = ""
)

object ChannelData {
    val categories = listOf(
        ChannelCategory("bein_1080", "beIN SPORTS (1080P)", "beIN Sports 1080p", isMultiQuality = true),
        ChannelCategory("bein_720", "beIN SPORTS (720P)", "beIN Sports 720p", isMultiQuality = true),
        ChannelCategory("bein_360", "beIN SPORTS (360P)", "beIN Sports 360p", isMultiQuality = true),
        ChannelCategory("bein_244", "beIN SPORTS (244P)", "beIN Sports 244p", isMultiQuality = true),
        ChannelCategory("alkass_ksa", "KSA & AL KASS SPORTS", "KSA & AL KASS SPORTS"),
        ChannelCategory("arabic_news", "ARABIC CHANNELS", "Arabic Channels"),
        ChannelCategory("mbc_entertainment", "MBC CHANNELS", "MBC Entertainment"),
        ChannelCategory("quran_sunnah", "QURAN & SUNNAH", "Islamic Channels")
    )

    val streams = listOf(
        // QURAN & SUNNAH
        Channel("quran_1", "SAUDI QURAN FHD", "http://ottpro.iptvpro2.com:80/Morad56/0661102069/219429", "Quran", "1080p"),
        Channel("sunnah_1", "SAUDIA SUNNAH FHD", "http://ottpro.iptvpro2.com:80/Morad56/0661102069/219428", "Quran", "1080p"),

        // beIN SPORTS
        Channel("bein_news", "beIN SPORTS NEWS FHD", "http://ottpro.iptvpro2.com:80/Morad56/0661102069/24979", "beIN Sports", "1080p"),
        Channel("bein_global", "beIN SPORTS GLOBAL FHD", "http://ottpro.iptvpro2.com:80/Morad56/0661102069/24980", "beIN Sports", "1080p"),
        Channel("bein_4k", "beIN SPORTS 4K UHD", "http://ottpro.iptvpro2.com:80/Morad56/0661102069/164973", "beIN Sports", "2160p"),
        Channel("bein_1", "beIN SPORTS 1 FHD", "http://ottpro.iptvpro2.com:80/Morad56/0661102069/13608", "beIN Sports", "1080p"),
        Channel("bein_2", "beIN SPORTS 2 FHD", "http://ottpro.iptvpro2.com:80/Morad56/0661102069/13607", "beIN Sports", "1080p"),
        Channel("bein_3", "beIN SPORTS 3 FHD", "http://ottpro.iptvpro2.com:80/Morad56/0661102069/13606", "beIN Sports", "1080p"),
        Channel("bein_4", "beIN SPORTS 4 FHD", "http://ottpro.iptvpro2.com:80/Morad56/0661102069/13605", "beIN Sports", "1080p"),
        Channel("bein_5", "beIN SPORTS 5 FHD", "http://ottpro.iptvpro2.com:80/Morad56/0661102069/13604", "beIN Sports", "1080p"),
        Channel("bein_xtra_1", "beIN SPORTS XTRA 1 FHD", "http://ottpro.iptvpro2.com:80/Morad56/0661102069/129996", "beIN Sports", "1080p"),
        Channel("bein_xtra_2", "beIN SPORTS XTRA 2 FHD", "http://ottpro.iptvpro2.com:80/Morad56/0661102069/423814", "beIN Sports", "1080p"),
        Channel("bein_eng_1", "beIN SPORTS ENGLISH 1 HD", "http://ottpro.iptvpro2.com:80/Morad56/0661102069/141018", "beIN Sports", "720p"),

        // AL KASS & KSA
        Channel("alkass_1", "AL KASS 1 FHD", "http://ottpro.iptvpro2.com:80/Morad56/0661102069/22766", "Al Kass", "1080p"),
        Channel("alkass_2", "AL KASS 2 FHD", "http://ottpro.iptvpro2.com:80/Morad56/0661102069/22765", "Al Kass", "1080p"),
        Channel("ksa_1", "KSA SPORTS 1 FHD", "http://ottpro.iptvpro2.com:80/Morad56/0661102069/212474", "KSA", "1080p"),
        Channel("ksa_2", "KSA SPORTS 2 FHD", "http://ottpro.iptvpro2.com:80/Morad56/0661102069/212472", "KSA", "1080p"),
        Channel("ad_sports_1", "AD SPORTS 1 FHD", "http://ottpro.iptvpro2.com:80/Morad56/0661102069/212461", "Al Kass", "1080p"),
        Channel("dubai_sports_1", "DUBAI SPORTS 1 FHD", "http://ottpro.iptvpro2.com:80/Morad56/0661102069/212455", "Al Kass", "1080p"),
        Channel("ontime_1", "ONTIME SPORT 1 HD", "http://ottpro.iptvpro2.com:80/Morad56/0661102069/212480", "Al Kass", "720p"),

        // ARABIC NEWS
        Channel("jazeera", "AL JAZEERA FHD", "http://ottpro.iptvpro2.com:80/Morad56/0661102069/23064", "Arabic", "1080p"),
        Channel("arabiya", "ALARABIA FHD", "http://ottpro.iptvpro2.com:80/Morad56/0661102069/23062", "Arabic", "1080p"),
        Channel("skynews", "SKY NEWS ARABIA", "http://ottpro.iptvpro2.com:80/Morad56/0661102069/23060", "Arabic", "720p"),
        Channel("france24", "FRANCE 24 FHD", "http://ottpro.iptvpro2.com:80/Morad56/0661102069/64262", "Arabic", "1080p"),
        Channel("trt", "TRT ARABIC", "http://ottpro.iptvpro2.com:80/Morad56/0661102069/23051", "Arabic", "720p"),
        Channel("mayadeen", "MAYADEEN TV", "http://ottpro.iptvpro2.com:80/Morad56/0661102069/283479", "Arabic", "720p"),
        Channel("hadath", "AL HADATH", "http://ottpro.iptvpro2.com:80/Morad56/0661102069/23061", "Arabic", "720p"),
        Channel("rt_arabic", "RT ARABIC", "http://ottpro.iptvpro2.com:80/Morad56/0661102069/23058", "Arabic", "720p"),

        // MBC ENTERTAINMENT (Simulated standard streams with gorgeous placeholders matching Yacine UI)
        Channel("mbc_1", "MBC 1 HD", "", "MBC", "1080p"),
        Channel("mbc_2", "MBC 2 HD", "", "MBC", "1080p"),
        Channel("mbc_action", "MBC ACTION", "", "MBC", "720p"),
        Channel("mbc_drama", "MBC DRAMA", "", "MBC", "1080p")
    )

    val matches = listOf(
        MatchEvent(
            id = "m1",
            teamHome = "ريال مدريد",
            teamAway = "برشلونة",
            scoreHome = "2",
            scoreAway = "1",
            tournament = "الدوري الإسباني - الكلاسيكو",
            time = "الكلاسيكو - انتهت",
            channelName = "beIN SPORTS 1 FHD",
            isLive = false,
            streamUrl = "http://ottpro.iptvpro2.com:80/Morad56/0661102069/13608"
        ),
        MatchEvent(
            id = "m2",
            teamHome = "الوداد البيضاوي",
            teamAway = "الرجاء البيضاوي",
            scoreHome = "1",
            scoreAway = "1",
            tournament = "الدوري المغربي الممتاز - ديربي الدار البيضاء",
            time = "الشوط الثاني - 75'",
            channelName = "beIN SPORTS 3 FHD",
            isLive = true,
            streamUrl = "http://ottpro.iptvpro2.com:80/Morad56/0661102069/13606"
        ),
        MatchEvent(
            id = "m3",
            teamHome = "الأهلي المصري",
            teamAway = "الزمالك",
            scoreHome = "0",
            scoreAway = "0",
            tournament = "الدوري المصري الممتاز - ديربي القاهرة",
            time = "تبدأ اليوم 21:00",
            channelName = "ONTIME SPORT 1 HD",
            isLive = false,
            streamUrl = "http://ottpro.iptvpro2.com:80/Morad56/0661102069/212480"
        ),
        MatchEvent(
            id = "m4",
            teamHome = "الهلال السعودي",
            teamAway = "النصر",
            scoreHome = "3",
            scoreAway = "2",
            tournament = "دوري روشن السعودي للمحترفين",
            time = "الشوط الأول - 32'",
            channelName = "KSA SPORTS 1 FHD",
            isLive = true,
            streamUrl = "http://ottpro.iptvpro2.com:80/Morad56/0661102069/212474"
        ),
        MatchEvent(
            id = "m5",
            teamHome = "مانشستر سيتي",
            teamAway = "ليفربول",
            scoreHome = "2",
            scoreAway = "2",
            tournament = "الدوري الإنجليزي الممتاز",
            time = "تبدأ اليوم 18:30",
            channelName = "beIN SPORTS 2 FHD",
            isLive = false,
            streamUrl = "http://ottpro.iptvpro2.com:80/Morad56/0661102069/13607"
        )
    )
}
