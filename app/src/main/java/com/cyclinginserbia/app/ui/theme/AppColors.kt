package com.cyclinginserbia.app.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Theme-aware color palette. Two instances exist (LightAppColors, DarkAppColors)
 * and the active one is provided through [LocalAppColors] in [CyclingInSerbiaTheme].
 *
 * Call sites use the top-level [AppColors] composable accessor; under the hood it
 * resolves to the current palette from [LocalAppColors]. Mirrors the web design
 * tokens (theme.css) plus the Tailwind palette actually used in the React app.
 *
 * Saturated brand and accent colors (Primary, Emerald500, etc.) intentionally
 * stay identical across themes — they read fine on both backgrounds. Only
 * surface/text/gray-scale + soft chip tints flip.
 */
@Immutable
class AppColorPalette(
    // Brand (Strava-orange) — theme-invariant
    val Primary: Color,
    val PrimaryDark: Color,
    val PrimaryForeground: Color,
    val Ring: Color,

    // Brand surfaces (warm tints — hero/banner/tip backgrounds)
    val Cream50: Color,
    val Cream100: Color,
    val OrangeTint: Color,
    val PeachOnBrand: Color,
    val StravaChipBg: Color,

    // Surfaces / text (semantic, from theme.css)
    val Background: Color,
    val Foreground: Color,
    val Card: Color,
    val CardForeground: Color,
    val Popover: Color,
    val PopoverForeground: Color,

    val Secondary: Color,
    val SecondaryForeground: Color,
    val Muted: Color,
    val MutedForeground: Color,
    val Accent: Color,
    val AccentForeground: Color,

    val Destructive: Color,
    val DestructiveForeground: Color,

    val Border: Color,
    val InputBackground: Color,
    val SwitchBackground: Color,

    // Gray scale (Tailwind, used everywhere)
    val Gray50: Color,
    val Gray100: Color,
    val Gray200: Color,
    val Gray300: Color,
    val Gray400: Color,
    val Gray500: Color,
    val Gray600: Color,
    val Gray700: Color,
    val Gray800: Color,
    val Gray900: Color,

    // Red (errors, destructive, "hard" difficulty)
    val Red50: Color,
    val Red200: Color,
    val Red300: Color,
    val Red500: Color,
    val Red600: Color,
    val Red700: Color,
    val Red800: Color,
    val Red900: Color,

    // Orange
    val Orange50: Color,
    val Orange100: Color,
    val Orange200: Color,
    val Orange300: Color,
    val Orange600: Color,
    val Orange900: Color,

    // Amber
    val Amber50: Color,
    val Amber100: Color,
    val Amber200: Color,
    val Amber300: Color,
    val Amber500: Color,
    val Amber600: Color,
    val Amber700: Color,
    val Amber800: Color,
    val Amber900: Color,

    // Emerald
    val Emerald50: Color,
    val Emerald100: Color,
    val Emerald200: Color,
    val Emerald300: Color,
    val Emerald500: Color,
    val Emerald600: Color,
    val Emerald700: Color,
    val Emerald900: Color,

    // Blue
    val Blue50: Color,
    val Blue200: Color,
    val Blue300: Color,
    val Blue500: Color,
    val Blue600: Color,
    val Blue700: Color,
    val Blue800: Color,
    val Blue900: Color,

    // Indigo
    val Indigo50: Color,
    val Indigo200: Color,
    val Indigo300: Color,
    val Indigo500: Color,
    val Indigo700: Color,
    val Indigo900: Color,

    // Purple / Violet
    val Purple50: Color,
    val Purple200: Color,
    val Purple300: Color,
    val Purple700: Color,
    val Purple900: Color,
    val Violet500: Color,

    // Soft chip tints — tuned per theme
    val ChipEasy: ChipPalette,
    val ChipMedium: ChipPalette,
    val ChipHard: ChipPalette,
    val ChipRoad: ChipPalette,
    val ChipGravel: ChipPalette,
    val ChipMixed: ChipPalette,
    val ChipEventUpcoming: ChipPalette,
    val ChipEventCanceled: ChipPalette,

    // True if this palette represents a dark theme — useful for the few
    // composables that need to branch on it (e.g. statusbar appearance).
    val isDark: Boolean,
)

