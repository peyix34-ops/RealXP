package com.realxp.app.ui

import com.realxp.app.data.Ranks

/**
 * RealXP için alana özel uzmanlık ünvanları.
 *
 * Bu sınıf yalnızca ünvan metnini belirler.
 * XP hesabını, seviye sistemini, kayıtları veya ilerlemeyi değiştirmez.
 */
private data class AreaTitleTier(
    val minLevel: Int,
    val english: String,
    val turkish: String
)

object AreaTitles {

    private fun tier(
        level: Int,
        en: String,
        tr: String
    ) = AreaTitleTier(level, en, tr)

    /**
     * Her alanın ünvanları en düşük seviyeden
     * en yüksek seviyeye doğru sıralanmalıdır.
     */
    private val byArea: Map<String, List<AreaTitleTier>> = mapOf(

        "English" to listOf(
            tier(1, "Word Scout", "Kelime Kaşifi"),
            tier(5, "Phrase Builder", "İfade Ustası"),
            tier(10, "Fluent Speaker", "Akıcı Konuşmacı"),
            tier(20, "Language Virtuoso", "Dil Virtüözü"),
            tier(35, "Shakespeare", "Shakespeare"),
            tier(50, "Voice of the World", "Dünyanın Sesi")
        ),

        "Languages" to listOf(
            tier(1, "Language Explorer", "Dil Kaşifi"),
            tier(5, "Polyglot Scout", "Poliglot Kaşifi"),
            tier(10, "Polyglot Navigator", "Dil Gezgini"),
            tier(20, "Master Polyglot", "Usta Poliglot"),
            tier(35, "Master Translator", "Baş Çevirmen"),
            tier(50, "Babel's Successor", "Babil'in Varisi")
        ),

        "Reading" to listOf(
            tier(1, "Page Explorer", "Sayfa Kaşifi"),
            tier(5, "Bookworm", "Kitap Kurdu"),
            tier(10, "Critical Reader", "Eleştirel Okur"),
            tier(20, "Literary Scholar", "Edebiyat Bilgesi"),
            tier(35, "Borges's Heir", "Borges'in Varisi"),
            tier(50, "Living Library", "Yaşayan Kütüphane")
        ),

        "History" to listOf(
            tier(1, "Time Traveler", "Zaman Yolcusu"),
            tier(5, "Archive Apprentice", "Arşiv Çırağı"),
            tier(10, "Field Historian", "Saha Tarihçisi"),
            tier(20, "Master Chronicler", "Usta Vakanüvis"),
            tier(35, "Herodotus", "Herodotos"),
            tier(50, "Civilization Sage", "Medeniyet Hafızı")
        ),

        "Math" to listOf(
            tier(1, "Number Seeker", "Sayı Kaşifi"),
            tier(5, "Pattern Finder", "Örüntü Avcısı"),
            tier(10, "Equation Crafter", "Denklem Ustası"),
            tier(20, "Math Strategist", "Matematik Ustası"),
            tier(35, "Gauss's Heir", "Gauss'un Varisi"),
            tier(50, "Archimedes Reborn", "Arşimet'in İzinde")
        ),

        "Science" to listOf(
            tier(1, "Curious Observer", "Meraklı Gözlemci"),
            tier(5, "Lab Apprentice", "Laboratuvar Çırağı"),
            tier(10, "Experimentalist", "Deney Ustası"),
            tier(20, "Research Mind", "Araştırmacı Zihin"),
            tier(35, "Marie Curie", "Marie Curie"),
            tier(50, "Discovery Pioneer", "Keşif Öncüsü")
        ),

        "Writing" to listOf(
            tier(1, "Word Gatherer", "Kelime Avcısı"),
            tier(5, "Scene Weaver", "Sahne Dokuyucusu"),
            tier(10, "Storyteller", "Hikâye Anlatıcısı"),
            tier(20, "Literary Craftsman", "Edebiyat Ustası"),
            tier(35, "Tolkien's Heir", "Tolkien'in Varisi"),
            tier(50, "World Architect", "Dünyaların Mimarı")
        ),

        "Coding" to listOf(
            tier(1, "Script Apprentice", "Kod Çırağı"),
            tier(5, "Bug Hunter", "Hata Avcısı"),
            tier(10, "Software Craftsman", "Yazılım Ustası"),
            tier(20, "Systems Architect", "Sistem Mimarı"),
            tier(35, "Elliot Alderson", "Elliot Alderson"),
            tier(50, "Machine Ghost", "Makine Hayaleti")
        ),

        "AI" to listOf(
            tier(1, "Data Scout", "Veri Kaşifi"),
            tier(5, "Model Tuner", "Model Eğitmeni"),
            tier(10, "Neural Builder", "Sinir Ağı Kurucusu"),
            tier(20, "AI Architect", "Zekâ Mimarı"),
            tier(35, "Turing's Heir", "Turing'in Varisi"),
            tier(50, "Singularity Sage", "Tekillik Mimarı")
        ),

        "Design" to listOf(
            tier(1, "Visual Scout", "Görsel Kaşifi"),
            tier(5, "Layout Crafter", "Yerleşim Ustası"),
            tier(10, "UX Designer", "UX Tasarımcısı"),
            tier(20, "Design Lead", "Tasarım Öncüsü"),
            tier(35, "Dieter Rams", "Dieter Rams"),
            tier(50, "Design Icon", "Tasarım İkonu")
        ),

        "Photography" to listOf(
            tier(1, "Frame Hunter", "Kare Avcısı"),
            tier(5, "Light Chaser", "Işık Avcısı"),
            tier(10, "Visual Storyteller", "Görsel Hikâyeci"),
            tier(20, "Composition Master", "Kompozisyon Ustası"),
            tier(35, "Ansel Adams", "Ansel Adams"),
            tier(50, "Eye of the Century", "Yüzyılın Gözü")
        ),

        "Art" to listOf(
            tier(1, "Color Apprentice", "Renk Çırağı"),
            tier(5, "Sketch Maker", "Eskiz Ustası"),
            tier(10, "Art Creator", "Sanat Üreticisi"),
            tier(20, "Master Painter", "Usta Ressam"),
            tier(35, "Leonardo da Vinci", "Leonardo da Vinci"),
            tier(50, "Renaissance Legend", "Rönesans Efsanesi")
        ),

        "Music" to listOf(
            tier(1, "Rhythm Seeker", "Ritim Kaşifi"),
            tier(5, "Melody Maker", "Melodi Ustası"),
            tier(10, "Composer", "Besteci"),
            tier(20, "Virtuoso", "Virtüöz"),
            tier(35, "Mozart's Heir", "Mozart'ın Varisi"),
            tier(50, "Maestro of Eras", "Çağların Maestrosu")
        ),

        "Speaking" to listOf(
            tier(1, "Clear Voice", "Güçlü Ses"),
            tier(5, "Confident Orator", "Özgüvenli Hatip"),
            tier(10, "Persuader", "İkna Ustası"),
            tier(20, "Master Orator", "Usta Hatip"),
            tier(35, "Cicero's Heir", "Cicero'nun Varisi"),
            tier(50, "Voice of Nations", "Ulusların Sesi")
        ),

        "Cooking" to listOf(
            tier(1, "Kitchen Apprentice", "Mutfak Çırağı"),
            tier(5, "Flavor Explorer", "Lezzet Kaşifi"),
            tier(10, "Skilled Chef", "Usta Aşçı"),
            tier(20, "Master Chef", "Baş Aşçı"),
            tier(35, "Escoffier", "Escoffier"),
            tier(50, "Culinary Legend", "Mutfak Efsanesi")
        ),

        "Driving" to listOf(
            tier(1, "Road Learner", "Yol Öğrencisi"),
            tier(5, "Road Reader", "Yol Okuyucusu"),
            tier(10, "Skilled Driver", "Usta Sürücü"),
            tier(20, "Safe Driving Ace", "Güvenli Sürüş"),
            tier(35, "Precision Driver", "Hassas Sürücü"),
            tier(50, "Roadcraft Master", "Yol Ustası")
        ),

        "Gardening" to listOf(
            tier(1, "Seed Keeper", "Tohum Koruyucusu"),
            tier(5, "Green Thumb", "Yeşil Parmak"),
            tier(10, "Cultivator", "Yetiştirici"),
            tier(20, "Garden Architect", "Bahçe Mimarı"),
            tier(35, "Botanical Artist", "Botanik Sanatçısı"),
            tier(50, "Garden Keeper", "Bahçe Koruyucusu")
        ),

        "Fitness" to listOf(
            tier(1, "Training Rookie", "Antrenman Çırağı"),
            tier(5, "Habit Builder", "Alışkanlık Ustası"),
            tier(10, "Athlete", "Atlet"),
            tier(20, "Performance Ace", "Performans Ustası"),
            tier(35, "Elite Competitor", "Elit Sporcu"),
            tier(50, "Peak Legend", "Zirve Efsanesi")
        ),

        "Running" to listOf(
            tier(1, "First Strides", "İlk Adımlar"),
            tier(5, "Road Runner", "Yol Koşucusu"),
            tier(10, "Endurance Pro", "Koşu Ustası"),
            tier(20, "Marathoner", "Maratoncu"),
            tier(35, "Kipchoge", "Kipchoge"),
            tier(50, "Endurance Legend", "Koşu Efsanesi")
        ),

        "Swimming" to listOf(
            tier(1, "Water Beginner", "Suya İlk Adım"),
            tier(5, "Lane Regular", "Kulvarın Tanıdığı"),
            tier(10, "Strong Swimmer", "Güçlü Yüzücü"),
            tier(20, "Aquatic Specialist", "Su Sporları Uzmanı"),
            tier(35, "Phelps's Standard", "Phelps Seviyesi"),
            tier(50, "Ocean Master", "Derinlik Ustası")
        ),

        "Cycling" to listOf(
            tier(1, "Pedal Starter", "Pedal Çırağı"),
            tier(5, "Road Explorer", "Yol Kaşifi"),
            tier(10, "Climber", "Tırmanış Ustası"),
            tier(20, "Peloton Pro", "Peloton Ustası"),
            tier(35, "Merckx's Standard", "Merckx Seviyesi"),
            tier(50, "Cycling Legend", "Bisiklet Efsanesi")
        ),

        "Yoga" to listOf(
            tier(1, "Breath Seeker", "Nefes Kaşifi"),
            tier(5, "Steady Yogi", "Dengeli Yogi"),
            tier(10, "Flow Keeper", "Akışın Koruyucusu"),
            tier(20, "Balance Adept", "Denge Ustası"),
            tier(35, "Balance Guide", "Denge Rehberi"),
            tier(50, "Stillness Master", "Dinginlik Ustası")
        ),

        "Health" to listOf(
            tier(1, "Wellbeing Seeker", "İyi Yaşam Kaşifi"),
            tier(5, "Health Habit Pro", "Sağlık Alışkanlığı"),
            tier(10, "Wellness Steward", "İyi Yaşam Rehberi"),
            tier(20, "Balance Strategist", "Denge Stratejisti"),
            tier(35, "Holistic Guide", "Bütünsel Rehber"),
            tier(50, "Vitality Guardian", "Zindelik Bekçisi")
        ),

        "Finance" to listOf(
            tier(1, "Budget Apprentice", "Bütçe Çırağı"),
            tier(5, "Cash-Flow Pro", "Nakit Akışı Ustası"),
            tier(10, "Financial Analyst", "Finans Analisti"),
            tier(20, "Finance Strategist", "Finans Stratejisti"),
            tier(35, "Benjamin Graham", "Benjamin Graham"),
            tier(50, "Finance Architect", "Finans Mimarı")
        ),

        "Business" to listOf(
            tier(1, "Idea Starter", "Fikir Başlatıcısı"),
            tier(5, "Builder", "Kurucu"),
            tier(10, "Operations Lead", "Operasyon Ustası"),
            tier(20, "Business Ace", "İş Stratejisti"),
            tier(35, "Visionary Founder", "Vizyoner Kurucu"),
            tier(50, "Industry Shaper", "Sektör Öncüsü")
        ),

        "Investing" to listOf(
            tier(1, "Investor Rookie", "Yatırımcı Adayı"),
            tier(5, "Asset Explorer", "Varlık Kaşifi"),
            tier(10, "Market Reader", "Piyasa Okuyucusu"),
            tier(20, "Portfolio Planner", "Portföy Ustası"),
            tier(35, "Warren Buffett", "Warren Buffett"),
            tier(50, "Capital Legend", "Sermaye Efsanesi")
        ),

        "Meditation" to listOf(
            tier(1, "Quiet Seeker", "Sessizlik Kaşifi"),
            tier(5, "Breath Keeper", "Nefesin Koruyucusu"),
            tier(10, "Mindful Adept", "Farkındalık Ustası"),
            tier(20, "Stillness Guide", "Dinginlik Rehberi"),
            tier(35, "Zen Virtuoso", "Zen Ustası"),
            tier(50, "Inner Citadel", "İçsel Kale")
        ),

        "Sleep" to listOf(
            tier(1, "Night Scout", "Gece Kaşifi"),
            tier(5, "Rest Routine Pro", "Dinlenme Rehberi"),
            tier(10, "Sleep Steward", "Uyku Düzeni Ustası"),
            tier(20, "Recovery Expert", "Toparlanma Uzmanı"),
            tier(35, "Dream Mapper", "Rüya Haritacısı"),
            tier(50, "Rest Guardian", "Uyku Koruyucusu")
        ),

        "Travel" to listOf(
            tier(1, "Local Explorer", "Yerel Kaşif"),
            tier(5, "Route Planner", "Rota Planlayıcısı"),
            tier(10, "World Wanderer", "Dünya Gezgini"),
            tier(20, "Expedition Leader", "Keşif Lideri"),
            tier(35, "Ibn Battuta", "İbn Battuta"),
            tier(50, "World Atlas", "Dünya Atlası")
        ),

        "Gaming" to listOf(
            tier(1, "New Player", "Yeni Oyuncu"),
            tier(5, "Tactics Learner", "Taktik Öğrencisi"),
            tier(10, "Tactical Genius", "Taktik Dehası"),
            tier(20, "Arena Champion", "Arena Şampiyonu"),
            tier(35, "Esports Prodigy", "E-spor Dehası"),
            tier(50, "Arena Legend", "Arena Efsanesi")
        )
    )

    /**
     * Verilen alan ve seviyeye karşılık gelen en yüksek ünvanı döndürür.
     */
    fun title(area: String, level: Int): String {
        val tiers = byArea[area]
            ?: return T(Ranks.title(level))

        val safeLevel = level.coerceAtLeast(1)

        val current = tiers.lastOrNull {
            safeLevel >= it.minLevel
        } ?: tiers.first()

        return if (Lang.tr) current.turkish else current.english
    }
}
