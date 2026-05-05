package com.cyclinginserbia.app.ui.screens.tracks

import androidx.annotation.DrawableRes
import com.cyclinginserbia.app.R
import com.cyclinginserbia.app.data.model.Track

/**
 * Maps a track to a small list-card drawable. Resolves by parsing the
 * `thumbnailUrl` filename (e.g. `/routes_images/Icon Belgrade-Leskovac.png`)
 * and looking it up in [LIST_DRAWABLES]. Falls back to `icon_misc`.
 */
@DrawableRes
fun trackThumbnailRes(track: Track): Int =
    LIST_DRAWABLES[normalisedThumbnailKey(track.thumbnailUrl)] ?: R.drawable.icon_misc

/**
 * Hero-sized drawable for the TrackDetail screen. The same `thumbnailUrl`
 * filename is mapped to the bigger `dbb__*` / `dbb_*` photo. Falls back to
 * `dbb_misc`.
 */
@DrawableRes
fun trackHeroRes(track: Track): Int =
    HERO_DRAWABLES[normalisedThumbnailKey(track.thumbnailUrl)] ?: R.drawable.dbb_misc

private fun normalisedThumbnailKey(thumbnailUrl: String): String =
    thumbnailUrl
        .substringAfterLast('/')
        .substringBeforeLast('.')
        .lowercase()
        .map { ch -> if (ch == ' ' || ch == '-' || ch == '.') '_' else ch }
        .joinToString("")
        .replace(Regex("_+"), "_")
        .trim('_')

private val LIST_DRAWABLES: Map<String, Int> = mapOf(
    "icon_belgrade_leskovac" to R.drawable.icon_belgrade_leskovac,
    "icon_belgrade_miroc" to R.drawable.icon_belgrade_miroc,
    "icon_belgrade_sarajevo" to R.drawable.icon_belgrade_sarajevo,
    "icon_golija_gravel" to R.drawable.icon_golija_gravel,
    "icon_tara_gravel" to R.drawable.icon_tara_gravel,
    "icon_coffee_1_x" to R.drawable.icon_coffee_1_x,
    "icon_coffee_2_x" to R.drawable.icon_coffee_2_x,
    "icon_coffee_3_x" to R.drawable.icon_coffee_3_x,
    "icon_coffee_4_x" to R.drawable.icon_coffee_4_x,
    "icon_coffee_5_x" to R.drawable.icon_coffee_5_x,
    "icon_dark" to R.drawable.icon_dark,
    "icon_sun" to R.drawable.icon_sun,
    "icon_misc" to R.drawable.icon_misc,
)

private val HERO_DRAWABLES: Map<String, Int> = mapOf(
    "icon_belgrade_leskovac" to R.drawable.dbb__belgrade_leskovac,
    "icon_belgrade_miroc" to R.drawable.dbb__belgrade_miroc,
    "icon_belgrade_sarajevo" to R.drawable.dbb__belgrade_sarajevo,
    "icon_golija_gravel" to R.drawable.dbb__golija__gravel,
    "icon_tara_gravel" to R.drawable.dbb__tara_gravel,
    "icon_coffee_1_x" to R.drawable.dbb_coffee,
    "icon_coffee_2_x" to R.drawable.dbb_coffee,
    "icon_coffee_3_x" to R.drawable.dbb_coffee,
    "icon_coffee_4_x" to R.drawable.dbb_coffee,
    "icon_coffee_5_x" to R.drawable.dbb_coffee,
    "icon_dark" to R.drawable.dbb_dark,
    "icon_sun" to R.drawable.dbb_sun,
    "icon_misc" to R.drawable.dbb_misc,
)
