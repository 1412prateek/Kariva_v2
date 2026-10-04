package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Order
import com.example.model.OrderStatus
import com.example.ui.components.KarivaButton
import com.example.ui.components.formatCurrency
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackOrderScreen(
    order: Order?,
    onBack: () -> Unit
) {
    if (order == null) {
        Box(
            modifier = Modifier.fillMaxSize().background(KarivaCreamBg),
            contentAlignment = Alignment.Center
        ) {
            Text("No active order selected")
        }
        return
    }

    var showDetailsSheet by remember { mutableStateOf(false) }

    val steps = listOf(
        OrderStatus.ORDER_PLACED to ("Order Placed" to "12 Sep 2026, 10:24 AM"),
        OrderStatus.PROCESSING to ("Processing & Hallmark" to "13 Sep 2026, 02:40 PM"),
        OrderStatus.SHIPPED to ("Shipped via Secure Armored" to "14 Sep 2026, 09:15 AM"),
        OrderStatus.OUT_FOR_DELIVERY to ("Out for Delivery" to "14 Sep 2026, 12:30 PM"),
        OrderStatus.DELIVERED to ("Delivered" to "Estimated Today, 6:00 PM")
    )

    val currentStatusIndex = when (order.status) {
        OrderStatus.ORDER_PLACED -> 0
        OrderStatus.PROCESSING -> 1
        OrderStatus.SHIPPED -> 2
        OrderStatus.OUT_FOR_DELIVERY -> 3
        OrderStatus.DELIVERED -> 4
        OrderStatus.CANCELLED -> -1
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(KarivaCreamBg)
            .statusBarsPadding()
            .testTag("track_order_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .padding(bottom = 90.dp)
        ) {
            // Header matching Screen 8
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(1.dp, KarivaBorder, CircleShape)
                        .testTag("track_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = KarivaCharcoal
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = "Track Order",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = KarivaCharcoal
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tracking info card
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, KarivaBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Tracking Number", fontSize = 11.5.sp, color = KarivaTextMuted)
                            Text(order.trackingNumber, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = KarivaCharcoal)
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = KarivaGoldContainer
                        ) {
                            Text(
                                text = order.status.label,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = KarivaGoldDark,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = KarivaBorder)
                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Estimated Delivery", fontSize = 11.5.sp, color = KarivaTextMuted)
                    Text(order.estimatedDelivery, fontSize = 13.5.sp, fontWeight = FontWeight.Medium, color = KarivaCharcoal)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Vertical Timeline matching screenshot (Screen 8)
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, KarivaBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    steps.forEachIndexed { index, (statusEnum, info) ->
                        val (stepTitle, stepTime) = info
                        val isDone = index < currentStatusIndex || (index == currentStatusIndex && currentStatusIndex == 4)
                        val isCurrent = index == currentStatusIndex && currentStatusIndex < 4
                        val isPending = index > currentStatusIndex

                        Row(modifier = Modifier.fillMaxWidth()) {
                            // Timeline icon and connector line
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when {
                                                isDone -> KarivaCharcoal
                                                isCurrent -> KarivaGold
                                                else -> KarivaSurfaceCard
                                            }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isDone) {
                                        Icon(
                                            imageVector = Icons.Filled.Check,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    } else if (isCurrent) {
                                        Box(
                                            modifier = Modifier
                                                .size(12.dp)
                                                .clip(CircleShape)
                                                .background(Color.White)
                                        )
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(KarivaBorder)
                                        )
                                    }
                                }

                                if (index < steps.size - 1) {
                                    Box(
                                        modifier = Modifier
                                            .width(2.dp)
                                            .height(44.dp)
                                            .background(
                                                if (isDone) KarivaCharcoal else KarivaBorder
                                            )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            // Step details
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(top = 4.dp)
                            ) {
                                Text(
                                    text = stepTitle,
                                    fontSize = 14.5.sp,
                                    fontWeight = if (isCurrent || isDone) FontWeight.SemiBold else FontWeight.Normal,
                                    color = if (isPending) KarivaTextMuted else KarivaCharcoal
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = stepTime,
                                    fontSize = 11.5.sp,
                                    color = if (isPending) KarivaTextMuted else KarivaTextSecondary
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Courier / Delivery notice card matching screenshot
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = KarivaSurfaceCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, KarivaBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.LocalShipping,
                            contentDescription = null,
                            tint = KarivaCharcoal,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = "Your order is on the way!",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = KarivaCharcoal
                        )
                        Text(
                            text = "Expected delivery today by 6:00 PM with armored courier escort.",
                            fontSize = 11.5.sp,
                            color = KarivaTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            KarivaButton(
                text = "View Order Details",
                onClick = { showDetailsSheet = true },
                isSecondary = true,
                testTag = "track_view_order_details_btn"
            )
        }

        // Details Modal Sheet
        if (showDetailsSheet) {
            ModalBottomSheet(
                onDismissRequest = { showDetailsSheet = false },
                containerColor = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Text(
                        text = "Order Details (${order.id})",
                        style = MaterialTheme.typography.titleLarge.copy(fontFamily = FontFamily.Serif),
                        color = KarivaCharcoal
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Items Purchased:", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    order.items.forEach { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("${item.quantity}x ${item.product.title} (${item.selectedTier.name})", fontSize = 12.5.sp)
                            Text(formatCurrency(item.totalCost), fontWeight = FontWeight.Medium, fontSize = 12.5.sp)
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 12.dp), color = KarivaBorder)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total Paid", fontWeight = FontWeight.Bold)
                        Text(formatCurrency(order.total), fontWeight = FontWeight.Bold, color = KarivaCharcoal)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Shipping Address:", fontSize = 12.sp, color = KarivaTextMuted)
                    Text(order.deliveryAddress, fontSize = 13.sp)

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}
