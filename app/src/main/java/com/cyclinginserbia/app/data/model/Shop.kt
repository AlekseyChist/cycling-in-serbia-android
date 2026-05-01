package com.cyclinginserbia.app.data.model

import androidx.annotation.DrawableRes

enum class ShopCategory(val title: String) {
    sellingPlatform("Selling Platforms"),
    shop("Shops"),
    service("Services"),
    friend("Friends"),
}

data class Shop(
    val id: String,
    val name: String,
    val category: ShopCategory,
    val type: String,
    val description: String,
    val location: String,
    val link: String,
    @DrawableRes val logoResId: Int? = null,
) {
    val isPhone: Boolean get() = link.startsWith("tel:") || link.startsWith("+")
}
