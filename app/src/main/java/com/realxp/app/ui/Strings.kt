package com.realxp.app.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.util.Locale

object Lang {
    var tr by mutableStateOf(false)
}

val MONTHS_EN = listOf(
    "January", "February", "March", "April", "May", "June",
    "July", "August", "September", "October", "November", "December"
)

object Catalog {
    val categories = listOf("All", "Learn", "Skills", "Body", "Money", "Life")

    val all = listOf(
        "English", "Languages", "Reading", "History", "Math", "Science", "Writing",
        "Coding", "AI", "Design", "Photography", "Art", "Music", "Speaking",
        "Cooking", "Driving", "Gardening",
        "Fitness", "Running", "Swimming", "Cycling", "Yoga", "Health",
        "Finance", "Business", "Investing",
        "Meditation", "Sleep", "Travel", "Gaming"
    )

    private val cat = mapOf(
        "English" to "Learn", "Languages" to "Learn", "Reading" to "Learn",
        "History" to "Learn", "Math" to "Learn", "Science" to "Learn", "Writing" to "Learn",
        "Coding" to "Skills", "AI" to "Skills", "Design" to "Skills",
        "Photography" to "Skills", "Art" to "Skills", "Music" to "Skills",
        "Speaking" to "Skills", "Cooking" to "Skills", "Driving" to "Skills",
        "Gardening" to "Skills",
        "Fitness" to "Body", "Running" to "Body", "Swimming" to "Body",
        "Cycling" to "Body", "Yoga" to "Body", "Health" to "Body",
        "Finance" to "Money", "Business" to "Money", "Investing" to "Money",
        "Meditation" to "Life", "Sleep" to "Life", "Travel" to "Life", "Gaming" to "Life"
    )

    fun categoryOf(name: String): String = cat[name] ?: "Life"
}

private val AREA_TR = mapOf(
    "English" to "İngilizce", "Languages" to "Diller", "Reading" to "Okuma",
    "History" to "Tarih", "Math" to "Matematik", "Science" to "Bilim", "Writing" to "Yazı",
    "Coding" to "Kodlama", "AI" to "Yapay Zeka", "Design" to "Tasarım",
    "Photography" to "Fotoğraf", "Art" to "Sanat", "Music" to "Müzik",
    "Speaking" to "Konuşma", "Cooking" to "Yemek", "Driving" to "Sürüş",
    "Gardening" to "Bahçe", "Fitness" to "Spor", "Running" to "Koşu",
    "Swimming" to "Yüzme", "Cycling" to "Bisiklet", "Yoga" to "Yoga",
    "Health" to "Sağlık", "Finance" to "Finans", "Business" to "İş",
    "Investing" to "Yatırım", "Meditation" to "Meditasyon", "Sleep" to "Uyku",
    "Travel" to "Seyahat", "Gaming" to "Oyun"
)

fun A(name: String): String = if (Lang.tr) (AREA_TR[name] ?: name) else name

