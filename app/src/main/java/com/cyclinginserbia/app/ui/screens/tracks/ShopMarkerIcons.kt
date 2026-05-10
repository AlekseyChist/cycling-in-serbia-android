package com.cyclinginserbia.app.ui.screens.tracks

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.TypedValue

/**
 * Round Strava-orange shop pin with a white storefront silhouette inside.
 * Smaller than [buildClusterMarker] so a dense Belgrade neighborhood doesn't
 * become visually heavier than the track markers it overlays.
 *
 * Theme-invariant — same orange reads on both light and dark map tiles.
 * Runs in non-@Composable scope (osmdroid overlay rendering), so the color
 * is hardcoded; theme-aware AppColors aren't reachable here.
 */
internal fun buildShopMarker(context: Context): Drawable {
    val sizeDp = 26f
    val borderDp = 2f
    val sizePx = sizeDp.toPx(context)
    val borderPx = borderDp.toPx(context)

    val canvasSize = sizePx.toInt().coerceAtLeast(1)
    val bitmap = Bitmap.createBitmap(canvasSize, canvasSize, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    val cx = sizePx / 2f
    val cy = sizePx / 2f
    val radius = sizePx / 2f

    val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(64, 0, 0, 0)
    }
    canvas.drawCircle(cx, cy + 1.5f.toPx(context), radius, shadowPaint)

    val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE }
    canvas.drawCircle(cx, cy, radius, borderPaint)

    val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FC5200") // AppColors.Primary (Strava-orange), theme-invariant
    }
    canvas.drawCircle(cx, cy, radius - borderPx, fillPaint)

    drawStorefrontGlyph(canvas, context, cx, cy)

    return BitmapDrawable(context.resources, bitmap)
}

/**
 * Simplified storefront silhouette — a roof trapezoid + body rectangle + door.
 * Hand-drawn rather than rasterising a vector resource because the marker
 * runs in non-@Composable scope and we want one self-contained file.
 */
private fun drawStorefrontGlyph(canvas: Canvas, context: Context, cx: Float, cy: Float) {
    val glyphPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE }
    val w = 12f.toPx(context)
    val h = 12f.toPx(context)
    val left = cx - w / 2f
    val top = cy - h / 2f
    val right = cx + w / 2f
    val bottom = cy + h / 2f

    val roofHeight = h * 0.28f
    val roof = Path().apply {
        moveTo(left, top + roofHeight)
        lineTo(left + w * 0.15f, top)
        lineTo(right - w * 0.15f, top)
        lineTo(right, top + roofHeight)
        close()
    }
    canvas.drawPath(roof, glyphPaint)

    val bodyTop = top + roofHeight + 1f.toPx(context)
    canvas.drawRect(RectF(left + w * 0.08f, bodyTop, right - w * 0.08f, bottom), glyphPaint)

    val cutoutPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FC5200") // punch a door in the body
    }
    val doorWidth = w * 0.28f
    val doorTop = bodyTop + (bottom - bodyTop) * 0.30f
    canvas.drawRect(
        RectF(cx - doorWidth / 2f, doorTop, cx + doorWidth / 2f, bottom),
        cutoutPaint,
    )
}

private fun Float.toPx(context: Context): Float =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, context.resources.displayMetrics)
