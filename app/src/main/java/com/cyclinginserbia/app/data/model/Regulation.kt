package com.cyclinginserbia.app.data.model

import androidx.annotation.DrawableRes

data class Regulation(
    val id: String,
    val title: String,
    val content: String,
    @DrawableRes val imageRes: Int? = null,
)

data class RegulationCategory(
    val id: String,
    val title: String,
    val items: List<Regulation>,
)
