package com.cyclinginserbia.app.ui.screens.regulations

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cyclinginserbia.app.data.model.Regulation
import com.cyclinginserbia.app.data.model.RegulationCategory
import com.cyclinginserbia.app.ui.components.SearchField
import com.cyclinginserbia.app.ui.theme.AppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegulationsScreen(
    viewModel: RegulationsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Rules") }) },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(AppColors.Background),
            contentAlignment = Alignment.Center,
        ) {
            when (val s = state) {
                is RegulationsUiState.Loading -> CircularProgressIndicator()
                is RegulationsUiState.Error -> ErrorView(s.message, onRetry = viewModel::load)
                is RegulationsUiState.Ready -> RegulationsContent(
                    state = s,
                    onToggleExpand = viewModel::toggleExpanded,
                    onToggleBookmark = viewModel::toggleBookmark,
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun RegulationsContent(
    state: RegulationsUiState.Ready,
    onToggleExpand: (String) -> Unit,
    onToggleBookmark: (String) -> Unit,
) {
    var query by remember { mutableStateOf("") }

    val filtered by remember(state.categories, query) {
        derivedStateOf { filterCategories(state.categories, query) }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        SearchHeader(query = query, onQueryChange = { query = it })

        if (filtered.isEmpty()) {
            EmptyResults()
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                filtered.forEach { category ->
                    stickyHeader(key = "header-${category.id}") {
                        CategoryHeader(category.title)
                    }
                    items(items = category.items, key = { it.id }) { rule ->
                        RegulationCard(
                            regulation = rule,
                            isExpanded = rule.id in state.expandedIds,
                            isBookmarked = rule.id in state.bookmarkedIds,
                            onToggleExpand = { onToggleExpand(rule.id) },
                            onToggleBookmark = { onToggleBookmark(rule.id) },
                        )
                    }
                }
                item(key = "disclaimer") { DisclaimerFooter() }
            }
        }
    }
}

@Composable
private fun SearchHeader(query: String, onQueryChange: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppColors.Background)
            .padding(16.dp),
    ) {
        SearchField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = "Search rules",
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
private fun CategoryHeader(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppColors.Background)
            .padding(vertical = 8.dp),
    ) {
        Text(
            text = text,
            style = TextStyle(
                color = AppColors.Gray900,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
            ),
        )
    }
}

@Composable
private fun RegulationCard(
    regulation: Regulation,
    isExpanded: Boolean,
    isBookmarked: Boolean,
    onToggleExpand: () -> Unit,
    onToggleBookmark: () -> Unit,
) {
    val rotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        label = "chevron-rotation",
    )

    Surface(
        onClick = onToggleExpand,
        shape = RoundedCornerShape(16.dp),
        color = AppColors.Card,
        border = BorderStroke(1.dp, AppColors.Gray200),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = regulation.title,
                    style = TextStyle(
                        color = AppColors.Gray900,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                    ),
                    modifier = Modifier.weight(1f),
                )
                IconButton(onClick = onToggleBookmark) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Filled.Bookmark
                        else Icons.Outlined.BookmarkBorder,
                        contentDescription = if (isBookmarked) "Remove bookmark" else "Bookmark",
                        tint = if (isBookmarked) AppColors.Primary else AppColors.Gray500,
                    )
                }
                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowDown,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = AppColors.Gray500,
                    modifier = Modifier.rotate(rotation),
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                ExpandedRegulationBody(regulation)
            }
        }
    }
}

@Composable
private fun ExpandedRegulationBody(regulation: Regulation) {
    Column(modifier = Modifier.padding(top = 8.dp)) {
        regulation.content.split("\n\n").forEach { paragraph ->
            Text(
                text = rememberRegulationParagraph(paragraph),
                style = TextStyle(
                    color = AppColors.Gray500,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                ),
                modifier = Modifier.padding(bottom = 8.dp),
            )
        }
        regulation.imageRes?.let { res ->
            Spacer(Modifier.height(4.dp))
            Image(
                painter = painterResource(id = res),
                contentDescription = regulation.title,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp)),
            )
        }
    }
}

@Composable
private fun DisclaimerFooter() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        shape = RoundedCornerShape(16.dp),
        color = AppColors.Cream50,
    ) {
        Text(
            text = "Disclaimer: We organise the weekly rides for free. This is done “as is”, " +
                "so neither DBB nor any associated persons including the ride leaders and other " +
                "riders shall bear any responsibility regarding event organisation.",
            style = TextStyle(
                color = AppColors.Gray500,
                fontSize = 12.sp,
                lineHeight = 18.sp,
            ),
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Composable
private fun EmptyResults() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.MenuBook,
            contentDescription = null,
            tint = AppColors.Gray400,
            modifier = Modifier.size(48.dp),
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = "No results found",
            style = TextStyle(
                color = AppColors.Gray900,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
            ),
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Try adjusting your search query",
            style = TextStyle(color = AppColors.Gray500, fontSize = 14.sp),
        )
    }
}

@Composable
private fun ErrorView(message: String, onRetry: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Couldn't load rules",
            style = TextStyle(
                color = AppColors.Gray900,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
            ),
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = message,
            style = TextStyle(color = AppColors.Gray500, fontSize = 14.sp),
        )
        Spacer(Modifier.height(16.dp))
        Button(onClick = onRetry) { Text("Retry") }
    }
}

private fun filterCategories(
    categories: List<RegulationCategory>,
    query: String,
): List<RegulationCategory> {
    if (query.isBlank()) return categories
    val q = query.trim().lowercase()
    return categories.mapNotNull { category ->
        val matchesCategory = category.title.lowercase().contains(q)
        val matchedItems = category.items.filter { item ->
            matchesCategory ||
                item.title.lowercase().contains(q) ||
                item.content.lowercase().contains(q)
        }
        if (matchedItems.isEmpty()) null else category.copy(items = matchedItems)
    }
}
