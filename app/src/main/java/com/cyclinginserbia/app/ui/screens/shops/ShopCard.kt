package com.cyclinginserbia.app.ui.screens.shops

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cyclinginserbia.app.data.model.Shop
import com.cyclinginserbia.app.ui.theme.AppPalette
import com.cyclinginserbia.app.ui.util.scaleOnPress

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ShopCard(
    shop: Shop,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val interactionSource = remember { MutableInteractionSource() }

    Surface(
        onClick = { handleShopClick(context, shop.link) },
        shape = RoundedCornerShape(16.dp),
        color = AppPalette.White,
        border = BorderStroke(1.dp, AppPalette.Gray200),
        interactionSource = interactionSource,
        modifier = modifier
            .fillMaxWidth()
            .scaleOnPress(interactionSource),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            ShopLogo(shop)

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = shop.name,
                    style = TextStyle(
                        color = AppPalette.Gray900,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                    ),
                )
                Text(
                    text = shop.category,
                    style = TextStyle(
                        color = AppPalette.Gray500,
                        fontSize = 12.sp,
                    ),
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = shop.description,
                    style = TextStyle(
                        color = AppPalette.Gray600,
                        fontSize = 14.sp,
                    ),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(8.dp))
                ShopFooter(shop)
            }
        }
    }
}

@Composable
private fun ShopLogo(shop: Shop) {
    val shape: Shape = if (shop.isPersonal) CircleShape else RoundedCornerShape(12.dp)
    Box(
        modifier = Modifier
            .size(64.dp)
            .clip(shape)
            .background(AppPalette.Gray50),
        contentAlignment = Alignment.Center,
    ) {
        if (shop.logoRes != null) {
            Image(
                painter = painterResource(id = shop.logoRes),
                contentDescription = "${shop.name} logo",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(shape),
            )
        } else {
            Icon(
                imageVector = Icons.Outlined.Storefront,
                contentDescription = null,
                tint = AppPalette.Gray400,
                modifier = Modifier.size(32.dp),
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ShopFooter(shop: Shop) {
    val isPhone = shop.link.startsWith("tel:")
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        if (shop.location != null) {
            Text(
                text = "${shop.location} ·",
                style = TextStyle(
                    color = AppPalette.Gray400,
                    fontSize = 12.sp,
                ),
                softWrap = false,
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Icon(
                imageVector = if (isPhone) Icons.Outlined.Phone
                else Icons.AutoMirrored.Outlined.OpenInNew,
                contentDescription = null,
                tint = AppPalette.Gray400,
                modifier = Modifier.size(12.dp),
            )
            Text(
                text = shop.linkLabel,
                style = TextStyle(
                    color = AppPalette.Gray400,
                    fontSize = 12.sp,
                ),
                softWrap = false,
            )
        }
    }
}

private fun handleShopClick(context: Context, link: String) {
    val intent = if (link.startsWith("tel:")) {
        Intent(Intent.ACTION_DIAL, Uri.parse(link))
    } else {
        Intent(Intent.ACTION_VIEW, Uri.parse(link))
    }
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
}
