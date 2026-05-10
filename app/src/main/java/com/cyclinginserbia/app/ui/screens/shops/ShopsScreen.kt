package com.cyclinginserbia.app.ui.screens.shops

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cyclinginserbia.app.data.model.ShopTab
import com.cyclinginserbia.app.ui.components.EmptyState
import com.cyclinginserbia.app.ui.components.SearchField
import com.cyclinginserbia.app.ui.theme.AppColors

@Composable
fun ShopsScreen(
    viewModel: ShopsViewModel = hiltViewModel(),
) {
    val query by viewModel.query.collectAsStateWithLifecycle()
    val tab by viewModel.tab.collectAsStateWithLifecycle()
    val shops by viewModel.filtered.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background),
    ) {
        StickyHeader(
            tab = tab,
            onTabChange = viewModel::onTabChange,
            query = query,
            onQueryChange = viewModel::onQueryChange,
        )

        if (shops.isEmpty()) {
            EmptyState(
                icon = Icons.Outlined.Storefront,
                title = "No results found",
                description = "Try adjusting your search or filter",
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(items = shops, key = { it.id }) { shop ->
                    ShopCard(shop = shop)
                }
            }
        }
    }
}

@Composable
private fun StickyHeader(
    tab: ShopTab,
    onTabChange: (ShopTab) -> Unit,
    query: String,
    onQueryChange: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppColors.Background)
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        SegmentedTabs(selected = tab, onSelect = onTabChange)
        Spacer(Modifier.height(8.dp))
        SearchField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = "Search shops and services",
        )
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(AppColors.Gray200),
    )
}

@Composable
private fun SegmentedTabs(
    selected: ShopTab,
    onSelect: (ShopTab) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AppColors.Gray100)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(0.dp),
    ) {
        ShopTab.entries.forEach { entry ->
            SegmentedTab(
                tab = entry,
                isSelected = entry == selected,
                onClick = { onSelect(entry) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun SegmentedTab(
    tab: ShopTab,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val background by animateColorAsState(
        targetValue = if (isSelected) AppColors.Background else AppColors.Background.copy(alpha = 0f),
        animationSpec = tween(150),
        label = "tab-bg",
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected) AppColors.Gray900 else AppColors.Gray600,
        animationSpec = tween(150),
        label = "tab-text",
    )
    val elevation = if (isSelected) 1.dp else 0.dp
    val interactionSource = remember { MutableInteractionSource() }

    Surface(
        modifier = modifier.height(32.dp),
        shape = RoundedCornerShape(12.dp),
        color = background,
        shadowElevation = elevation,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = tab.label,
                style = TextStyle(
                    color = textColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                ),
                textAlign = TextAlign.Center,
            )
        }
    }
}
