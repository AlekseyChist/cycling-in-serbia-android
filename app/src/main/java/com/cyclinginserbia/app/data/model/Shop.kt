package com.cyclinginserbia.app.data.model

import androidx.annotation.DrawableRes

enum class ShopTab(val label: String) {
    ALL("All"),
    SHOPS("Shops"),
    SERVICES("Services"),
    FRIENDS("Friends"),
}

/**
 * A geo-pin for a shop on the Tracks map. Empty list = shop never shown on the map
 * (online-only, mechanic without a public address, etc). Multi-element list supports
 * chains like Decathlon and Planet Bike — each pin gets its own marker.
 */
data class ShopLocation(
    val lat: Double,
    val lng: Double,
    val address: String? = null,
)

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
    val locations: List<ShopLocation> = emptyList(),
)