internal val LocalAppColors = staticCompositionLocalOf { LightAppColors }

/**
 * Top-level façade — drop-in replacement for the previous static [AppColors] object.
 * Resolves at composition time via [LocalAppColors]. Must be called from a
 * @Composable scope; values used outside @Composable scope (e.g. polyline
 * colors in [DifficultyMapColors]) are kept as plain constants.
 */
val AppColors: AppColorPalette
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current

// ──────────────────────────────────────────────
// Soft chip palette container (used in cards)
// ──────────────────────────────────────────────
@Immutable
data class ChipPalette(val background: Color, val text: Color, val border: Color)

// ──────────────────────────────────────────────
// Light palette — current production values
// ──────────────────────────────────────────────
val LightAppColors = AppColorPalette(
    // Brand
    Primary = Color(0xFFFC5200),
    PrimaryDark = Color(0xFFE34402),
    PrimaryForeground = Color(0xFFFFFFFF),
    Ring = Color(0xFFFC5200),

    // Brand surfaces
    Cream50 = Color(0xFFFFF5F0),
    Cream100 = Color(0xFFFFE8DC),
    OrangeTint = Color(0xFFFEF3EC),
    PeachOnBrand = Color(0xFFFFD6C0),
    StravaChipBg = Color(0xFFFFF0EB),

    // Surfaces / text
    Background = Color(0xFFFFFFFF),
    Foreground = Color(0xFF1A1A1A),
    Card = Color(0xFFFFFFFF),
    CardForeground = Color(0xFF1A1A1A),
    Popover = Color(0xFFFFFFFF),
    PopoverForeground = Color(0xFF1A1A1A),

    Secondary = Color(0xFFF5F5F5),
    SecondaryForeground = Color(0xFF1A1A1A),
    Muted = Color(0xFFF9F9F9),
    MutedForeground = Color(0xFF6B7280),
    Accent = Color(0xFFF3F4F6),
    AccentForeground = Color(0xFF1A1A1A),

    Destructive = Color(0xFFEF4444),
    DestructiveForeground = Color(0xFFFFFFFF),

    Border = Color(0x14000000), // rgba(0,0,0,0.08)
    InputBackground = Color(0xFFF9F9F9),
    SwitchBackground = Color(0xFFD1D5DB),

    // Gray
    Gray50 = Color(0xFFF9FAFB),
    Gray100 = Color(0xFFF3F4F6),
    Gray200 = Color(0xFFE5E7EB),
    Gray300 = Color(0xFFD1D5DB),
    Gray400 = Color(0xFF9CA3AF),
    Gray500 = Color(0xFF6B7280),
    Gray600 = Color(0xFF4B5563),
    Gray700 = Color(0xFF374151),
    Gray800 = Color(0xFF1F2937),
    Gray900 = Color(0xFF111827),

    // Red
    Red50 = Color(0xFFFEF2F2),
    Red200 = Color(0xFFFECACA),
    Red300 = Color(0xFFFCA5A5),
    Red500 = Color(0xFFEF4444),
    Red600 = Color(0xFFDC2626),
    Red700 = Color(0xFFB91C1C),
    Red800 = Color(0xFF991B1B),
    Red900 = Color(0xFF7F1D1D),

    // Orange
    Orange50 = Color(0xFFFFF7ED),
    Orange100 = Color(0xFFFFEDD5),
    Orange200 = Color(0xFFFED7AA),
    Orange300 = Color(0xFFFDBA74),
    Orange600 = Color(0xFFEA580C),
    Orange900 = Color(0xFF7C2D12),

    // Amber
    Amber50 = Color(0xFFFFFBEB),
    Amber100 = Color(0xFFFEF3C7),
    Amber200 = Color(0xFFFDE68A),
    Amber300 = Color(0xFFFCD34D),
    Amber500 = Color(0xFFF59E0B),
    Amber600 = Color(0xFFD97706),
    Amber700 = Color(0xFFB45309),
    Amber800 = Color(0xFF92400E),
    Amber900 = Color(0xFF78350F),

    // Emerald
    Emerald50 = Color(0xFFECFDF5),
    Emerald100 = Color(0xFFD1FAE5),
    Emerald200 = Color(0xFFA7F3D0),
    Emerald300 = Color(0xFF6EE7B7),
    Emerald500 = Color(0xFF10B981),
    Emerald600 = Color(0xFF059669),
    Emerald700 = Color(0xFF047857),
    Emerald900 = Color(0xFF064E3B),

    // Blue
    Blue50 = Color(0xFFEFF6FF),
    Blue200 = Color(0xFFBFDBFE),
    Blue300 = Color(0xFF93C5FD),
    Blue500 = Color(0xFF3B82F6),
    Blue600 = Color(0xFF2563EB),
    Blue700 = Color(0xFF1D4ED8),
    Blue800 = Color(0xFF1E40AF),
    Blue900 = Color(0xFF1E3A8A),

    // Indigo
    Indigo50 = Color(0xFFEEF2FF),
    Indigo200 = Color(0xFFC7D2FE),
    Indigo300 = Color(0xFFA5B4FC),
    Indigo500 = Color(0xFF6366F1),
    Indigo700 = Color(0xFF4338CA),
    Indigo900 = Color(0xFF312E81),

    // Purple / Violet
    Purple50 = Color(0xFFFAF5FF),
    Purple200 = Color(0xFFE9D5FF),
    Purple300 = Color(0xFFD8B4FE),
    Purple700 = Color(0xFF7E22CE),
    Purple900 = Color(0xFF581C87),
    Violet500 = Color(0xFF8B5CF6),

    // Light chip palettes — soft tint backgrounds with darker text/border.
    ChipEasy = ChipPalette(Color(0xFFECFDF5), Color(0xFF047857), Color(0xFFA7F3D0)),
    ChipMedium = ChipPalette(Color(0xFFFFFBEB), Color(0xFFB45309), Color(0xFFFDE68A)),
    ChipHard = ChipPalette(Color(0xFFFEF2F2), Color(0xFFB91C1C), Color(0xFFFECACA)),
    ChipRoad = ChipPalette(Color(0xFFEFF6FF), Color(0xFF1D4ED8), Color(0xFFBFDBFE)),
    ChipGravel = ChipPalette(Color(0xFFFAF5FF), Color(0xFF7E22CE), Color(0xFFE9D5FF)),
    ChipMixed = ChipPalette(Color(0xFFEEF2FF), Color(0xFF4338CA), Color(0xFFC7D2FE)),
    ChipEventUpcoming = ChipPalette(Color(0xFFECFDF5), Color(0xFF047857), Color(0xFFA7F3D0)),
    ChipEventCanceled = ChipPalette(Color(0xFFFEF2F2), Color(0xFFB91C1C), Color(0xFFFECACA)),

    isDark = false,
)

