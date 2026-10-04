package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.NotificationItem
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NotificationScreen(
    notifications: List<NotificationItem>,
    onBack: () -> Unit,
    onMarkAsRead: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KarivaCreamBg)
            .statusBarsPadding()
            .testTag("notification_screen")
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(1.dp, KarivaBorder, CircleShape)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = KarivaCharcoal)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = "Notifications",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.SemiBold
                ),
                color = KarivaCharcoal
            )
        }

        if (notifications.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Outlined.Notifications,
                        contentDescription = null,
                        tint = KarivaTextMuted,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No Notifications", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = KarivaCharcoal)
                    Text("You're all caught up on order alerts and product releases.", fontSize = 13.sp, color = KarivaTextSecondary)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(notifications, key = { it.id }) { item ->
                    val icon = when (item.type) {
                        "inventory" -> Icons.Outlined.WarningAmber
                        "order" -> Icons.Outlined.LocalShipping
                        else -> Icons.Outlined.CheckCircle
                    }
                    val iconColor = when (item.type) {
                        "inventory" -> KarivaWarning
                        "order" -> KarivaGoldDark
                        else -> KarivaCharcoal
                    }

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (item.isRead) Color.White else KarivaSurfaceCard,
                        border = androidx.compose.foundation.BorderStroke(1.dp, KarivaBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onMarkAsRead(item.id) }
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(iconColor.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = item.title,
                                        fontWeight = if (item.isRead) FontWeight.Medium else FontWeight.Bold,
                                        fontSize = 13.5.sp,
                                        color = KarivaCharcoal
                                    )
                                    if (!item.isRead) {
                                        Box(
                                            modifier = Modifier
                                                .size(7.dp)
                                                .clip(CircleShape)
                                                .background(KarivaGold)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(3.dp))

                                Text(
                                    text = item.message,
                                    fontSize = 12.sp,
                                    color = KarivaTextSecondary,
                                    lineHeight = 17.sp
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                val timeStr = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(item.timestamp))
                                Text(
                                    text = timeStr,
                                    fontSize = 10.5.sp,
                                    color = KarivaTextMuted
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
