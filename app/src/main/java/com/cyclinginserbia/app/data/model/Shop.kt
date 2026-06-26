package com.cyclinginserbia.app.data.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.cyclinginserbia.app.R

enum class ShopTab(@StringRes val labelRes: Int) {
    ALL(R.string.shoptab_all),
    SHOPS(R.string.shoptab_shops),
    SERVICES(R.string.shoptab_services),
    FRIENDS(R.string.shoptab_friends),
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
