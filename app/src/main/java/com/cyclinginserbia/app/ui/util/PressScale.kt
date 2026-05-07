package com.cyclinginserbia.app.ui.util

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer

fun Modifier.scaleOnPress(
    interactionSource: InteractionSource? = null,
    pressedScale: Float = 0.98f,
): Modifier = composed {
    val source = interactionSource as? MutableInteractionSource
        ?: remember { MutableInteractionSource() }
    val pressed by source.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) pressedScale else 1f,
        label = "press-scale",
    )
    val mod = Modifier.graphicsLayer { scaleX = scale; scaleY = scale }
    if (interactionSource == null) {
        this.then(mod).indication(source, null)
    } else {
        this.then(mod)
    }
}
