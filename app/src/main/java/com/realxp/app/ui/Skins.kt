package com.realxp.app.ui

fun skinFor(area: String): ArenaSkin = when (area) {
    "Coding" -> CodingFixed
    "English" -> EnglishFixed
    "History" -> HistorySkin
    "AI" -> AISkin
    "Math" -> MathSkin
    "Science" -> ScienceSkin
    else -> DefaultSkin
}
