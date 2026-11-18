package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.BraversCatalog
import com.example.model.BraversCategory
import com.example.model.Product
import com.example.model.ThemePreference
import com.example.ui.BraversUiState
import com.example.ui.components.BraversPromoBanner
import com.example.ui.components.ProductCard

@Composable
fun HomeScreen(
    uiState: BraversUiState,
    onCategoryClick: (BraversCategory) -> Unit,
    onProductClick: (Product) -> Unit,
    onToggleWishlist: (String) -> Unit,
    onOpenShop: () -> Unit,
    onOpenLiveWebsite: () -> Unit,
    onApplyOff20: () -> Unit,
    onCycleTheme: () -> Unit,
    modifier: Modifier = Modifier
) {
    val featuredProducts = uiState.allProducts.filter { it.isFeatured }
    val categories = listOf(
        BraversCategory.MENS,
        BraversCategory.WOMENS,
        BraversCategory.TEES_BOTTOM,
        BraversCategory.KURTI,
        BraversCategory.TOP_TANK
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen"),
        contentPadding = PaddingValues(bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Brand Editorial Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_bravers_logo),
                        contentDescription = "Bravers Logo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                    )
                    Column {
                        Text(
                            text = "BRAVERS",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "bravers.in • STREETWEAR & ETHNIC",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = onOpenShop,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("home_search_button")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = "Search Bravers catalog"
                        )
                    }
                    IconButton(
                        onClick = onCycleTheme,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("home_theme_toggle")
                    ) {
                        Icon(
                            imageVector = if (uiState.themePreference == ThemePreference.DARK) {
                                Icons.Outlined.LightMode
                            } else {
                                Icons.Outlined.DarkMode
                            },
                            contentDescription = "Switch color theme"
                        )
                    }
                }
            }
        }

        // Hero Lookbook Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .aspectRatio(16f / 10f)
                    .clip(MaterialTheme.shapes.large)
                    .clickable { onOpenShop() }
                    .testTag("home_hero_banner")
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_hero_bravers),
                    contentDescription = "Bravers Lookbook Hero",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.15f),
                                    Color.Black.copy(alpha = 0.78f)
                                )
                            )
                        )
                )

                Surface(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(16.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.secondary
                ) {
                    Text(
                        text = "NEW SEASON • INDIA",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(18.dp)
                ) {
                    Text(
                        text = "WEAR YOUR\nCOURAGE.",
                        style = MaterialTheme.typography.displayMedium,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Oversized drop-shoulder tees, kurtis & urban essentials.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.88f)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = onOpenShop,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = Color(0xFF121212)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("hero_explore_button")
                        ) {
                            Text(
                                text = "Explore Drops",
                                style = MaterialTheme.typography.labelLarge
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        OutlinedButton(
                            onClick = onOpenLiveWebsite,
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color.White
                            ),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.75f)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("hero_live_web_button")
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Language,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("bravers.in")
                        }
                    }
                }
            }
        }

        // Promo Code Strip
        item {
            BraversPromoBanner(
                promoApplied = uiState.appliedPromoCode == BraversCatalog.PROMO_CODE_OFF20,
                onApplyOff20 = onApplyOff20,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }

        // Storefront Categories Carousel
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Shop by Category",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    TextButton(
                        onClick = onOpenShop,
                        modifier = Modifier.testTag("view_all_categories_btn")
                    ) {
                        Text("View All")
                    }
                }

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(categories, key = { it.name }) { category ->
                        CategorySpotlightCard(
                            category = category,
                            onClick = { onCategoryClick(category) }
                        )
                    }
                }
            }
        }

        // Featured Drops Adaptive Grid
        item {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Signature Drops",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Curated highlights from bravers.in",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    TextButton(onClick = onOpenShop) {
                        Text("Shop All")
                    }
                }

                BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                    val columns = if (maxWidth >= 600.dp) 3 else 2
                    val rows = featuredProducts.chunked(columns)
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        rows.forEach { rowProducts ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                rowProducts.forEach { product ->
                                    ProductCard(
                                        product = product,
                                        isWishlisted = product.id in uiState.wishlistIds,
                                        onProductClick = onProductClick,
                                        onToggleWishlist = onToggleWishlist,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                repeat(columns - rowProducts.size) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }
        }

        // Brand Story & Live Website Portal Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Verified,
                            contentDescription = "Verified brand",
                            tint = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = "ABOUT BRAVERS INDIA",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    Text(
                        text = "Built by Rahul Sharma for fearless everyday dressing — pairing heavyweight 240 GSM drop-shoulder streetwear with crisp modern Indian kurtis and core essentials.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Button(
                        onClick = onOpenLiveWebsite,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("about_open_website_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Language,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Browse Live Web App (bravers.in)")
                    }
                }
            }
        }
    }
}

@Composable
private fun CategorySpotlightCard(
    category: BraversCategory,
    onClick: () -> Unit
) {
    val thumbRes = when (category) {
        BraversCategory.MENS -> R.drawable.img_product_mens_tee
        BraversCategory.WOMENS, BraversCategory.KURTI -> R.drawable.img_product_kurti
        BraversCategory.TEES_BOTTOM, BraversCategory.TOP_TANK -> R.drawable.img_product_bottoms
        BraversCategory.ALL -> R.drawable.img_hero_bravers
    }

    Card(
        modifier = Modifier
            .width(152.dp)
            .clip(MaterialTheme.shapes.medium)
            .clickable { onClick() }
            .testTag("home_category_${category.name.lowercase()}"),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(96.dp)
            ) {
                Image(
                    painter = painterResource(id = thumbRes),
                    contentDescription = category.label,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = category.label,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = category.shortDesc,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }
    }
}
