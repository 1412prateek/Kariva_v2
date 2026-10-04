package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.PricingTier
import com.example.model.Product
import com.example.ui.components.KarivaButton
import com.example.ui.theme.*

@Composable
fun ProductEditorDialog(
    initialProduct: Product?,
    onDismiss: () -> Unit,
    onSave: (Product) -> Unit
) {
    if (initialProduct == null) return

    val isNew = initialProduct.id.isBlank()

    var title by remember { mutableStateOf(initialProduct.title) }
    var category by remember { mutableStateOf(initialProduct.category) }
    var priceText by remember { mutableStateOf(if (initialProduct.price > 0) initialProduct.price.toInt().toString() else "") }
    var stockText by remember { mutableStateOf(initialProduct.stock.toString()) }
    var description by remember { mutableStateOf(initialProduct.description) }
    var materials by remember { mutableStateOf(initialProduct.materials) }

    val catList = listOf(
        "Earpods Covers",
        "Phone Covers",
        "Keychains & Charms",
        "Hair & Appliqués",
        "Woolen Items"
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("product_editor_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                Text(
                    text = if (isNew) "Add Handcrafted Kurus Item" else "Edit Kurus Creation",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold
                    ),
                    color = KarivaCharcoal
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Product Title (e.g. Kurus Earpods Cover)") },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().testTag("editor_title_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text("Category", fontSize = 12.sp, color = KarivaTextMuted)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    catList.take(3).forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat, fontSize = 10.5.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = KarivaCharcoal,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    catList.drop(3).forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat, fontSize = 10.5.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = KarivaCharcoal,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = priceText,
                        onValueChange = { priceText = it },
                        label = { Text("Base Price (Rs.)") },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f).testTag("editor_price_input"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = stockText,
                        onValueChange = { stockText = it },
                        label = { Text("Stock Level") },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f).testTag("editor_stock_input"),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = materials,
                    onValueChange = { materials = it },
                    label = { Text("Yarn / Materials (e.g. 100% Soft Wool)") },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Kurus Story & Dimensions") },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    maxLines = 4
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }

                    KarivaButton(
                        text = if (isNew) "Publish Craft" else "Save Changes",
                        onClick = {
                            val price = priceText.toDoubleOrNull() ?: 350.0
                            val stock = stockText.toIntOrNull() ?: 5
                            val updatedTiers = listOf(
                                PricingTier("tier_std", "Standard Soft Yarn", price, "Single piece"),
                                PricingTier("tier_prem", "Premium Merino Blend", price * 1.25, "Ultra soft yarn"),
                                PricingTier("tier_custom", "Custom Colorway", price * 1.4, "Custom palette")
                            )

                            val productToSave = initialProduct.copy(
                                title = title.ifBlank { "Handmade Kurus Creation" },
                                category = category,
                                price = price,
                                stock = stock,
                                description = description.ifBlank { "Exclusive handcrafted crochet piece made with soft woolen yarn." },
                                materials = materials.ifBlank { "100% Handcrafted Soft Wool Yarn" },
                                pricingTiers = updatedTiers,
                                imageRes = initialProduct.imageRes ?: com.example.R.drawable.crochet_earpods_pouch
                            )
                            onSave(productToSave)
                        },
                        modifier = Modifier.weight(1.5f),
                        testTag = "editor_save_btn"
                    )
                }
            }
        }
    }
}
