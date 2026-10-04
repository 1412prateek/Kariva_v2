package com.example.ui.screens

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.Order
import com.example.model.Product
import com.example.model.UserProfile
import com.example.model.UserRole
import com.example.ui.components.formatCurrency
import com.example.ui.theme.*

@Composable
fun ProfileScreen(
    user: UserProfile?,
    latestOrder: Order?,
    lowStockProducts: List<Product>,
    unreadNotifCount: Int,
    onOrdersClick: () -> Unit,
    onWishlistClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onTrackOrderClick: (Order) -> Unit,
    onLogout: () -> Unit
) {
    val displayName = user?.displayName ?: "Shopper"
    val email = user?.email ?: "shopper@kariva.com"
    val isCreator = user?.role == UserRole.CREATOR

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(KarivaCreamBg)
            .statusBarsPadding()
            .testTag("profile_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .padding(bottom = 90.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar & Name matching Screen 9
            Box(
                modifier = Modifier
                    .size(86.dp)
                    .clip(CircleShape)
                    .background(KarivaSurfaceCard)
                    .border(2.dp, KarivaGoldLight, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.jewelry_hero_model),
                    contentDescription = displayName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = displayName,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.SemiBold
                ),
                color = KarivaCharcoal
            )

            Text(
                text = email,
                style = MaterialTheme.typography.bodyMedium,
                color = KarivaTextMuted
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Loyalty Membership Card matching Screen 9
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.Transparent,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFFEADCC6),
                                    Color(0xFFF7F1E5),
                                    Color(0xFFE5D5BA)
                                )
                            ),
                            RoundedCornerShape(20.dp)
                        )
                        .border(1.dp, KarivaGoldLight.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(KarivaGoldDark),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Diamond,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {
                                Text(
                                    text = if (isCreator) "Kariva Creator Studio" else "Kariva Gold Member",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.5.sp,
                                    color = KarivaCharcoal
                                )
                                Text(
                                    text = if (isCreator) "Master Atelier License" else "You are special! Enjoy VIP Concierge",
                                    fontSize = 11.5.sp,
                                    color = KarivaGoldDark
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Filled.ChevronRight,
                            contentDescription = null,
                            tint = KarivaGoldDark
                        )
                    }
                }
            }

            // Real-time Alerts Section (Required by prompt: "THE user dashboard should provide real-time alerts for low stock and pending orders.")
            Spacer(modifier = Modifier.height(18.dp))

            // 1. Pending Order Alert
            if (latestOrder != null) {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, KarivaBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onTrackOrderClick(latestOrder) }
                        .testTag("dashboard_pending_order_alert")
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(KarivaGoldContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.LocalShipping,
                                contentDescription = null,
                                tint = KarivaGoldDark,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Pending Order Alert", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = KarivaGoldDark)
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(shape = RoundedCornerShape(8.dp), color = KarivaCharcoal) {
                                    Text(latestOrder.status.label, color = Color.White, fontSize = 9.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }
                            Text(
                                text = "Order ${latestOrder.trackingNumber} is on its way",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = KarivaCharcoal
                            )
                        }

                        Text("Track >", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = KarivaCharcoal)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }

            // 2. Low Stock Alerts
            if (lowStockProducts.isNotEmpty()) {
                val firstLow = lowStockProducts.first()
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = KarivaWarningContainer.copy(alpha = 0.7f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, KarivaWarning.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.WarningAmber,
                            contentDescription = null,
                            tint = KarivaWarning,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Low Stock Alert: ${firstLow.title}",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = KarivaWarning
                            )
                            Text(
                                text = "Only ${firstLow.stock} piece(s) remaining in stock. Order before it sells out!",
                                fontSize = 11.5.sp,
                                color = KarivaCharcoal
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Menu Items matching Screen 9
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, KarivaBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(vertical = 6.dp)) {
                    ProfileMenuItem(
                        icon = Icons.Outlined.ShoppingBag,
                        label = "My Orders",
                        badge = if (latestOrder != null) "1 Active" else null,
                        onClick = onOrdersClick
                    )
                    ProfileDivider()

                    ProfileMenuItem(
                        icon = Icons.Outlined.FavoriteBorder,
                        label = "My Wishlist",
                        onClick = onWishlistClick
                    )
                    ProfileDivider()

                    ProfileMenuItem(
                        icon = Icons.Outlined.Notifications,
                        label = "Notifications",
                        badge = if (unreadNotifCount > 0) "$unreadNotifCount New" else null,
                        onClick = onNotificationsClick
                    )
                    ProfileDivider()

                    ProfileMenuItem(
                        icon = Icons.Outlined.LocationOn,
                        label = "Addresses",
                        onClick = {}
                    )
                    ProfileDivider()

                    ProfileMenuItem(
                        icon = Icons.Outlined.CreditCard,
                        label = "Payment Methods",
                        onClick = {}
                    )
                    ProfileDivider()

                    ProfileMenuItem(
                        icon = Icons.Outlined.SupportAgent,
                        label = "Help & Support",
                        onClick = {}
                    )
                    ProfileDivider()

                    ProfileMenuItem(
                        icon = Icons.Outlined.Settings,
                        label = "Settings",
                        onClick = {}
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Sign Out
            OutlinedButton(
                onClick = onLogout,
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = KarivaAccentRed),
                border = androidx.compose.foundation.BorderStroke(1.dp, KarivaAccentRed.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("profile_logout_btn")
            ) {
                Icon(Icons.Outlined.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sign Out", fontSize = 13.5.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    label: String,
    badge: String? = null,
    isHighlight: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isHighlight) KarivaGoldDark else KarivaCharcoal,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = label,
                fontSize = 13.5.sp,
                fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.Medium,
                color = if (isHighlight) KarivaGoldDark else KarivaCharcoal
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (badge != null) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isHighlight) KarivaGoldContainer else KarivaCharcoal
                ) {
                    Text(
                        text = badge,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isHighlight) KarivaGoldDark else Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = KarivaBorder,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun ProfileDivider() {
    Divider(modifier = Modifier.padding(horizontal = 16.dp), color = KarivaBorder.copy(alpha = 0.5f))
}
