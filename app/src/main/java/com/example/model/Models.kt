package com.example.model

enum class UserRole {
    CUSTOMER,
    CREATOR
}

data class CustomerDetails(
    val customer_id: String = "",
    val customer_uuid: String = "",
    val customer_first_name: String = "",
    val customer_middle_name: String = "",
    val customer_last_name: String = "",
    val customer_email: String = "",
    val customer_mobile_no: String = "",
    val customer_house_no: String = "",
    val customer_address_line_1: String = "",
    val customer_address_line_2: String = "",
    val customer_district: String = "",
    val customer_state: String = "",
    val customer_country: String = "India",
    val customer_zip_code: String = "",
    val created_at: Long = System.currentTimeMillis(),
    val updated_at: Long = System.currentTimeMillis(),
    val customer_location: Map<String, Any>? = null
) {
    val fullFormattedAddress: String
        get() = listOfNotNull(
            customer_house_no.ifBlank { null },
            customer_address_line_1.ifBlank { null },
            customer_address_line_2.ifBlank { null },
            customer_district.ifBlank { null },
            customer_state.ifBlank { null },
            customer_zip_code.ifBlank { null },
            customer_country.ifBlank { null }
        ).joinToString(", ")

    val fullName: String
        get() = listOfNotNull(
            customer_first_name.ifBlank { null },
            customer_middle_name.ifBlank { null },
            customer_last_name.ifBlank { null }
        ).joinToString(" ")
}

data class UserProfile(
    val id: String = "",
    val email: String = "",
    val displayName: String = "",
    val role: UserRole = UserRole.CUSTOMER,
    val loyaltyTier: String = "Kariva Artisan Patron",
    val phoneNumber: String = "+91 98765 43210",
    val address: String = "Sector 14, Urban Estate, Gurugram",
    val customerDetails: CustomerDetails? = null
)

data class PricingTier(
    val id: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val description: String = ""
)

data class Product(
    val id: String = "",
    val title: String = "",
    val category: String = "Earpods Covers",
    val price: Double = 0.0,
    val originalPrice: Double? = null,
    val rating: Float = 4.9f,
    val reviewsCount: Int = 86,
    val description: String = "",
    val materials: String = "100% Breathable Soft Woolen Yarn",
    val imageRes: Int? = null,
    val imageUrl: String? = null,
    val stock: Int = 10,
    val pricingTiers: List<PricingTier> = emptyList(),
    val isFeatured: Boolean = true,
    val tags: List<String> = emptyList(),
    val creatorEmail: String = "shikha@kariva.com",
    val createdAt: Long = System.currentTimeMillis()
) {
    val isLowStock: Boolean get() = stock in 1..4
    val isOutOfStock: Boolean get() = stock <= 0
}

data class CartItem(
    val id: String = "",
    val product: Product,
    val quantity: Int = 1,
    val selectedTier: PricingTier = PricingTier("default", "Standard", product.price)
) {
    val totalCost: Double get() = (if (selectedTier.price > 0) selectedTier.price else product.price) * quantity
}

enum class OrderStatus(val label: String) {
    ORDER_PLACED("Order Placed"),
    PROCESSING("Crafting & Knitting"),
    SHIPPED("Shipped"),
    OUT_FOR_DELIVERY("Out for Delivery"),
    DELIVERED("Delivered"),
    CANCELLED("Cancelled")
}

data class TimelineStep(
    val status: OrderStatus,
    val title: String,
    val timestamp: String,
    val isCompleted: Boolean,
    val isCurrent: Boolean,
    val description: String
)

data class Order(
    val id: String = "",
    val userId: String = "",
    val userEmail: String = "",
    val items: List<CartItem> = emptyList(),
    val subtotal: Double = 0.0,
    val shipping: Double = 0.0,
    val total: Double = 0.0,
    val status: OrderStatus = OrderStatus.ORDER_PLACED,
    val placedAt: Long = System.currentTimeMillis(),
    val estimatedDelivery: String = "In 2-3 Days",
    val trackingNumber: String = "KV-88392194",
    val deliveryAddress: String = "Sector 14, Urban Estate, Gurugram",
    val paymentMethod: String = "UPI / PhonePe"
)

data class NotificationItem(
    val id: String = "",
    val title: String = "",
    val message: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val type: String = "order",
    val isRead: Boolean = false
)

data class CreatorAnalytics(
    val totalRevenue: Double = 0.0,
    val totalOrders: Int = 0,
    val activeListings: Int = 0,
    val lowStockCount: Int = 0,
    val categoryBreakdown: Map<String, Int> = emptyMap()
)