private val TR: Map<String, String> = mapOf(
    "h" to "sa", "m" to "dk", "min" to "dk", "day" to "gün", "days" to "gün",

    "January" to "Ocak", "February" to "Şubat", "March" to "Mart", "April" to "Nisan",
    "May" to "Mayıs", "June" to "Haziran", "July" to "Temmuz", "August" to "Ağustos",
    "September" to "Eylül", "October" to "Ekim", "November" to "Kasım", "December" to "Aralık",

    "Novice" to "Acemi", "Apprentice" to "Çırak", "Adept" to "Kabiliyetli",
    "Veteran" to "Tecrübeli", "Elite" to "Seçkin", "Master" to "Usta",
    "Grandmaster" to "Üstat", "Legend" to "Efsane",

    "All" to "Tümü", "Learn" to "Öğren", "Skills" to "Beceri", "Body" to "Beden",
    "Money" to "Para", "Life" to "Yaşam",

    "Welcome to" to "Hoş geldin",
    "Life doesn't hand out levels." to "Hayat sana seviye vermez.",
    "Earn yours." to "Seviyeni kazan.",
    "Get started  →" to "Başla  →",

    "←  Back" to "←  Geri",
    "Choose your areas" to "Alanlarını seç",
    "Add areas" to "Alan ekle",
    "Pick everything\nyou want to level up." to "Seviye atlamak istediğin\nher şeyi seç.",
    "Add more\narenas." to "Daha fazla\nalan ekle.",
    "Continue" to "Devam",
    "Add" to "Ekle",
    "%d selected" to "%d seçildi",
    "Select at least one area to continue." to "Devam etmek için en az bir alan seç.",
    "You've added every arena." to "Tüm alanları ekledin.",
    "Nothing left in this category." to "Bu kategoride eklenecek bir şey kalmadı.",
    "LV" to "SV",
    "Add Arena" to "Alan Ekle",

    "Level" to "Seviye",
    "invested" to "çalışıldı",
    "Your arenas" to "Alanların",
    "Edit" to "Düzenle",
    "Done" to "Tamam",
    "Remove %s?" to "%s kaldırılsın mı?",
    "Keep your progress hidden, or erase it for good?" to "İlerlemen gizli kalsın mı, yoksa tamamen silinsin mi?",
    "Remove, keep progress" to "Kaldır, ilerlemeyi sakla",
    "Remove and erase progress" to "Kaldır ve ilerlemeyi sil",
    "Cancel" to "Vazgeç",
    "Settings" to "Ayarlar",
    "Dark mode" to "Karanlık mod",
    "Easier on the eyes at night." to "Geceleri gözlere daha rahat.",
    "Language" to "Dil",
    "1 day streak" to "1 günlük seri",
    "%d day streak" to "%d günlük seri",
    "%s report is ready" to "%s raporun hazır",
    "See how your month went" to "Ayının nasıl geçtiğine bak",

    "Level %d" to "Seviye %d",
    "My stats" to "İstatistiklerim",
    "Current Streak" to "Güncel Seri",
    "Best: %d" to "En iyi: %d",
    "Total XP" to "Toplam XP",
    "Time Invested" to "Harcanan Süre",
    "Total Logs" to "Toplam Kayıt",
    "Monthly Reports" to "Aylık Raporlar",
    "See every month at a glance" to "Her ayı tek bakışta gör",
    "Activity  ·  last 16 weeks" to "Aktivite  ·  son 16 hafta",
    "Less" to "Az",
    "More" to "Çok",

    "Monthly report" to "Aylık rapor",
    "In progress" to "Devam ediyor",
    "Complete" to "Tamamlandı",
    "No activity in %s." to "%s ayında hiç aktivite yok.",
    "Total Time" to "Toplam Süre",
    "XP Earned" to "Kazanılan XP",
    "Active Days" to "Aktif Günler",
    "consistency" to "düzenlilik",
    "Best Day" to "En İyi Gün",
    "Insights" to "Öne çıkanlar",
    "Longest streak" to "En uzun seri",
    "Average on active days" to "Aktif günlerde ortalama",
    "Most invested" to "En çok emek verilen",
    "Calendar" to "Takvim",
    "Week by week" to "Hafta hafta",
    "Arenas" to "Alanlar",
    "First tracked month" to "İlk takip edilen ay",
    "vs last month" to "önceki aya göre",

    "Overview" to "Genel",
    "History" to "Geçmiş",
    "Log your work" to "Çalışmanı kaydet",
    "Other (min)" to "Diğer (dk)",
    "Log" to "Kaydet",
    "Type any amount, for example 5." to "İstediğin kadar yaz, örneğin 5.",
    "Type how many minutes you worked." to "Kaç dakika çalıştığını yaz.",
    "A day only has 24 hours." to "Bir günde sadece 24 saat var.",
    "A day has 24 hours. %d min left today." to "Bir günde 24 saat var. Bugün %d dk kaldı.",
    "+%d XP  ·  %d min" to "+%d XP  ·  %d dk",
    "Today's XP" to "Bugünkü XP",
    "Today" to "Bugün",
    "Total" to "Toplam",
    "Logs" to "Kayıt",
    "Last 7 days" to "Son 7 gün",
    "Last 16 weeks" to "Son 16 hafta",
    "YOUR LOGS  ·  %d" to "KAYITLARIN  ·  %d",
    "No logs yet. Log your first session in Overview." to "Henüz kayıt yok. İlk çalışmanı Genel sekmesinde kaydet.",
    "Undo" to "Geri al",
    "Undone." to "Geri alındı.",
    "Be honest." to "Dürüst ol.",
    "You actually did this, right? Or are you just fooling yourself? \uD83D\uDE42" to
        "Bunu gerçekten yaptın, değil mi? Yoksa sadece kendini mi kandırıyorsun? \uD83D\uDE42",
    "Yes, I did" to "Evet, yaptım",
    "Not really" to "Pek değil",
    "Respect for the honesty." to "Dürüstlüğüne saygı.",
    "Tap to continue" to "Devam etmek için dokun",
    "Level up" to "Seviye atladın",
    "+%d LEVELS" to "+%d SEVİYE"
)

private val TR_UP: Map<String, String> by lazy {
    val loc = Locale("tr", "TR")
    val out = HashMap<String, String>()
    for ((k, v) in TR) {
        if (!k.contains('%')) out[k.uppercase(Locale.ENGLISH)] = v.uppercase(loc)
    }
    out
}

fun T(en: String): String {
    if (!Lang.tr) return en
    val a = TR[en]
    if (a != null) return a
    val b = TR_UP[en]
    return b ?: en
}

fun TF(en: String, vararg args: Any): String = T(en).format(*args)

fun up(s: String): String = s.uppercase(if (Lang.tr) Locale("tr", "TR") else Locale.ENGLISH)

fun ftime(sec: Int): String {
    val h = sec / 3600
    val m = (sec % 3600) / 60
    return if (h > 0) "$h${T("h")} $m${T("m")}" else "$m${T("m")}"
}

fun monthName(m: Int): String = T(MONTHS_EN[m])

fun dayLetter(i: Int): String =
    if (Lang.tr) listOf("P", "S", "Ç", "P", "C", "C", "P")[i]
    else listOf("M", "T", "W", "T", "F", "S", "S")[i]

fun pctText(p: Int): String = if (Lang.tr) "%$p" else "$p%"

fun daysText(n: Int): String = if (n == 1) "1 ${T("day")}" else "$n ${T("days")}"
