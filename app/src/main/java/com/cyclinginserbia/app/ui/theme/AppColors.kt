package com.cyclinginserbia.app.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Single source of truth for all colors.
 * Mirrors the web design tokens (theme.css) + Tailwind palette actually used in the React app.
 * Do NOT add ad-hoc Color(0x...) calls in composables — extend this object instead.
 */
object AppColors {

    // ──────────────────────────────────────────────
    // Brand (Strava-orange)
    // ──────────────────────────────────────────────
    val Primary             = Color(0xFFFC5200)
    val PrimaryDark         = Color(0xFFE34402) // hero-gradient bottom stop
    val PrimaryForeground   = Color(0xFFFFFFFF)
    val Ring                = Color(0xFFFC5200) // focus ring = primary

    // ──────────────────────────────────────────────
    // Brand surfaces (warm tints — hero/banner/tip backgrounds)
    // ──────────────────────────────────────────────
    val Cream50         = Color(0xFFFFF5F0) // onboarding gradient top, event date badge bg, DBB tip card bg
    val Cream100        = Color(0xFFFFE8DC) // onboarding gradient bottom
    val OrangeTint      = Color(0xFFFEF3EC) // 44dp icon container in onboarding feature list
    val PeachOnBrand    = Color(0xFFFFD6C0) // muted text on solid Primary hero
    val StravaChipBg    = Color(0xFFFFF0EB) // Strava chip background

    // ──────────────────────────────────────────────
    // Surfaces / text (semantic, from theme.css)
    // ──────────────────────────────────────────────
    val Background          = Color(0xFFFFFFFF)
    val Foreground          = Color(0xFF1A1A1A)
    val Card                = Color(0xFFFFFFFF)
    val CardForeground      = Color(0xFF1A1A1A)
    val Popover             = Color(0xFFFFFFFF)
    val PopoverForeground   = Color(0xFF1A1A1A)

    val Secondary           = Color(0xFFF5F5F5)
    val SecondaryForeground = Color(0xFF1A1A1A)
    val Muted               = Color(0xFFF9F9F9)
    val MutedForeground     = Color(0xFF6B7280)
    val Accent              = Color(0xFFF3F4F6)
    val AccentForeground    = Color(0xFF1A1A1A)

    val Destructive         = Color(0xFFEF4444)
    val DestructiveForeground = Color(0xFFFFFFFF)

    /** rgba(0,0,0,0.08) from theme.css */
    val Border              = Color(0x14000000)
    val InputBackground     = Color(0xFFF9F9F9)
    val SwitchBackground    = Color(0xFFD1D5DB)

    // ──────────────────────────────────────────────
    // Gray scale (Tailwind, used everywhere)
    // ──────────────────────────────────────────────
    val Gray50  = Color(0xFFF9FAFB)
    val Gray100 = Color(0xFFF3F4F6)
    val Gray200 = Color(0xFFE5E7EB)
    val Gray300 = Color(0xFFD1D5DB)
    val Gray400 = Color(0xFF9CA3AF)
    val Gray500 = Color(0xFF6B7280)
    val Gray600 = Color(0xFF4B5563)
    val Gray700 = Color(0xFF374151)
    val Gray800 = Color(0xFF1F2937)
    val Gray900 = Color(0xFF111827)

    // ──────────────────────────────────────────────
    // Red (errors, destructive, "hard" difficulty)
    // ──────────────────────────────────────────────
    val Red50   = Color(0xFFFEF2F2)
    val Red200  = Color(0xFFFECACA)
    val Red500  = Color(0xFFEF4444)
    val Red600  = Color(0xFFDC2626)
    val Red700  = Color(0xFFB91C1C)
    val Red800  = Color(0xFF991B1B)
    val Red900  = Color(0xFF7F1D1D)

    // ──────────────────────────────────────────────
    // Orange (info banners, onboarding accent #1)
    // ──────────────────────────────────────────────
    val Orange50  = Color(0xFFFFF7ED)
    val Orange100 = Color(0xFFFFEDD5)
    val Orange200 = Color(0xFFFED7AA)
    val Orange600 = Color(0xFFEA580C)
    val Orange900 = Color(0xFF7C2D12)