// ──────────────────────────────────────────────
// Dark palette — defined here so the file is self-contained, but
// only activated when ThemeMode resolves to dark in CyclingInSerbiaTheme.
// ──────────────────────────────────────────────
val DarkAppColors = AppColorPalette(
    // Brand — same orange. Reads well on both backgrounds.
    Primary = Color(0xFFFC5200),
    PrimaryDark = Color(0xFFE34402),
    PrimaryForeground = Color(0xFFFFFFFF),
    Ring = Color(0xFFFC5200),

    // Warm tints — darkened to sit on a near-black background.
    Cream50 = Color(0xFF2A1A12),
    Cream100 = Color(0xFF3A2418),
    OrangeTint = Color(0xFF2A1A12),
    PeachOnBrand = Color(0xFFFFD6C0),
    StravaChipBg = Color(0xFF2A1A12),

    // Surfaces / text — Material 3 dark-surface levels.
    Background = Color(0xFF0F0F0F),
    Foreground = Color(0xFFF5F5F5),
    Card = Color(0xFF1C1C1E),
    CardForeground = Color(0xFFF5F5F5),
    Popover = Color(0xFF1C1C1E),
    PopoverForeground = Color(0xFFF5F5F5),

    Secondary = Color(0xFF2A2A2C),
    SecondaryForeground = Color(0xFFF5F5F5),
    Muted = Color(0xFF1C1C1E),
    MutedForeground = Color(0xFF9CA3AF),
    Accent = Color(0xFF2A2A2C),
    AccentForeground = Color(0xFFF5F5F5),

    Destructive = Color(0xFFEF4444),
    DestructiveForeground = Color(0xFFFFFFFF),

    Border = Color(0x33FFFFFF), // rgba(255,255,255,0.2)
    InputBackground = Color(0xFF1C1C1E),
    SwitchBackground = Color(0xFF4B5563),

    // Gray scale — flipped so semantic intent (e.g. Gray100 = "very subtle tint")
    // still reads correctly on a dark background.
    Gray50 = Color(0xFF111827),
    Gray100 = Color(0xFF1F2937),
    Gray200 = Color(0xFF374151),
    Gray300 = Color(0xFF4B5563),
    Gray400 = Color(0xFF6B7280),
    Gray500 = Color(0xFF9CA3AF),
    Gray600 = Color(0xFFD1D5DB),
    Gray700 = Color(0xFFE5E7EB),
    Gray800 = Color(0xFFF3F4F6),
    Gray900 = Color(0xFFF9FAFB),

    // Saturated accent scales — same hex as light, they pop on dark too.
    Red50 = Color(0xFFFEF2F2),
    Red200 = Color(0xFFFECACA),
    Red300 = Color(0xFFFCA5A5),
    Red500 = Color(0xFFEF4444),
    Red600 = Color(0xFFDC2626),
    Red700 = Color(0xFFB91C1C),
    Red800 = Color(0xFF991B1B),
    Red900 = Color(0xFF7F1D1D),

    Orange50 = Color(0xFFFFF7ED),
    Orange100 = Color(0xFFFFEDD5),
    Orange200 = Color(0xFFFED7AA),
    Orange300 = Color(0xFFFDBA74),
    Orange600 = Color(0xFFEA580C),
    Orange900 = Color(0xFF7C2D12),

    Amber50 = Color(0xFFFFFBEB),
    Amber100 = Color(0xFFFEF3C7),
    Amber200 = Color(0xFFFDE68A),
    Amber300 = Color(0xFFFCD34D),
    Amber500 = Color(0xFFF59E0B),
    Amber600 = Color(0xFFD97706),
    Amber700 = Color(0xFFB45309),
    Amber800 = Color(0xFF92400E),
    Amber900 = Color(0xFF78350F),

    Emerald50 = Color(0xFFECFDF5),
    Emerald100 = Color(0xFFD1FAE5),
    Emerald200 = Color(0xFFA7F3D0),
    Emerald300 = Color(0xFF6EE7B7),
    Emerald500 = Color(0xFF10B981),
    Emerald600 = Color(0xFF059669),
    Emerald700 = Color(0xFF047857),
    Emerald900 = Color(0xFF064E3B),

    Blue50 = Color(0xFFEFF6FF),
    Blue200 = Color(0xFFBFDBFE),
    Blue300 = Color(0xFF93C5FD),
    Blue500 = Color(0xFF3B82F6),
    Blue600 = Color(0xFF2563EB),
    Blue700 = Color(0xFF1D4ED8),
    Blue800 = Color(0xFF1E40AF),
    Blue900 = Color(0xFF1E3A8A),

    Indigo50 = Color(0xFFEEF2FF),
    Indigo200 = Color(0xFFC7D2FE),
    Indigo300 = Color(0xFFA5B4FC),
    Indigo500 = Color(0xFF6366F1),
    Indigo700 = Color(0xFF4338CA),
    Indigo900 = Color(0xFF312E81),

    Purple50 = Color(0xFFFAF5FF),
    Purple200 = Color(0xFFE9D5FF),
    Purple300 = Color(0xFFD8B4FE),
    Purple700 = Color(0xFF7E22CE),
    Purple900 = Color(0xFF581C87),
    Violet500 = Color(0xFF8B5CF6),

    // Dark chip palettes — deep tinted bg, light text, mid-tone border.
    ChipEasy = ChipPalette(Color(0xFF064E3B), Color(0xFF6EE7B7), Color(0xFF047857)),
    ChipMedium = ChipPalette(Color(0xFF78350F), Color(0xFFFCD34D), Color(0xFFB45309)),
    ChipHard = ChipPalette(Color(0xFF7F1D1D), Color(0xFFFCA5A5), Color(0xFFB91C1C)),
    ChipRoad = ChipPalette(Color(0xFF1E3A8A), Color(0xFF93C5FD), Color(0xFF1D4ED8)),
    ChipGravel = ChipPalette(Color(0xFF581C87), Color(0xFFD8B4FE), Color(0xFF7E22CE)),
    ChipMixed = ChipPalette(Color(0xFF312E81), Color(0xFFA5B4FC), Color(0xFF4338CA)),
    ChipEventUpcoming = ChipPalette(Color(0xFF064E3B), Color(0xFF6EE7B7), Color(0xFF047857)),
    ChipEventCanceled = ChipPalette(Color(0xFF7F1D1D), Color(0xFFFCA5A5), Color(0xFFB91C1C)),

    isDark = true,
)

