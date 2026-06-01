package com.cyclinginserbia.app.ui.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.annotation.StringRes
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cyclinginserbia.app.BuildConfig
import com.cyclinginserbia.app.R
import com.cyclinginserbia.app.data.local.preferences.AppLanguage
import com.cyclinginserbia.app.data.local.preferences.ThemeMode
import com.cyclinginserbia.app.data.model.Track
import com.cyclinginserbia.app.ui.navigation.RootViewModel
import com.cyclinginserbia.app.ui.screens.tracks.trackThumbnailRes
import com.cyclinginserbia.app.ui.theme.AppColors

@Composable
fun ProfileScreen(
    rootViewModel: RootViewModel,
    onTrackClick: (String) -> Unit,
    profileViewModel: ProfileViewModel = hiltViewModel(),
) {
    val themeMode by rootViewModel.themeMode.collectAsStateWithLifecycle()
    val appLanguage by rootViewModel.appLanguage.collectAsStateWithLifecycle()
    val favorites by profileViewModel.favorites.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background),
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                Text(
                    text = stringResource(R.string.profile_title),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.Foreground,
                )
            }

            item { Spacer(Modifier.height(16.dp)) }

            item {
                AppearanceSection(
                    current = themeMode,
                    onChange = rootViewModel::setThemeMode,
                )
            }

            item { Spacer(Modifier.height(24.dp)) }

            item {
                LanguageSection(
                    current = appLanguage,
                    onChange = rootViewModel::setAppLanguage,
                )
            }

            item { Spacer(Modifier.height(24.dp)) }

            item { SectionHeader(stringResource(R.string.profile_favorites, favorites.size)) }
            item { Spacer(Modifier.height(8.dp)) }

            if (favorites.isEmpty()) {
                item { FavoritesEmptyState() }
            } else {
                items(items = favorites, key = { it.uuid }) { track ->
                    FavoriteTrackRow(track = track, onClick = { onTrackClick(track.legacyId) })
                }
            }

            item { Spacer(Modifier.height(32.dp)) }

            item { AboutFooter() }
        }
    }
}

@Composable
private fun AppearanceSection(current: ThemeMode, onChange: (ThemeMode) -> Unit) {
    SectionHeader(stringResource(R.string.profile_appearance))
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
                Text(stringResource(opt.labelRes))
            }
        }
    }

    Spacer(Modifier.height(8.dp))
    Text(
        text = stringResource(
            when (current) {
                ThemeMode.SYSTEM -> R.string.theme_desc_system
                ThemeMode.LIGHT -> R.string.theme_desc_light
                ThemeMode.DARK -> R.string.theme_desc_dark
            },
        ),
        fontSize = 13.sp,
        color = AppColors.MutedForeground,
    )
}

@Composable
private fun LanguageSection(current: AppLanguage, onChange: (AppLanguage) -> Unit) {
    SectionHeader(stringResource(R.string.profile_language))
    Spacer(Modifier.height(12.dp))

    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
        LanguageOption.entries.forEachIndexed { idx, opt ->
            // Language names are autonyms (English/Русский/Srpski) shown the same
            // in every locale; only "System" is translated.
            val label = opt.autonym ?: stringResource(R.string.language_system)
            SegmentedButton(
                selected = opt.language == current,
                onClick = { onChange(opt.language) },
                shape = SegmentedButtonDefaults.itemShape(idx, LanguageOption.entries.size),
                icon = {},
            ) {
                Text(label, maxLines = 1)
            }
        }
    }

    Spacer(Modifier.height(8.dp))
    Text(
        text = stringResource(R.string.profile_language_caption),
        fontSize = 13.sp,
        color = AppColors.MutedForeground,
    )
}

@Composable
private fun FavoriteTrackRow(track: Track, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.Card),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(trackThumbnailRes(track)),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(AppColors.Gray100),
            )
            Spacer(Modifier.size(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = track.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.Foreground,
                    maxLines = 1,
                )
                Text(
                    text = track.region,
                    fontSize = 12.sp,
                    color = AppColors.Gray500,
                    maxLines = 1,
                )
            }
            Spacer(Modifier.size(8.dp))
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${track.distanceKm} km",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.Foreground,
                )
                Text(
                    text = "${track.elevationM} m",
                    fontSize = 12.sp,
                    color = AppColors.Gray500,
                )
            }
        }
    }
}

@Composable
private fun FavoritesEmptyState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(AppColors.Muted)
            .padding(vertical = 24.dp, horizontal = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.FavoriteBorder,
                contentDescription = null,
                tint = AppColors.Gray500,
                modifier = Modifier.size(20.dp),
            )
            Spacer(Modifier.size(10.dp))
            Text(
                text = stringResource(R.string.profile_favorites_empty),
                fontSize = 13.sp,
                color = AppColors.MutedForeground,
            )
        }
    }
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
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text.uppercase(),
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color = AppColors.Gray500,
    )
}

private enum class ThemeModeOption(
    val mode: ThemeMode,
    @StringRes val labelRes: Int,
    val icon: ImageVector,
) {
    System(ThemeMode.SYSTEM, R.string.theme_system, Icons.Outlined.PhoneAndroid),
    Light(ThemeMode.LIGHT, R.string.theme_light, Icons.Outlined.LightMode),
    Dark(ThemeMode.DARK, R.string.theme_dark, Icons.Outlined.DarkMode),
}

// autonym = the language's own name, shown identically in every locale; null
// for SYSTEM, which is translated via R.string.language_system.
private enum class LanguageOption(val language: AppLanguage, val autonym: String?) {
    System(AppLanguage.SYSTEM, null),
    English(AppLanguage.EN, "English"),
    Russian(AppLanguage.RU, "Русский"),
    Serbian(AppLanguage.SR, "Srpski"),
}
