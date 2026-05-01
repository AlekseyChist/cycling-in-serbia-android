package com.cyclinginserbia.app.ui.screens.shops

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Handshake
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cyclinginserbia.app.data.model.Shop
import com.cyclinginserbia.app.data.model.ShopCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopsScreen(
    viewModel: ShopsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Shops") }) },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center,
        ) {
            when (val s = state) {
                is ShopsUiState.Loading -> CircularProgressIndicator()
                is ShopsUiState.Error -> ErrorView(s.message, onRetry = viewModel::load)
                is ShopsUiState.Ready -> ShopsList(shops = s.shops)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ShopsList(shops: List<Shop>) {
    val context = LocalContext.current
    val grouped = shops.groupBy { it.category }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        ShopCategory.entries.forEach { category ->
            val itemsInCategory = grouped[category].orEmpty()
            if (itemsInCategory.isEmpty()) return@forEach
            stickyHeader(key = "header-${category.name}") {
                CategoryHeader(category.title)
            }
            items(items = itemsInCategory, key = { it.id }) { shop ->
                ShopCard(shop = shop, onClick = { openLink(context, shop.link) })
            }
        }
    }
}

@Composable
private fun CategoryHeader(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(vertical = 8.dp),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun ShopCard(shop: Shop, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            ShopLogo(shop)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = shop.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = shop.type,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = shop.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(8.dp))
                LocationChip(shop.location)
            }
        }
    }
}

@Composable
private fun ShopLogo(shop: Shop) {
    Box(
        modifier = Modifier
            .size(64.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
        if (shop.logoResId != null) {
            Image(
                painter = painterResource(id = shop.logoResId),
                contentDescription = "${shop.name} logo",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(6.dp),
            )
        } else {
            Icon(
                imageVector = categoryIcon(shop.category),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(28.dp),
            )
        }
    }
}

@Composable
private fun LocationChip(location: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Outlined.LocationOn,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(14.dp),
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = location,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun ErrorView(message: String, onRetry: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Couldn't load shops", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(4.dp))
        Text(
            message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(16.dp))
        Button(onClick = onRetry) { Text("Retry") }
    }
}

private fun categoryIcon(category: ShopCategory): ImageVector = when (category) {
    ShopCategory.sellingPlatform -> Icons.Outlined.ShoppingCart
    ShopCategory.shop -> Icons.Outlined.Storefront
    ShopCategory.service -> Icons.Outlined.Build
    ShopCategory.friend -> Icons.Outlined.Handshake
}

private fun openLink(context: Context, link: String) {
    val tel = link.startsWith("tel:") || link.startsWith("+")
    val uri = if (link.startsWith("+")) Uri.parse("tel:$link") else Uri.parse(link)
    val action = if (tel) Intent.ACTION_DIAL else Intent.ACTION_VIEW
    context.startActivity(Intent(action, uri))
}
