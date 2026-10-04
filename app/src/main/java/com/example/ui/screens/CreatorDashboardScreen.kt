package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.model.*
import com.example.ui.components.KarivaButton
import com.example.ui.components.formatCurrency
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatorDashboardScreen(
    analytics: CreatorAnalytics,
    products: List<Product>,
    orders: List<Order>,
    onAddProduct: () -> Unit,
    onEditProduct: (Product) -> Unit,
    onDeleteProduct: (String) -> Unit,
    onUpdateStock: (String, Int) -> Unit,
    onUpdateOrderStatus: (String, OrderStatus) -> Unit,
    onSwitchToShopperView: () -> Unit,
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Inventory, 1: Orders, 2: Analytics

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(KarivaCreamBg)
            .statusBarsPadding()
            .testTag("creator_dashboard_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 20.dp)
        ) {
            // Creator Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(KarivaGoldDark),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Storefront,
                            contentDescription = "Creator",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Shikha",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = KarivaCharcoal
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = KarivaGoldContainer
                            ) {
                                Text(
                                    text = "CREATOR",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = KarivaGoldDark,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "shikha@kariva.com",
                            fontSize = 11.5.sp,
                            color = KarivaTextMuted
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Preview Storefront Button
                    OutlinedButton(
                        onClick = onSwitchToShopperView,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = KarivaCharcoal),
                        border = androidx.compose.foundation.BorderStroke(1.dp, KarivaBorder),
                        modifier = Modifier.height(38.dp).testTag("creator_preview_store_btn")
                    ) {
                        Icon(Icons.Outlined.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Store", fontSize = 12.sp)
                    }

                    IconButton(
                        onClick = onLogout,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(1.dp, KarivaBorder, CircleShape)
                    ) {
                        Icon(Icons.Outlined.Logout, contentDescription = "Logout", tint = KarivaAccentRed, modifier = Modifier.size(18.dp))
                    }
                }
            }

            // High-Level KPIs Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                KPICard(
                    title = "Revenue",
                    value = formatCurrency(analytics.totalRevenue),
                    modifier = Modifier.weight(1f)
                )
                KPICard(
                    title = "Orders",
                    value = "${analytics.totalOrders}",
                    modifier = Modifier.weight(0.7f)
                )
                KPICard(
                    title = "Items",
                    value = "${analytics.activeListings}",
                    modifier = Modifier.weight(0.7f)
                )
                KPICard(
                    title = "Low Stock",
                    value = "${analytics.lowStockCount}",
                    highlight = analytics.lowStockCount > 0,
                    modifier = Modifier.weight(0.8f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Navigation Tabs
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(18.dp),
                color = KarivaSurfaceCard
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    listOf("Inventory & Catalog", "Fulfillment", "Analytics").forEachIndexed { index, title ->
                        val isSelected = selectedTab == index
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(4.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSelected) KarivaCharcoal else Color.Transparent)
                                .clickable { selectedTab = index }
                                .padding(vertical = 10.dp)
                                .testTag("creator_tab_$index"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = title,
                                fontSize = 11.5.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (isSelected) Color.White else KarivaTextSecondary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Main Tab Content
            when (selectedTab) {
                0 -> {
                    // Inventory Tab
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Catalog (${products.size} pieces)",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold
                            ),
                            color = KarivaCharcoal
                        )

                        Button(
                            onClick = onAddProduct,
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = KarivaGold),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.height(36.dp).testTag("creator_add_product_btn")
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("New Piece", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 40.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(products, key = { it.id }) { product ->
                            CreatorProductItem(
                                product = product,
                                onEdit = { onEditProduct(product) },
                                onDelete = { onDeleteProduct(product.id) },
                                onStockDelta = { delta ->
                                    onUpdateStock(product.id, (product.stock + delta).coerceAtLeast(0))
                                }
                            )
                        }
                    }
                }

                1 -> {
                    // Orders & Automated Fulfillment Tab
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 40.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text(
                                text = "Active Orders & Automated Fulfillment",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = KarivaCharcoal,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
                            )
                        }

                        items(orders, key = { it.id }) { order ->
                            CreatorOrderItem(
                                order = order,
                                onAdvanceStatus = { nextStatus ->
                                    onUpdateOrderStatus(order.id, nextStatus)
                                }
                            )
                        }
                    }
                }

                2 -> {
                    // Analytics Tab
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Sales & Inventory Analytics",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold
                            ),
                            color = KarivaCharcoal
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = Color.White,
                            border = androidx.compose.foundation.BorderStroke(1.dp, KarivaBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Category Breakdown", fontWeight = FontWeight.SemiBold, fontSize = 13.5.sp)
                                Spacer(modifier = Modifier.height(12.dp))

                                analytics.categoryBreakdown.forEach { (cat, count) ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(cat, fontSize = 13.sp, color = KarivaCharcoal)
                                        Text("$count active listings", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = KarivaGoldDark)
                                    }
                                    LinearProgressIndicator(
                                        progress = { (count.toFloat() / (analytics.activeListings.coerceAtLeast(1))) },
                                        color = KarivaGold,
                                        trackColor = KarivaSurfaceCard,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(6.dp)
                                            .clip(RoundedCornerShape(3.dp))
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = KarivaGoldContainer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("💎 Creator Insight", fontWeight = FontWeight.Bold, color = KarivaGoldDark, fontSize = 13.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Your top performing pieces are in Necklaces and Rings. Consider restocking the Golden Solitaire Ring as only 3 pieces remain.",
                                    fontSize = 12.sp,
                                    color = KarivaCharcoal
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun KPICard(
    title: String,
    value: String,
    highlight: Boolean = false,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (highlight) KarivaWarningContainer else Color.White,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (highlight) KarivaWarning else KarivaBorder
        ),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, fontSize = 10.sp, color = KarivaTextMuted)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Bold,
                color = if (highlight) KarivaWarning else KarivaCharcoal,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun CreatorProductItem(
    product: Product,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onStockDelta: (Int) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, KarivaBorder),
        modifier = Modifier.fillMaxWidth().testTag("creator_item_${product.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(KarivaSurfaceCard)
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
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.5.sp,
                    color = KarivaCharcoal,
                    maxLines = 1
                )
                Text(
                    text = "${product.category} • ${formatCurrency(product.price)}",
                    fontSize = 11.5.sp,
                    color = KarivaTextMuted
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Stock management buttons (- / +)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Stock: ",
                        fontSize = 11.sp,
                        color = KarivaTextSecondary
                    )

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (product.isLowStock) KarivaWarningContainer else KarivaSurfaceCard
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { onStockDelta(-1) }, modifier = Modifier.size(24.dp)) {
                                Text("-", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                            Text(
                                text = "${product.stock}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (product.isLowStock) KarivaWarning else KarivaCharcoal,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                            IconButton(onClick = { onStockDelta(1) }, modifier = Modifier.size(24.dp)) {
                                Text("+", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Edit & Delete actions
            Row {
                IconButton(onClick = onEdit, modifier = Modifier.size(34.dp)) {
                    Icon(Icons.Outlined.Edit, contentDescription = "Edit", tint = KarivaCharcoal, modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(34.dp)) {
                    Icon(Icons.Outlined.Delete, contentDescription = "Delete", tint = KarivaAccentRed, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
private fun CreatorOrderItem(
    order: Order,
    onAdvanceStatus: (OrderStatus) -> Unit
) {
    val nextStatus = when (order.status) {
        OrderStatus.ORDER_PLACED -> OrderStatus.PROCESSING
        OrderStatus.PROCESSING -> OrderStatus.SHIPPED
        OrderStatus.SHIPPED -> OrderStatus.OUT_FOR_DELIVERY
        OrderStatus.OUT_FOR_DELIVERY -> OrderStatus.DELIVERED
        OrderStatus.DELIVERED -> null
        OrderStatus.CANCELLED -> null
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, KarivaBorder),
        modifier = Modifier.fillMaxWidth().testTag("creator_order_${order.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(order.id, fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = KarivaCharcoal)
                    Text("Customer: ${order.userEmail}", fontSize = 11.5.sp, color = KarivaTextMuted)
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = KarivaGoldContainer
                ) {
                    Text(
                        text = order.status.label,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = KarivaGoldDark,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${order.items.size} item(s) • Total: ${formatCurrency(order.total)}",
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Medium,
                color = KarivaCharcoal
            )

            if (nextStatus != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = { onAdvanceStatus(nextStatus) },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = KarivaCharcoal),
                    modifier = Modifier.fillMaxWidth().height(38.dp)
                ) {
                    Text("Advance to: ${nextStatus.label}", fontSize = 12.sp)
                }
            }
        }
    }
}
