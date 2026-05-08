package com.cyclinginserbia.app.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cyclinginserbia.app.BuildConfig
import com.cyclinginserbia.app.data.local.preferences.ThemeMode
import com.cyclinginserbia.app.ui.navigation.RootViewModel
import com.cyclinginserbia.app.ui.theme.AppColors

@Composable
fun ProfileScreen(rootViewModel: RootViewModel) {
    val themeMode by rootViewModel.themeMode.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        Text(
            text = "Profile",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = AppColors.Foreground,
        )

        Spacer(Modifier.height(24.dp))

        AppearanceSection(
            current = themeMode,
            onChange = rootViewModel::setThemeMode,
        )

        Spacer(Modifier.weight(1f))

        AboutFooter()
    }
}

@Composable
private fun AppearanceSection(current: ThemeMode, onChange: (ThemeMode) -> Unit) {
    SectionHeader("Appearance")
    Spacer(Modifier.height(12.dp))

    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
        ThemeModeOption.entries.forEachIndexed { idx, opt ->
            SegmentedButton(
                selected = opt.mode == current,
                onClick = { onChange(opt.mode) },
                shape = SegmentedButtonDefaults.itemShape(idx, ThemeModeOption.entries.size),
                icon = {
                    Icon(
                        imageVector = opt.icon,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                },
            ) {
                Text(opt.label)
            }
        }
    }

    Spacer(Modifier.height(8.dp))
    Text(
        text = when (current) {
            ThemeMode.SYSTEM -> "Follows your phone's display setting."
            ThemeMode.LIGHT -> "Always light, regardless of phone setting."
            ThemeMode.DARK -> "Always dark, regardless of phone setting."
        },
        fontSize = 13.sp,
        color = AppColors.MutedForeground,
    )
}

@Composable
private fun AboutFooter() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(AppColors.Primary),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "DBB",
                    color = AppColors.PrimaryForeground,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            Spacer(Modifier.size(8.dp))
            Text(
                text = "Cycling in Serbia",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.Foreground,
            )
        }
        Text(
            text = "v${BuildConfig.VERSION_NAME} · build ${BuildConfig.VERSION_CODE}",
            fontSize = 12.sp,
            color = AppColors.Gray500,
        )
        Text(
            text = "Made with ❤️ by Aleksei Chistiakov",
            fontSize = 11.sp,
            color = AppColors.Gray400,
        )
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun SectionHeader(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .padding(vertical = 4.dp),
    ) {
        Text(
            text = text.uppercase(),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = AppColors.Gray500,
        )
    }
}

private enum class ThemeModeOption(val mode: ThemeMode, val label: String, val icon: ImageVector) {
    System(ThemeMode.SYSTEM, "System", Icons.Outlined.PhoneAndroid),
    Light(ThemeMode.LIGHT, "Light", Icons.Outlined.LightMode),
    Dark(ThemeMode.DARK, "Dark", Icons.Outlined.DarkMode),
}
