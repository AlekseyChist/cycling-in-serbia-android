package com.cyclinginserbia.app.ui.screens.tracks

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cyclinginserbia.app.R
import com.cyclinginserbia.app.ui.theme.AppColors
import com.cyclinginserbia.app.ui.theme.DifficultyMapColors

/**
 * Compact legend that explains the difficulty colour code on the Tracks map.
 * Beta testers thought the red/blue/green dots meant ride type
 * (Coffee Ride / Dark on Draft / etc) — they actually mean Easy / Medium /
 * Hard. Collapsed: a small info button. Expanded: three labelled colour
 * swatches.
 */
@Composable
fun MapLegend(modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }

    Surface(
        onClick = { expanded = !expanded },
        modifier = modifier.shadow(elevation = 4.dp, shape = RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        color = AppColors.Background,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = stringResource(
                        if (expanded) R.string.tracks_legend_hide else R.string.tracks_legend_show,
                    ),
                    tint = AppColors.Gray700,
                    modifier = Modifier.size(16.dp),
                )
                Text(
                    text = stringResource(R.string.tracks_difficulty),
                    style = TextStyle(
                        color = AppColors.Gray700,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                    ),
                )
            }
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut(),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    LegendRow(color = DifficultyMapColors.Easy, label = stringResource(R.string.difficulty_easy))
                    LegendRow(color = DifficultyMapColors.Medium, label = stringResource(R.string.difficulty_medium))
                    LegendRow(color = DifficultyMapColors.Hard, label = stringResource(R.string.difficulty_hard))
                }
            }
        }
    }
}

@Composable
private fun LegendRow(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color),
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = label,
            style = TextStyle(
                color = AppColors.Gray700,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
            ),
        )
    }
}
