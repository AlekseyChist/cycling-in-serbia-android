package com.cyclinginserbia.app.util

/**
 * Serbian Latin letters that users routinely type without their diacritics.
 * Only lowercase keys are needed — [foldForSearch] lowercases before mapping.
 *
 * Note that `đ` is a distinct letter rather than `d` plus a combining mark, so a
 * Normalizer-based approach would leave it untouched; an explicit table avoids
 * that trap entirely.
 */
private val SERBIAN_FOLDED_LETTERS = mapOf(
    'š' to 's',
    'č' to 'c',
    'ć' to 'c',
    'ž' to 'z',
    'đ' to 'd',
)

/**
 * Lowercases the string and strips Serbian Latin diacritics, so that a query
 * typed on a plain keyboard ("sabac") still matches "Šabac". Everything else —
 * ASCII letters, digits, spaces, punctuation — passes through unchanged.
 */
fun String.foldForSearch(): String {
    val lowered = lowercase()
    return buildString(lowered.length) {
        for (ch in lowered) append(SERBIAN_FOLDED_LETTERS[ch] ?: ch)
    }
}

/**
 * True when the receiver contains [query] once both sides are folded. An empty
 * query matches everything.
 */
fun String.containsFolded(query: String): Boolean {
    if (query.isEmpty()) return true
    return foldForSearch().contains(query.foldForSearch())
}
