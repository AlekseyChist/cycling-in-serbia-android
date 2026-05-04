package com.cyclinginserbia.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.gestures.detectTapGestures
import com.cyclinginserbia.app.ui.theme.AppPalette

@Composable
fun SearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search",
) {
    val interactionSource = remember { MutableInteractionSource() }
    val focused by interactionSource.collectIsFocusedAsState()

    val borderColor by animateColorAsState(
        targetValue = if (focused) AppPalette.Primary else AppPalette.Gray200,
        animationSpec = tween(durationMillis = 150),
        label = "search-border-color",
    )
    val ringColor by animateColorAsState(
        targetValue = if (focused) AppPalette.Primary.copy(alpha = 0.2f)
        else AppPalette.Primary.copy(alpha = 0f),
        animationSpec = tween(durationMillis = 150),
        label = "search-ring-color",
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .border(2.dp, ringColor, RoundedCornerShape(18.dp))
            .padding(2.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(AppPalette.Gray50)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .height(44.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
        ) {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = null,
                tint = AppPalette.Gray400,
                modifier = Modifier.size(20.dp),
            )
            Box(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .weight(1f),
                contentAlignment = Alignment.CenterStart,
            ) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = TextStyle(
                            color = AppPalette.Gray400,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                        ),
                    )
                }
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    interactionSource = interactionSource,
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(
                        color = AppPalette.Gray900,
                        fontSize = 14.sp,
                    ),
                    cursorBrush = androidx.compose.ui.graphics.SolidColor(AppPalette.Primary),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            if (value.isNotEmpty()) {
                Icon(
                    imageVector = Icons.Outlined.Close,
                    contentDescription = "Clear search",
                    tint = AppPalette.Gray400,
                    modifier = Modifier
                        .size(20.dp)
                        .pointerInput(Unit) {
                            detectTapGestures(onTap = { onValueChange("") })
                        },
                )
            }
        }
    }
}