    // ──────────────────────────────────────────────
    // Amber (warnings, "medium" difficulty, onboarding accent #2)
    // ──────────────────────────────────────────────
    val Amber50  = Color(0xFFFFFBEB)
    val Amber100 = Color(0xFFFEF3C7)
    val Amber200 = Color(0xFFFDE68A)
    val Amber500 = Color(0xFFF59E0B)
    val Amber600 = Color(0xFFD97706)
    val Amber700 = Color(0xFFB45309)
    val Amber800 = Color(0xFF92400E)
    val Amber900 = Color(0xFF78350F)

    // ──────────────────────────────────────────────
    // Emerald (success, "easy" difficulty, onboarding accent #3)
    // ──────────────────────────────────────────────
    val Emerald50  = Color(0xFFECFDF5)
    val Emerald100 = Color(0xFFD1FAE5)
    val Emerald200 = Color(0xFFA7F3D0)
    val Emerald500 = Color(0xFF10B981)
    val Emerald600 = Color(0xFF059669)
    val Emerald700 = Color(0xFF047857)
    val Emerald900 = Color(0xFF064E3B)

    // ──────────────────────────────────────────────
    // Blue ("road" surface type, links)
    // ──────────────────────────────────────────────
    val Blue50  = Color(0xFFEFF6FF)
    val Blue200 = Color(0xFFBFDBFE)
    val Blue500 = Color(0xFF3B82F6)
    val Blue600 = Color(0xFF2563EB)
    val Blue700 = Color(0xFF1D4ED8)
    val Blue800 = Color(0xFF1E40AF)

    // ──────────────────────────────────────────────
    // Indigo ("mixed" surface type)
    // ──────────────────────────────────────────────
    val Indigo50  = Color(0xFFEEF2FF)
    val Indigo200 = Color(0xFFC7D2FE)
    val Indigo500 = Color(0xFF6366F1)
    val Indigo700 = Color(0xFF4338CA)

    // ──────────────────────────────────────────────
    // Purple / Violet ("gravel" surface type)
    // ──────────────────────────────────────────────
    val Purple50  = Color(0xFFFAF5FF)
    val Purple200 = Color(0xFFE9D5FF)
    val Purple700 = Color(0xFF7E22CE)
    val Violet500 = Color(0xFF8B5CF6) // == --chip-gravel
}

/**
 * Semantic aliases for the Tracks domain (chips & map pins).
 * Matches --chip-* tokens from theme.css.
 */
object DifficultyColors {
    val Easy   = AppColors.Emerald500   // #10B981
    val Medium = AppColors.Amber500     // #F59E0B
    val Hard   = AppColors.Red500       // #EF4444
}

object SurfaceTypeColors {
    val Road   = AppColors.Blue500      // #3B82F6
    val Gravel = AppColors.Violet500    // #8B5CF6
    val Mixed  = AppColors.Indigo500    // #6366F1
}

/**
 * Chip "soft" variants used on cards (bg-*-50 + text-*-700 + border-*-200).
 */
data class ChipPalette(val background: Color, val text: Color, val border: Color)

object ChipColors {
    val Easy   = ChipPalette(AppColors.Emerald50, AppColors.Emerald700, AppColors.Emerald200)
    val Medium = ChipPalette(AppColors.Amber50,   AppColors.Amber700,   AppColors.Amber200)
    val Hard   = ChipPalette(AppColors.Red50,     AppColors.Red700,     AppColors.Red200)
    val Road   = ChipPalette(AppColors.Blue50,    AppColors.Blue700,    AppColors.Blue200)
    val Gravel = ChipPalette(AppColors.Purple50,  AppColors.Purple700,  AppColors.Purple200)
    val Mixed  = ChipPalette(AppColors.Indigo50,  AppColors.Indigo700,  AppColors.Indigo200)

    val EventUpcoming = ChipPalette(AppColors.Emerald50, AppColors.Emerald700, AppColors.Emerald200)
    val EventCanceled = ChipPalette(AppColors.Red50,     AppColors.Red700,     AppColors.Red200)
}
