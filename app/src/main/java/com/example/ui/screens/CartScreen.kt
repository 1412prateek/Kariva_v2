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
import com.example.model.CartItem
import com.example.ui.components.KarivaButton
import com.example.ui.components.formatCurrency
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    cartItems: List<CartItem>,
    onBack: () -> Unit,
    onQuantityChange: (String, Int) -> Unit,
    onRemoveItem: (String) -> Unit,
    onClearCart: () -> Unit,
    onProceedToCheckout: (deliveryAddress: String, paymentMethod: String) -> Unit,
    onShopNow: () -> Unit
) {
    val subtotal = cartItems.sumOf { it.totalCost }
    val shipping = if (subtotal > 5000 || subtotal == 0.0) 0.0 else 250.0
    val total = subtotal + shipping

    var showCheckoutDialog by remember { mutableStateOf(false) }
    var address by remember { mutableStateOf("742 Evergreen Terrace, Beverly Hills, CA") }
    var selectedPayment by remember { mutableStateOf("Apple Pay (Recommended)") }
    var isProcessingPayment by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(KarivaCreamBg)
            .statusBarsPadding()
            .testTag("cart_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = if (cartItems.isNotEmpty()) 180.dp else 90.dp)
        ) {
            // Header matching Screen 6
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
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
                        .testTag("cart_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = KarivaCharcoal
                    )
                }

                Text(
                    text = "My Cart",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = KarivaCharcoal
                )

                IconButton(
                    onClick = onClearCart,
                    enabled = cartItems.isNotEmpty(),
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(1.dp, KarivaBorder, CircleShape)
                        .testTag("cart_clear_btn")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.DeleteOutline,
                        contentDescription = "Clear Cart",
                        tint = if (cartItems.isNotEmpty()) KarivaAccentRed else KarivaTextMuted
                    )
                }
            }

            if (cartItems.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(KarivaSurfaceCard),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ShoppingBag,
                                contentDescription = null,
                                tint = KarivaGold,
                                modifier = Modifier.size(40.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Your Luxury Bag is Empty",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = KarivaCharcoal
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Explore the creator's curated collections and discover timeless fine pieces.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = KarivaTextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        KarivaButton(
                            text = "Explore Collection",
                            onClick = onShopNow,
                            modifier = Modifier.width(220.dp),
                            testTag = "cart_empty_explore_btn"
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(cartItems, key = { it.id }) { item ->
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = Color.White,
                            border = androidx.compose.foundation.BorderStroke(1.dp, KarivaBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("cart_item_${item.id}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Thumbnail
                                Box(
                                    modifier = Modifier
                                        .size(76.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(KarivaSurfaceCard)
                                ) {
                                    if (item.product.imageRes != null) {
                                        Image(
                                            painter = painterResource(id = item.product.imageRes),
                                            contentDescription = item.product.title,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    } else if (!item.product.imageUrl.isNullOrBlank()) {
                                        AsyncImage(
                                            model = item.product.imageUrl,
                                            contentDescription = item.product.title,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                // Title, tier & price
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.product.title,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                                        color = KarivaCharcoal,
                                        maxLines = 1
                                    )

                                    if (item.selectedTier.name.isNotBlank()) {
                                        Text(
                                            text = item.selectedTier.name,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = KarivaGoldDark
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = formatCurrency(item.totalCost),
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = KarivaCharcoal
                                    )
                                }

                                // Stepper (- 1 +)
                                Surface(
                                    shape = RoundedCornerShape(18.dp),
                                    color = KarivaSurfaceCard
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 2.dp)
                                    ) {
                                        IconButton(
                                            onClick = { onQuantityChange(item.id, item.quantity - 1) },
                                            modifier = Modifier.size(30.dp)
                                        ) {
                                            Text("-", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = KarivaCharcoal)
                                        }

                                        Text(
                                            text = "${item.quantity}",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = KarivaCharcoal,
                                            modifier = Modifier.padding(horizontal = 6.dp)
                                        )

                                        IconButton(
                                            onClick = { onQuantityChange(item.id, item.quantity + 1) },
                                            modifier = Modifier.size(30.dp)
                                        ) {
                                            Text("+", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = KarivaCharcoal)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                IconButton(
                                    onClick = { onRemoveItem(item.id) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Close,
                                        contentDescription = "Remove",
                                        tint = KarivaTextMuted,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Bottom Summary & Checkout Bar matching screenshot
        if (cartItems.isNotEmpty()) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(bottom = 60.dp)
                    .shadow(16.dp, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                color = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Subtotal", color = KarivaTextSecondary, fontSize = 13.sp)
                        Text(formatCurrency(subtotal), color = KarivaCharcoal, fontWeight = FontWeight.Medium, fontSize = 13.sp)
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Shipping", color = KarivaTextSecondary, fontSize = 13.sp)
                        Text(if (shipping == 0.0) "FREE" else formatCurrency(shipping), color = if (shipping == 0.0) KarivaSuccess else KarivaCharcoal, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }

                    Divider(modifier = Modifier.padding(vertical = 10.dp), color = KarivaBorder)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Total",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = KarivaCharcoal
                        )
                        Text(
                            text = formatCurrency(total),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = KarivaCharcoal
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    KarivaButton(
                        text = "Proceed to Checkout",
                        onClick = { showCheckoutDialog = true },
                        testTag = "cart_checkout_btn"
                    )
                }
            }
        }

        // Integrated Payment Processing & Checkout Sheet
        if (showCheckoutDialog) {
            ModalBottomSheet(
                onDismissRequest = { if (!isProcessingPayment) showCheckoutDialog = false },
                containerColor = Color.White,
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "Secure Checkout",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = KarivaCharcoal
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Delivery Address") },
                        leadingIcon = { Icon(Icons.Outlined.LocationOn, contentDescription = null, tint = KarivaGold) },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = KarivaCharcoal,
                            unfocusedBorderColor = KarivaBorder
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Payment Method",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = KarivaCharcoal
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val paymentOptions = listOf(
                        "Apple Pay (Recommended)",
                        "Visa •••• 4242",
                        "Razorpay / UPI Instant",
                        "Cash on Insured Delivery"
                    )

                    paymentOptions.forEach { method ->
                        val isSelected = selectedPayment == method
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isSelected) KarivaGoldContainer else Color.White,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) KarivaGold else KarivaBorder
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { selectedPayment = method }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { selectedPayment = method },
                                    colors = RadioButtonDefaults.colors(selectedColor = KarivaGoldDark)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = method,
                                    fontSize = 13.5.sp,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                    color = KarivaCharcoal
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    KarivaButton(
                        text = if (isProcessingPayment) "Processing Payment..." else "Pay ${formatCurrency(total)}",
                        onClick = {
                            isProcessingPayment = true
                            onProceedToCheckout(address, selectedPayment)
                            showCheckoutDialog = false
                            isProcessingPayment = false
                        },
                        enabled = !isProcessingPayment,
                        isGold = true,
                        testTag = "confirm_payment_btn"
                    )

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}
