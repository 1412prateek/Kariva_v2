package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.Product
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    products: List<Product>,
    wishlist: Set<String>,
    cartCount: Int,
    searchQuery: String,
    selectedCategory: String,
    onSearchChange: (String) -> Unit,
    onCategorySelect: (String) -> Unit,
    onProductClick: (Product) -> Unit,
    onWishlistToggle: (Product) -> Unit,
    onCartClick: () -> Unit,
    onWishlistClick: () -> Unit,
    onSeeAllCategories: () -> Unit,
    onCreatorPortalClick: () -> Unit
) {
    val categories = listOf(
        "All",
        "Earpods Covers",
        "Phone Covers",
        "Keychains & Charms",
        "Hair & Appliqués",
        "Woolen Items"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KarivaCreamBg)
            .statusBarsPadding()
            .testTag("home_screen")
    ) {
        // Luxury Top Header
        KarivaTopHeader(
            title = "Kariva",
            subtitle = "HANDCRAFTED KURUS",
            cartItemCount = cartCount,
            onCartClick = onCartClick,
            onWishlistClick = onWishlistClick,
            wishlistCount = wishlist.size,
            onMenuClick = onCreatorPortalClick
        )

        // Main Scrollable Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 90.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Search Bar
            item(span = { GridItemSpan(2) }) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = searchQuery,
                        onValueChange = onSearchChange,
                        placeholder = {
                            Text(
                                "Search kurus covers, charms, woolens...",
                                color = KarivaTextMuted,
                                fontSize = 13.5.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Search,
                                contentDescription = "Search",
                                tint = KarivaTextMuted
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { onSearchChange("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear", tint = KarivaTextMuted)
                                }
                            } else {
                                Icon(
                                    imageVector = Icons.Outlined.Tune,
                                    contentDescription = "Filter",
                                    tint = KarivaCharcoal,
                                    modifier = Modifier.padding(end = 4.dp)
                                )
                            }
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, KarivaBorder, RoundedCornerShape(24.dp))
                            .shadow(2.dp, RoundedCornerShape(24.dp))
                            .testTag("home_search_bar")
                    )
                }
            }

            // Promotional Hero Banner with Kurus Handcraft
            item(span = { GridItemSpan(2) }) {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = KarivaCharcoal),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clickable { onCategorySelect("Phone Covers") }
                        .testTag("home_hero_banner")
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Image(
                            painter = painterResource(id = R.drawable.crochet_phone_sleeve),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(200.dp)
                                .align(Alignment.CenterEnd)
                        )

                        // Vignette gradient
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            KarivaCharcoal,
                                            KarivaCharcoal.copy(alpha = 0.95f),
                                            KarivaCharcoal.copy(alpha = 0.55f),
                                            Color.Transparent
                                        ),
                                        endX = 650f
                                    )
                                )
                        )

                        // Banner Text
                        Column(
                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = KarivaTerracottaContainer.copy(alpha = 0.35f)
                            ) {
                                Text(
                                    text = "HANDCRAFTED BATCH",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = KarivaTerracottaLight,
                                    letterSpacing = 1.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Cozy Protection\nFor Your Tech",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 20.sp,
                                    lineHeight = 24.sp
                                ),
                                color = Color.White
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = Color.White,
                                modifier = Modifier.clickable { onCategorySelect("Phone Covers") }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Shop Kurus",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = KarivaCharcoal
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.ArrowForward,
                                        contentDescription = null,
                                        tint = KarivaCharcoal,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Categories horizontal selector
            item(span = { GridItemSpan(2) }) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    categories.forEach { cat ->
                        val isSelected = selectedCategory.equals(cat, ignoreCase = true)
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) KarivaCharcoal else Color.White,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) KarivaCharcoal else KarivaBorder
                            ),
                            modifier = Modifier
                                .clickable { onCategorySelect(cat) }
                                .testTag("cat_pill_$cat")
                        ) {
                            Text(
                                text = cat,
                                fontSize = 12.5.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (isSelected) Color.White else KarivaTextPrimary,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            // Section Title Row
            item(span = { GridItemSpan(2) }) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (selectedCategory == "All") "Featured Handcrafts" else "$selectedCategory",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp
                        ),
                        color = KarivaCharcoal
                    )

                    Text(
                        text = "See all",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = KarivaTerracotta,
                        modifier = Modifier
                            .clickable(onClick = onSeeAllCategories)
                            .testTag("see_all_categories_link")
                    )
                }
            }

            // Empty state
            if (products.isEmpty()) {
                item(span = { GridItemSpan(2) }) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Outlined.SearchOff,
                                contentDescription = null,
                                tint = KarivaTextMuted,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                "No handcrafted kurus items found",
                                color = KarivaTextSecondary,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            // 2-Column Product Cards
            items(products, key = { it.id }) { product ->
                KarivaProductCard(
                    product = product,
                    isWishlisted = wishlist.contains(product.id),
                    onProductClick = { onProductClick(product) },
                    onWishlistToggle = { onWishlistToggle(product) }
                )
            }
        }
    }
}
