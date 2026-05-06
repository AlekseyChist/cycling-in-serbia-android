package com.cyclinginserbia.app.ui.screens.onboarding

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
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.PedalBike
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cyclinginserbia.app.ui.theme.AppColors

@Composable
fun OnboardingScreen(
    onFinished: () -> Unit,
    @Suppress("UNUSED_PARAMETER") viewModel: OnboardingViewModel = hiltViewModel(),
) {
    val gradient = Brush.linearGradient(
        colors = listOf(AppColors.Cream50, AppColors.Cream100),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(horizontal = 24.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center,
        ) {
            HeroBlock()
        }

        BottomBlock(onGetStarted = onFinished)
        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun HeroBlock() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        AppLogoBox()

        Spacer(Modifier.height(20.dp))
        Text(
            text = "Cycling in Serbia",
            style = TextStyle(
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.Foreground,
            ),
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(8.dp))
        Text(
            text = "Discover the best cycling tracks, connect with local shops, and join exciting events.",
            style = TextStyle(
                fontSize = 14.sp,
                color = AppColors.Gray500,
                lineHeight = 21.sp,
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(max = 280.dp),
        )

        Spacer(Modifier.height(36.dp))
        FeatureList()
    }
}

@Composable
private fun AppLogoBox() {
    Box(
        modifier = Modifier
            .size(88.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(AppColors.Primary),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "DBB",
            style = TextStyle(
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.PrimaryForeground,
            ),
        )
    }
}

@Composable
private fun FeatureList() {
    Column(
        modifier = Modifier.widthIn(max = 300.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        FeatureRow(
            icon = Icons.Outlined.LocationOn,
            iconBg = AppColors.OrangeTint,
            iconTint = AppColors.Primary,
            title = "Explore Tracks",
            subtitle = "Find popular cycling routes and events",
        )
        FeatureRow(
            icon = Icons.Outlined.PedalBike,
            iconBg = AppColors.OrangeTint,
            iconTint = AppColors.Primary,
            title = "Local Shops",
            subtitle = "Connect with bike shops and service places",
        )
        FeatureRow(
            icon = Icons.Outlined.CalendarMonth,
            iconBg = AppColors.Emerald50,
            iconTint = AppColors.Emerald500,
            title = "Join Events",
            subtitle = "Stay informed about group rides and cycling events",
        )
    }
}

@Composable
private fun FeatureRow(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    subtitle: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(iconBg),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp),
            )
        }

        Spacer(Modifier.width(12.dp))

        Column {
            Text(
                text = title,
                style = TextStyle(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.Foreground,
                ),
            )
            Text(
                text = subtitle,
                style = TextStyle(
                    fontSize = 13.sp,
                    color = AppColors.Gray500,
                ),
            )
        }
    }
}

@Composable
private fun BottomBlock(onGetStarted: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Button(
            onClick = onGetStarted,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AppColors.Primary,
                contentColor = AppColors.PrimaryForeground,
            ),
        ) {
            Text(
                text = "Get Started",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                ),
            )
        }

        Spacer(Modifier.height(12.dp))

        Text(
            text = "By continuing, you agree to follow safe cycling practices",
            style = TextStyle(
                fontSize = 12.sp,
                color = AppColors.Gray400,
            ),
            textAlign = TextAlign.Center,
        )
    }
}
