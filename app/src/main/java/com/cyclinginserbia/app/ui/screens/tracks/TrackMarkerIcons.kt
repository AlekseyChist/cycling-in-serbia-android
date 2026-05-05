package com.cyclinginserbia.app.ui.screens.tracks

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.TypedValue
import androidx.compose.ui.graphics.toArgb
import com.cyclinginserbia.app.data.model.Difficulty
import com.cyclinginserbia.app.ui.theme.AppColors
import com.cyclinginserbia.app.ui.theme.DifficultyMapColors

internal fun buildClusterMarker(
    context: Context,
    difficulty: Difficulty,
    count: Int,
    isSelected: Boolean,
): Drawable {
    val baseDp = if (isSelected) 36f else 30f
    val borderDp = if (isSelected) 3f else 2f
    val showBadge = count > 1

    val basePx = baseDp.toPx(context)
    val borderPx = borderDp.toPx(context)
    val badgeDiameterPx = 18f.toPx(context)
    val padding = if (showBadge) badgeDiameterPx / 2f else 0f

    val canvasSize = (basePx + padding * 2f).toInt().coerceAtLeast(1)
    val bitmap = Bitmap.createBitmap(canvasSize, canvasSize, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    val cx = padding + basePx / 2f
    val cy = padding + basePx / 2f
    val radius = basePx / 2f

    val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(64, 0, 0, 0)
    }
    canvas.drawCircle(cx, cy + 1.5f.toPx(context), radius, shadowPaint)

    val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
    }
    canvas.drawCircle(cx, cy, radius, borderPaint)

    val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = colorForDifficulty(difficulty)
    }
    canvas.drawCircle(cx, cy, radius - borderPx, fillPaint)

    if (showBadge) {
        val badgeRadius = badgeDiameterPx / 2f
        val badgeCx = padding + basePx - badgeRadius / 2f
        val badgeCy = padding + badgeRadius / 2f
        val badgeBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE }
        canvas.drawCircle(badgeCx, badgeCy, badgeRadius, badgeBorderPaint)
        val badgeFillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = AppColors.Red500.toArgb()
        }
        canvas.drawCircle(badgeCx, badgeCy, badgeRadius - 1.5f.toPx(context), badgeFillPaint)
        val text = count.toString()
        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 11f.toPx(context)
            isFakeBoldText = true
        }
        val textBounds = Rect()
        textPaint.getTextBounds(text, 0, text.length, textBounds)
        canvas.drawText(
            text,
            badgeCx - textBounds.exactCenterX(),
            badgeCy - textBounds.exactCenterY(),
            textPaint,
        )
    }

    return BitmapDrawable(context.resources, bitmap)
}

private fun colorForDifficulty(d: Difficulty): Int = when (d) {
    Difficulty.easy -> DifficultyMapColors.Easy.toArgb()
    Difficulty.medium -> DifficultyMapColors.Medium.toArgb()
    Difficulty.hard -> DifficultyMapColors.Hard.toArgb()
}

private fun Float.toPx(context: Context): Float =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, context.resources.displayMetrics)
