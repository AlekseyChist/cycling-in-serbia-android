package com.cyclinginserbia.app.data.model

import androidx.annotation.DrawableRes

enum class ShopTab(val label: String) {
    ALL("All"),
    SHOPS("Shops"),
    SERVICES("Services"),
    FRIENDS("Friends"),
}

data class Shop(
    val id: String,
    val name: String,
    val category: String,
    val description: String,
    val location: String? = null,
    val link: String,
    val linkLabel: String,
    @DrawableRes val logoRes: Int? = null,
    val tabs: List<ShopTab>,
    val isPersonal: Boolean = false,
)
