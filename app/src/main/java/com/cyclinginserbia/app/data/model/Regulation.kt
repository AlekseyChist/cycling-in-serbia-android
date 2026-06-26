package com.cyclinginserbia.app.data.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class Regulation(
    val id: String,
    @StringRes val titleRes: Int,
    @StringRes val contentRes: Int,
    @DrawableRes val imageRes: Int? = null,
)

data class RegulationCategory(
    val id: String,
    @StringRes val titleRes: Int,
    val items: List<Regulation>,
)
