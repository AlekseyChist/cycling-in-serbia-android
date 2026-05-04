package com.cyclinginserbia.app.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColors = lightColorScheme(
    primary             = AppColors.Primary,
    onPrimary           = AppColors.PrimaryForeground,
    secondary           = AppColors.Secondary,
    onSecondary         = AppColors.SecondaryForeground,
    tertiary            = AppColors.Accent,
    onTertiary          = AppColors.AccentForeground,
    background          = AppColors.Background,
    onBackground        = AppColors.Foreground,
    surface             = AppColors.Card,
    onSurface           = AppColors.CardForeground,
    surfaceVariant      = AppColors.Muted,
    onSurfaceVariant    = AppColors.MutedForeground,
    error               = AppColors.Destructive,
    onError             = AppColors.DestructiveForeground,
    outline             = AppColors.Border,
    outlineVariant      = AppColors.Gray200,
)

@Composable
fun CyclingInSerbiaTheme(
    content: @Composable () -> Unit,
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    MaterialTheme(
        colorScheme = LightColors,
        typography  = AppTypography,
        shapes      = AppShapes,
        content     = content,
    )
}