// ──────────────────────────────────────────────
// Semantic chip / map color shortcuts (composable accessors over the active palette)
// ──────────────────────────────────────────────

/**
 * Soft chip variants used on cards (bg-*-50 + text-*-700 + border-*-200 in light;
 * bg-*-900 + text-*-300 + border-*-700 in dark).
 */
object ChipColors {
    val Easy: ChipPalette
        @Composable @ReadOnlyComposable get() = AppColors.ChipEasy
    val Medium: ChipPalette
        @Composable @ReadOnlyComposable get() = AppColors.ChipMedium
    val Hard: ChipPalette
        @Composable @ReadOnlyComposable get() = AppColors.ChipHard
    val Road: ChipPalette
        @Composable @ReadOnlyComposable get() = AppColors.ChipRoad
    val Gravel: ChipPalette
        @Composable @ReadOnlyComposable get() = AppColors.ChipGravel
    val Mixed: ChipPalette
        @Composable @ReadOnlyComposable get() = AppColors.ChipMixed
    val EventUpcoming: ChipPalette
        @Composable @ReadOnlyComposable get() = AppColors.ChipEventUpcoming
    val EventCanceled: ChipPalette
        @Composable @ReadOnlyComposable get() = AppColors.ChipEventCanceled
}

/** Difficulty shortcut — same hue across themes. */
object DifficultyColors {
    val Easy: Color
        @Composable @ReadOnlyComposable get() = AppColors.Emerald500
    val Medium: Color
        @Composable @ReadOnlyComposable get() = AppColors.Amber500
    val Hard: Color
        @Composable @ReadOnlyComposable get() = AppColors.Red500
}

/** Surface-type shortcut — same hue across themes. */
object SurfaceTypeColors {
    val Road: Color
        @Composable @ReadOnlyComposable get() = AppColors.Blue500
    val Gravel: Color
        @Composable @ReadOnlyComposable get() = AppColors.Violet500
    val Mixed: Color
        @Composable @ReadOnlyComposable get() = AppColors.Indigo500
}

/**
 * Map polyline colors. These are referenced from non-@Composable code that
 * builds osmdroid overlays, so they stay as plain constants. Values match the
 * web spec where Medium is **blue** on the map (visibility on OSM tiles) but
 * **amber** in card chips.
 */
object DifficultyMapColors {
    val Easy = Color(0xFF10B981)   // Emerald500
    val Medium = Color(0xFF3B82F6) // Blue500 — differs from chip
    val Hard = Color(0xFFEF4444)   // Red500
}
