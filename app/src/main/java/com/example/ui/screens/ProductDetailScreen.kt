package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.model.PricingTier
import com.example.model.Product
import com.example.ui.components.KarivaButton
import com.example.ui.components.formatCurrency
import com.example.ui.theme.*

@Composable
fun ProductDetailScreen(
    product: Product?,
    isWishlisted: Boolean,
    onBack: () -> Unit,
    onWishlistToggle: () -> Unit,
    onAddToCart: (Product, PricingTier, Int) -> Unit
) {
    if (product == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Product not found")
        }
        return
    }

    val context = LocalContext.current
    var selectedTier by remember(product.id) {
        mutableStateOf(product.pricingTiers.firstOrNull() ?: PricingTier("default", "Standard", product.price))
    }
    var quantity by remember { mutableStateOf(1) }

    val currentPrice = if (selectedTier.price > 0) selectedTier.price else product.price

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(KarivaCreamBg)
            .testTag("product_detail_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 100.dp)
        ) {
            // Top Navigation Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(1.dp, KarivaBorder, CircleShape)
                        .testTag("detail_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = KarivaCharcoal
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(
                        onClick = onWishlistToggle,
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(1.dp, KarivaBorder, CircleShape)
                            .testTag("detail_wishlist_btn")
                    ) {
                        Icon(
                            imageVector = if (isWishlisted) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Wishlist",
                            tint = if (isWishlisted) KarivaAccentRed else KarivaCharcoal
                        )
                    }

                    IconButton(
                        onClick = {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, "Look at this adorable handmade kurus item: ${product.title} on Kariva!")
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share with"))
                        },
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(1.dp, KarivaBorder, CircleShape)
                            .testTag("detail_share_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Share,
                            contentDescription = "Share",
                            tint = KarivaCharcoal
                        )
                    }
                }
            }

            // Hero Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp)
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFFF0E7DC))
            ) {
                if (product.imageRes != null) {
                    Image(
                        painter = painterResource(id = product.imageRes),
                        contentDescription = product.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else if (!product.imageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = product.imageUrl,
                        contentDescription = product.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(Icons.Outlined.Palette, contentDescription = null, tint = KarivaTerracotta, modifier = Modifier.size(64.dp))
                    }
                }

                // Gallery Dots Indicator
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(KarivaCharcoal))
                    Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.6f)))
                    Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.6f)))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Details Container
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Text(
                    text = product.title,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 22.sp
                    ),
                    color = KarivaCharcoal
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Price & Discount row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = formatCurrency(currentPrice),
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        ),
                        color = KarivaCharcoal
                    )

                    if (product.originalPrice != null && product.originalPrice > currentPrice) {
                        Text(
                            text = formatCurrency(product.originalPrice),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                textDecoration = TextDecoration.LineThrough
                            ),
                            color = KarivaTextMuted
                        )

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = KarivaTerracottaContainer
                        ) {
                            Text(
                                text = "SPECIAL OFFER",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = KarivaTerracotta,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Rating & Reviews row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = KarivaMustard,
                        modifier = Modifier.size(17.dp)
                    )
                    Text(
                        text = "${product.rating}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp,
                        color = KarivaCharcoal
                    )
                    Text(
                        text = "(${product.reviewsCount} reviews)",
                        fontSize = 12.5.sp,
                        color = KarivaTextMuted
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    // Stock status
                    if (product.isOutOfStock) {
                        Surface(shape = RoundedCornerShape(12.dp), color = KarivaAccentRed.copy(alpha = 0.15f)) {
                            Text("Out of Stock", color = KarivaAccentRed, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                        }
                    } else if (product.isLowStock) {
                        Surface(shape = RoundedCornerShape(12.dp), color = KarivaWarningContainer) {
                            Text("Only ${product.stock} Left", color = KarivaWarning, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                        }
                    } else {
                        Surface(shape = RoundedCornerShape(12.dp), color = KarivaSuccessContainer) {
                            Text("Ready to Ship (${product.stock})", color = KarivaSuccess, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Handcraft Guarantees row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(KarivaSurfaceCard)
                        .padding(vertical = 12.dp, horizontal = 12.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Outlined.AutoAwesome, contentDescription = null, tint = KarivaCharcoal, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("100% Handcrafted", fontSize = 10.5.sp, color = KarivaTextSecondary)
                    }

                    Divider(modifier = Modifier.height(24.dp).width(1.dp), color = KarivaBorder)

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Outlined.Eco, contentDescription = null, tint = KarivaCharcoal, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("Soft Wool Yarn", fontSize = 10.5.sp, color = KarivaTextSecondary)
                    }

                    Divider(modifier = Modifier.height(24.dp).width(1.dp), color = KarivaBorder)

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Outlined.LocalShipping, contentDescription = null, tint = KarivaCharcoal, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("Safe Padded Ship", fontSize = 10.5.sp, color = KarivaTextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Pricing Tiers selector
                if (product.pricingTiers.isNotEmpty()) {
                    Text(
                        text = "Craft Option & Wool Tier",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = KarivaCharcoal
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        product.pricingTiers.forEach { tier ->
                            val isSelected = selectedTier.id == tier.id
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = if (isSelected) KarivaCharcoal else Color.White,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) KarivaCharcoal else KarivaBorder
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedTier = tier }
                                    .testTag("tier_btn_${tier.id}")
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = tier.name,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                        color = if (isSelected) Color.White else KarivaTextPrimary,
                                        maxLines = 1
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = formatCurrency(tier.price),
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) KarivaTerracottaLight else KarivaTerracotta
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))
                }

                // Description
                Text(
                    text = "Handcrafted Story & Details",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = KarivaCharcoal
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = product.description,
                    style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp),
                    color = KarivaTextSecondary
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Materials
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, KarivaBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.Palette, contentDescription = null, tint = KarivaTerracotta, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Artisan Materials", fontSize = 11.sp, color = KarivaTextMuted)
                            Text(product.materials, fontSize = 12.5.sp, fontWeight = FontWeight.Medium, color = KarivaCharcoal)
                        }
                    }
                }
            }
        }

        // Bottom Action Bar: [ - 1 + ] and [ Add to Cart ]
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .shadow(16.dp, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            color = Color.White
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = KarivaSurfaceCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, KarivaBorder)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        IconButton(
                            onClick = { if (quantity > 1) quantity-- },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Text("-", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = KarivaCharcoal)
                        }

                        Text(
                            text = "$quantity",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                            color = KarivaCharcoal,
                            modifier = Modifier.padding(horizontal = 10.dp)
                        )

                        IconButton(
                            onClick = {
                                if (quantity < product.stock) quantity++
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Text("+", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = KarivaCharcoal)
                        }
                    }
                }

                KarivaButton(
                    text = "Add to Bag",
                    onClick = {
                        onAddToCart(product, selectedTier, quantity)
                    },
                    enabled = !product.isOutOfStock,
                    leadingIcon = Icons.Outlined.ShoppingBag,
                    modifier = Modifier.weight(1f),
                    testTag = "detail_add_to_cart_btn"
                )
            }
        }
    }
}
