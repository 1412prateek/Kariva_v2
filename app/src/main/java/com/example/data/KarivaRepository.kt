package com.example.data

import android.content.Context
import android.util.Log
import com.example.R
import com.example.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class KarivaRepository private constructor(private val context: Context) {

    private val scope = CoroutineScope(Dispatchers.IO)
    private val auth: FirebaseAuth? by lazy {
        try {
            FirebaseAuth.getInstance()
        } catch (e: Exception) {
            Log.w("KarivaRepo", "Firebase Auth init fallback: ${e.message}")
            null
        }
    }

    private val firestore: FirebaseFirestore? by lazy {
        try {
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            Log.w("KarivaRepo", "Firestore init fallback: ${e.message}")
            null
        }
    }

    private var productsListener: ListenerRegistration? = null
    private var ordersListener: ListenerRegistration? = null

    // State flows
    private val _currentUser = MutableStateFlow<UserProfile?>(null)
    val currentUser: StateFlow<UserProfile?> = _currentUser.asStateFlow()

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _cart = MutableStateFlow<List<CartItem>>(emptyList())
    val cart: StateFlow<List<CartItem>> = _cart.asStateFlow()

    private val _wishlist = MutableStateFlow<Set<String>>(setOf("prod_1", "prod_3"))
    val wishlist: StateFlow<Set<String>> = _wishlist.asStateFlow()

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    private val _notifications = MutableStateFlow<List<NotificationItem>>(emptyList())
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    private val _analytics = MutableStateFlow(CreatorAnalytics())
    val analytics: StateFlow<CreatorAnalytics> = _analytics.asStateFlow()

    init {
        initializeSeedData()
        setupFirestoreListeners()
        updateAnalytics()
    }

    private fun initializeSeedData() {
        val initialProducts = listOf(
            Product(
                id = "prod_1",
                title = "Handmade Kurus Earpods Case Pouch",
                category = "Earpods Covers",
                price = 380.0,
                originalPrice = 499.0,
                rating = 4.9f,
                reviewsCount = 92,
                description = "Custom handcrafted olive green and white crochet pouch with adjustable drawstring closure. Designed specifically to cradle wireless earbud cases (AirPods, OnePlus Buds) with plush shock-absorbing wool knit.",
                materials = "100% Breathable Soft Acrylic Wool with Braided Ties",
                imageRes = R.drawable.crochet_earpods_pouch,
                stock = 14,
                pricingTiers = listOf(
                    PricingTier("tier_std", "Standard Soft Yarn", 380.0, "Classic durable everyday protection"),
                    PricingTier("tier_dual", "Cotton-Wool Dual Tone", 450.0, "Breathable natural fiber blend"),
                    PricingTier("tier_fleece", "Extra Padded Fleece Line", 520.0, "Inner shock absorbing lining")
                ),
                tags = listOf("Bestseller", "Trending"),
                isFeatured = true
            ),
            Product(
                id = "prod_2",
                title = "Pastel Lattice Kurus Phone Cover Sleeve",
                category = "Phone Covers",
                price = 650.0,
                originalPrice = 850.0,
                rating = 4.8f,
                reviewsCount = 68,
                description = "Artisan-crafted blush pink and ivory white crocheted phone sleeve bag with hand-woven grid openwork pattern, top reinforced ribbed rim, and crossbody shoulder carrying strap.",
                materials = "High-Tensile Spun Wool Yarn with Crossbody Strap",
                imageRes = R.drawable.crochet_phone_sleeve,
                stock = 3, // Low stock trigger
                pricingTiers = listOf(
                    PricingTier("tier_compact", "Compact (Up to 6.1\" screens)", 650.0, "Fits iPhone 13/14/15/16"),
                    PricingTier("tier_plus", "Plus / Max (Up to 6.7\" screens)", 750.0, "Fits Pro Max and Ultra models"),
                    PricingTier("tier_pocket", "With Card Pocket", 820.0, "Adds back knitted card slot")
                ),
                tags = listOf("Low Stock", "Artisan Favorite"),
                isFeatured = true
            ),
            Product(
                id = "prod_3",
                title = "Blooming Daisy Flower Kurus Keychain",
                category = "Keychains & Charms",
                price = 220.0,
                originalPrice = 299.0,
                rating = 4.9f,
                reviewsCount = 114,
                description = "Vibrant magenta pink and sunny yellow puffy daisy flower charm with dark green knitted hanging loop. Hand-stitched petals add playful handmade texture to keys, backpacks, or handbags.",
                materials = "Fluffy Woolen Yarn with Braided Key Loop",
                imageRes = R.drawable.crochet_flower_keychain,
                stock = 18,
                pricingTiers = listOf(
                    PricingTier("tier_single", "Single Flower Charm", 220.0, "One daisy charm with strap"),
                    PricingTier("tier_pair", "Matching Pair Set", 390.0, "Two matching daisy charms")
                ),
                tags = listOf("New Arrival", "Popular"),
                isFeatured = true
            ),
            Product(
                id = "prod_4",
                title = "Sunny Yellow Crochet Butterfly Bow",
                category = "Hair & Appliqués",
                price = 180.0,
                originalPrice = 250.0,
                rating = 4.7f,
                reviewsCount = 45,
                description = "Charming bright yellow knit bow appliqué with centered cinch knot. Multi-purpose craft accessory perfect as a hair clip, tote bag embellishment, or garment patch.",
                materials = "Soft Baby Wool Yarn with Non-Snag Clip",
                imageRes = R.drawable.crochet_yellow_bow,
                stock = 2, // Low stock trigger
                pricingTiers = listOf(
                    PricingTier("tier_clip", "With Hair Clip Pin", 180.0, "Mounted on metal snap clip"),
                    PricingTier("tier_sewon", "Sew-on Appliqué", 150.0, "Flat knit for stitching")
                ),
                tags = listOf("Low Stock"),
                isFeatured = true
            ),
            Product(
                id = "prod_5",
                title = "Double Tulip Bell Vine Car & Bag Hanging",
                category = "Keychains & Charms",
                price = 340.0,
                originalPrice = 450.0,
                rating = 5.0f,
                reviewsCount = 53,
                description = "Handmade green vine with twin drooping yellow bell tulip flowers and leaf accents. Beautiful hanging charm for car rear-view mirrors, room curtains, or handbag decor.",
                materials = "Natural Cotton-Wool Blend Yarn",
                imageRes = R.drawable.crochet_tulip_hanging,
                stock = 7,
                pricingTiers = listOf(
                    PricingTier("tier_yellow", "Sunny Yellow Bells", 340.0, "Twin yellow tulip bells"),
                    PricingTier("tier_pastel", "Pastel Mix Bells", 370.0, "Pink and yellow combination")
                ),
                tags = listOf("Creator's Pick"),
                isFeatured = true
            ),
            Product(
                id = "prod_6",
                title = "Artisanal Patchwork Woolen Cardigan",
                category = "Woolen Items",
                price = 2850.0,
                originalPrice = 3400.0,
                rating = 4.9f,
                reviewsCount = 31,
                description = "Slow-crafted oversized woolen cardigan made from authentic hand-stitched floral granny squares, cozy balloon sleeves, and wooden buttons.",
                materials = "100% Warm Pure Wool Skeins",
                imageRes = R.drawable.crochet_hero_artisan,
                stock = 4,
                pricingTiers = listOf(
                    PricingTier("tier_s_m", "Size S / M (Oversized)", 2850.0, "Bust 36-40 inches"),
                    PricingTier("tier_l_xl", "Size L / XL (Relaxed)", 3100.0, "Bust 42-46 inches")
                ),
                tags = listOf("Limited Edition"),
                isFeatured = false
            ),
            Product(
                id = "prod_7",
                title = "Cozy Floral Crochet Coaster Set",
                category = "Woolen Items",
                price = 390.0,
                originalPrice = 499.0,
                rating = 4.8f,
                reviewsCount = 39,
                description = "Set of 4 hand-knitted floral crochet coasters that protect surfaces from heat and add cozy handmade cottagecore warmth to your coffee table.",
                materials = "Absorbent Washable Cotton Wool Yarn",
                imageRes = R.drawable.crochet_flower_keychain,
                stock = 9,
                pricingTiers = listOf(
                    PricingTier("tier_set4", "Set of 4 Coasters", 390.0, "Four assorted floral designs"),
                    PricingTier("tier_set6", "Set of 6 Coasters", 550.0, "Six assorted floral designs")
                ),
                tags = listOf("Home Decor"),
                isFeatured = false
            )
        )

        _products.value = initialProducts

        // Initial default cart matching reference image
        _cart.value = listOf(
            CartItem("c_1", initialProducts[0], 1, initialProducts[0].pricingTiers[0]),
            CartItem("c_2", initialProducts[1], 1, initialProducts[1].pricingTiers[0]),
            CartItem("c_3", initialProducts[2], 2, initialProducts[2].pricingTiers[0])
        )

        // Initial active order
        val sampleOrder = Order(
            id = "ORD-2026-9812",
            userId = "usr_demo",
            userEmail = "sana.ansari@gmail.com",
            items = listOf(
                CartItem("ci_1", initialProducts[0], 1, initialProducts[0].pricingTiers[0]),
                CartItem("ci_2", initialProducts[2], 1, initialProducts[2].pricingTiers[0])
            ),
            subtotal = 600.0,
            shipping = 0.0,
            total = 600.0,
            status = OrderStatus.OUT_FOR_DELIVERY,
            placedAt = System.currentTimeMillis() - 86400000L * 2,
            estimatedDelivery = "Expected delivery: Today by 5:00 PM",
            trackingNumber = "KV-88392194",
            deliveryAddress = "Sector 14, Urban Estate, Gurugram",
            paymentMethod = "UPI (PhonePe •••• 4321)"
        )
        _orders.value = listOf(sampleOrder)

        // Seed notifications for crochet craft updates
        _notifications.value = listOf(
            NotificationItem(
                id = "notif_1",
                title = "Order Out For Delivery",
                message = "Your handmade crochet package (KV-88392194) is with our courier and will arrive today.",
                timestamp = System.currentTimeMillis() - 3600000L * 3,
                type = "order"
            ),
            NotificationItem(
                id = "notif_2",
                title = "Low Stock Alert: Kurus Phone Sleeve",
                message = "Only 3 pieces remaining of the Pastel Lattice Phone Sleeve in stock.",
                timestamp = System.currentTimeMillis() - 3600000L * 12,
                type = "inventory"
            ),
            NotificationItem(
                id = "notif_3",
                title = "New Creator Drop: Tulip Bell Vine",
                message = "Shikha published a new batch of handcrafted tulip car & bag hangings.",
                timestamp = System.currentTimeMillis() - 86400000L,
                type = "promotion"
            )
        )
    }

    private fun setupFirestoreListeners() {
        val db = firestore ?: return
        try {
            productsListener = db.collection("products")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.w("KarivaRepo", "Listen error: ${error.message}")
                        return@addSnapshotListener
                    }
                    if (snapshot != null && !snapshot.isEmpty) {
                        val remoteList = mutableListOf<Product>()
                        for (doc in snapshot.documents) {
                            try {
                                val p = doc.toObject(Product::class.java)
                                if (p != null) remoteList.add(p.copy(id = doc.id))
                            } catch (e: Exception) {
                                Log.e("KarivaRepo", "Error mapping doc: ${e.message}")
                            }
                        }
                        if (remoteList.isNotEmpty()) {
                            _products.value = remoteList
                            updateAnalytics()
                        }
                    }
                }

            ordersListener = db.collection("orders")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) return@addSnapshotListener
                    if (snapshot != null && !snapshot.isEmpty) {
                        val remoteOrders = mutableListOf<Order>()
                        for (doc in snapshot.documents) {
                            try {
                                val o = doc.toObject(Order::class.java)
                                if (o != null) remoteOrders.add(o.copy(id = doc.id))
                            } catch (e: Exception) {
                                Log.e("KarivaRepo", "Error mapping order: ${e.message}")
                            }
                        }
                        if (remoteOrders.isNotEmpty()) {
                            _orders.value = remoteOrders
                            updateAnalytics()
                        }
                    }
                }
        } catch (e: Exception) {
            Log.e("KarivaRepo", "Firestore setup exception: ${e.message}")
        }
    }

    fun updateAnalytics() {
        val currentOrders = _orders.value
        val currentProducts = _products.value

        val totalRev = currentOrders.sumOf { it.total }
        val lowStock = currentProducts.count { it.isLowStock }
        val breakdown = currentProducts.groupBy { it.category }.mapValues { it.value.size }

        _analytics.value = CreatorAnalytics(
            totalRevenue = if (totalRev > 0) totalRev else 18450.0,
            totalOrders = if (currentOrders.isNotEmpty()) currentOrders.size else 24,
            activeListings = currentProducts.size,
            lowStockCount = lowStock,
            categoryBreakdown = breakdown
        )
    }

    // Authentication methods with direct Firebase Authentication
    suspend fun signIn(email: String, pass: String): Result<UserProfile> {
        val cleanEmail = email.trim().lowercase(Locale.ROOT)
        val isCreator = cleanEmail == "shikha@kariva.com"

        if (cleanEmail.isBlank() || pass.isBlank()) {
            return Result.failure(Exception("Please enter both email and password."))
        }

        // Creator authentication check
        if (isCreator) {
            if (pass != "Shikha@1810") {
                return Result.failure(Exception("Invalid creator credentials. Access denied."))
            }
            // Ensure creator is also registered in Firebase
            val fbCreator = auth
            if (fbCreator != null) {
                try {
                    fbCreator.signInWithEmailAndPassword(cleanEmail, pass).await()
                } catch (_: Exception) {
                    try {
                        val res = fbCreator.createUserWithEmailAndPassword(cleanEmail, pass).await()
                        res.user?.updateProfile(
                            UserProfileChangeRequest.Builder()
                                .setDisplayName("Shikha (Crochet Creator)")
                                .build()
                        )?.await()
                        firestore?.collection("users")?.document(res.user?.uid ?: "creator_shikha")?.set(
                            hashMapOf(
                                "uid" to (res.user?.uid ?: "creator_shikha"),
                                "name" to "Shikha",
                                "email" to cleanEmail,
                                "role" to "CREATOR",
                                "createdAt" to System.currentTimeMillis()
                            )
                        )?.await()
                    } catch (_: Exception) {}
                }
            }

            val creatorProfile = UserProfile(
                id = auth?.currentUser?.uid ?: "creator_shikha",
                email = "shikha@kariva.com",
                displayName = "Shikha (Crochet Creator)",
                role = UserRole.CREATOR,
                loyaltyTier = "Kariva Master Knitter & Founder"
            )
            _currentUser.value = creatorProfile
            return Result.success(creatorProfile)
        }

        // Customer authentication via Firebase Auth
        val fb = auth
        if (fb == null) {
            return Result.failure(Exception("Firebase Authentication is not initialized."))
        }

        try {
            val authResult = fb.signInWithEmailAndPassword(cleanEmail, pass).await()
            val fbUser = authResult.user ?: throw Exception("Authentication returned empty user session.")
            val uid = fbUser.uid

            // Fetch dynamic customer details from Firestore if available
            var customerDetails: CustomerDetails? = null
            try {
                val doc = firestore?.collection("customer_details")?.document(uid)?.get()?.await()
                if (doc != null && doc.exists()) {
                    customerDetails = doc.toObject(CustomerDetails::class.java)
                }
            } catch (de: Exception) {
                Log.w("KarivaRepo", "Fetch customer_details error: ${de.message}")
            }

            val displayName = customerDetails?.fullName?.ifBlank { null }
                ?: fbUser.displayName
                ?: cleanEmail.substringBefore("@").replaceFirstChar { it.uppercase() }

            val profile = UserProfile(
                id = uid,
                email = cleanEmail,
                displayName = displayName,
                role = UserRole.CUSTOMER,
                loyaltyTier = "Kariva Artisan Patron",
                phoneNumber = customerDetails?.customer_mobile_no ?: "+91 98765 43210",
                address = customerDetails?.fullFormattedAddress?.ifBlank { null } ?: "Sector 14, Urban Estate, Gurugram",
                customerDetails = customerDetails
            )
            _currentUser.value = profile
            return Result.success(profile)
        } catch (e: Exception) {
            Log.e("KarivaRepo", "Firebase signIn error: ${e.message}", e)
            val rawMsg = e.localizedMessage ?: e.message ?: ""
            val userMsg = when {
                rawMsg.contains("user-not-found", ignoreCase = true) ||
                rawMsg.contains("no user record", ignoreCase = true) ||
                rawMsg.contains("INVALID_LOGIN_CREDENTIALS", ignoreCase = true) ->
                    "User not registered. Please register first."
                rawMsg.contains("wrong-password", ignoreCase = true) ->
                    "Invalid credentials. Please check your password."
                rawMsg.contains("CONFIGURATION_NOT_FOUND", ignoreCase = true) ||
                rawMsg.contains("disabled", ignoreCase = true) ->
                    "Firebase Email/Password provider is disabled. Please enable 'Email/Password' in Firebase Console under Sign-in method tab."
                rawMsg.contains("network-request-failed", ignoreCase = true) ->
                    "Network error. Please check your internet connection."
                else -> rawMsg
            }
            return Result.failure(Exception(userMsg))
        }
    }

    suspend fun signUp(name: String, email: String, pass: String): Result<UserProfile> {
        val cleanEmail = email.trim().lowercase(Locale.ROOT)
        val isCreator = cleanEmail == "shikha@kariva.com"

        if (name.isBlank()) {
            return Result.failure(Exception("Please enter your full name."))
        }
        if (cleanEmail.isBlank() || !cleanEmail.contains("@") || !cleanEmail.contains(".")) {
            return Result.failure(Exception("Please enter a valid email address."))
        }
        if (pass.length < 6) {
            return Result.failure(Exception("Password must be at least 6 characters."))
        }

        val fb = auth
        if (fb == null) {
            return Result.failure(Exception("Firebase Authentication is not initialized."))
        }

        try {
            // Real Firebase createUser call
            val authResult = fb.createUserWithEmailAndPassword(cleanEmail, pass).await()
            val fbUser = authResult.user ?: throw Exception("Failed to retrieve created Firebase user.")
            val uid = fbUser.uid

            // Set user profile display name in Firebase
            try {
                fbUser.updateProfile(
                    UserProfileChangeRequest.Builder()
                        .setDisplayName(name.trim())
                        .build()
                ).await()
            } catch (pe: Exception) {
                Log.w("KarivaRepo", "Profile name update notice: ${pe.message}")
            }

            // Sync user to Firestore "users" collection so it appears in database
            try {
                firestore?.collection("users")?.document(uid)?.set(
                    hashMapOf(
                        "uid" to uid,
                        "name" to name.trim(),
                        "email" to cleanEmail,
                        "role" to if (isCreator) "CREATOR" else "CUSTOMER",
                        "createdAt" to System.currentTimeMillis()
                    )
                )?.await()
            } catch (fe: Exception) {
                Log.w("KarivaRepo", "Firestore users sync notice: ${fe.message}")
            }

            val profile = UserProfile(
                id = uid,
                email = cleanEmail,
                displayName = name.trim(),
                role = if (isCreator) UserRole.CREATOR else UserRole.CUSTOMER,
                loyaltyTier = if (isCreator) "Kariva Master Knitter & Founder" else "Kariva Artisan Patron"
            )
            _currentUser.value = profile
            return Result.success(profile)
        } catch (e: Exception) {
            Log.e("KarivaRepo", "Firebase signUp error: ${e.message}", e)
            val rawMsg = e.localizedMessage ?: e.message ?: ""
            val userMsg = when {
                rawMsg.contains("email-already-in-use", ignoreCase = true) ||
                rawMsg.contains("already registered", ignoreCase = true) ->
                    "Email is already registered. Please login."
                rawMsg.contains("CONFIGURATION_NOT_FOUND", ignoreCase = true) ||
                rawMsg.contains("disabled", ignoreCase = true) ->
                    "Firebase Email/Password provider is disabled. Please enable 'Email/Password' in Firebase Console under Sign-in method tab."
                rawMsg.contains("weak-password", ignoreCase = true) ->
                    "Password is too weak. Please use at least 6 characters."
                rawMsg.contains("invalid-email", ignoreCase = true) ->
                    "Please enter a valid email address."
                rawMsg.contains("network-request-failed", ignoreCase = true) ->
                    "Network error. Please check your internet connection."
                else -> rawMsg
            }
            return Result.failure(Exception(userMsg))
        }
    }

    suspend fun completeCustomerRegistration(details: CustomerDetails, pass: String): Result<UserProfile> {
        val cleanEmail = details.customer_email.trim().lowercase(Locale.ROOT)
        val fb = auth ?: return Result.failure(Exception("Firebase Authentication is not initialized."))

        try {
            // 1. Create Firebase Auth user
            val authResult = fb.createUserWithEmailAndPassword(cleanEmail, pass).await()
            val fbUser = authResult.user ?: throw Exception("Failed to create user in Firebase.")
            val uid = fbUser.uid

            // 2. Set Firebase User Display Name
            try {
                fbUser.updateProfile(
                    UserProfileChangeRequest.Builder()
                        .setDisplayName(details.fullName)
                        .build()
                ).await()
            } catch (pe: Exception) {
                Log.w("KarivaRepo", "User profile name update notice: ${pe.message}")
            }

            // 3. Save customer details to Firestore customer_details collection
            val enrichedDetails = details.copy(
                customer_id = "cust_${System.currentTimeMillis()}",
                customer_uuid = uid,
                customer_email = cleanEmail,
                created_at = System.currentTimeMillis(),
                updated_at = System.currentTimeMillis()
            )

            val firestoreMap = hashMapOf(
                "customer_id" to enrichedDetails.customer_id,
                "customer_uuid" to enrichedDetails.customer_uuid,
                "customer_first_name" to enrichedDetails.customer_first_name,
                "customer_middle_name" to enrichedDetails.customer_middle_name,
                "customer_last_name" to enrichedDetails.customer_last_name,
                "customer_email" to enrichedDetails.customer_email,
                "customer_mobile_no" to enrichedDetails.customer_mobile_no,
                "customer_house_no" to enrichedDetails.customer_house_no,
                "customer_address_line_1" to enrichedDetails.customer_address_line_1,
                "customer_address_line_2" to enrichedDetails.customer_address_line_2,
                "customer_district" to enrichedDetails.customer_district,
                "customer_state" to enrichedDetails.customer_state,
                "customer_country" to enrichedDetails.customer_country,
                "customer_zip_code" to enrichedDetails.customer_zip_code,
                "created_at" to enrichedDetails.created_at,
                "updated_at" to enrichedDetails.updated_at,
                "customer_location" to enrichedDetails.customer_location
            )

            firestore?.collection("customer_details")?.document(uid)?.set(firestoreMap)?.await()

            // Also keep users collection in sync
            firestore?.collection("users")?.document(uid)?.set(
                hashMapOf(
                    "uid" to uid,
                    "name" to enrichedDetails.fullName,
                    "email" to cleanEmail,
                    "role" to "CUSTOMER",
                    "phoneNumber" to enrichedDetails.customer_mobile_no,
                    "address" to enrichedDetails.fullFormattedAddress,
                    "createdAt" to System.currentTimeMillis()
                )
            )?.await()

            val profile = UserProfile(
                id = uid,
                email = cleanEmail,
                displayName = enrichedDetails.fullName,
                role = UserRole.CUSTOMER,
                loyaltyTier = "Kariva Artisan Patron",
                phoneNumber = enrichedDetails.customer_mobile_no,
                address = enrichedDetails.fullFormattedAddress,
                customerDetails = enrichedDetails
            )
            _currentUser.value = profile
            return Result.success(profile)
        } catch (e: Exception) {
            Log.e("KarivaRepo", "Complete customer registration error: ${e.message}", e)
            val rawMsg = e.localizedMessage ?: e.message ?: ""
            val userMsg = when {
                rawMsg.contains("email-already-in-use", ignoreCase = true) ||
                rawMsg.contains("already registered", ignoreCase = true) ->
                    "Email is already registered. Please login."
                rawMsg.contains("CONFIGURATION_NOT_FOUND", ignoreCase = true) ||
                rawMsg.contains("disabled", ignoreCase = true) ->
                    "Firebase Email/Password provider is disabled. Please enable 'Email/Password' in Firebase Console under Sign-in method tab."
                rawMsg.contains("weak-password", ignoreCase = true) ->
                    "Password must be at least 8 characters long and include an uppercase letter, lowercase letter, number, and special character."
                rawMsg.contains("invalid-email", ignoreCase = true) ->
                    "Please enter a valid email address."
                rawMsg.contains("network-request-failed", ignoreCase = true) ->
                    "Network error. Please check your internet connection."
                else -> rawMsg
            }
            return Result.failure(Exception(userMsg))
        }
    }

    suspend fun saveCustomerDetails(details: CustomerDetails): Result<CustomerDetails> {
        val uid = details.customer_uuid.ifBlank {
            auth?.currentUser?.uid ?: _currentUser.value?.id ?: "cust_${UUID.randomUUID().toString().take(8)}"
        }
        val customerId = details.customer_id.ifBlank {
            "cust_${System.currentTimeMillis()}"
        }

        val enriched = details.copy(
            customer_id = customerId,
            customer_uuid = uid,
            updated_at = System.currentTimeMillis()
        )

        val firestoreMap = hashMapOf(
            "customer_id" to enriched.customer_id,
            "customer_uuid" to enriched.customer_uuid,
            "customer_first_name" to enriched.customer_first_name,
            "customer_middle_name" to enriched.customer_middle_name,
            "customer_last_name" to enriched.customer_last_name,
            "customer_email" to enriched.customer_email,
            "customer_mobile_no" to enriched.customer_mobile_no,
            "customer_house_no" to enriched.customer_house_no,
            "customer_address_line_1" to enriched.customer_address_line_1,
            "customer_address_line_2" to enriched.customer_address_line_2,
            "customer_district" to enriched.customer_district,
            "customer_state" to enriched.customer_state,
            "customer_country" to enriched.customer_country,
            "customer_zip_code" to enriched.customer_zip_code,
            "created_at" to enriched.created_at,
            "updated_at" to enriched.updated_at,
            "customer_location" to enriched.customer_location
        )

        try {
            firestore?.collection("customer_details")?.document(uid)?.set(firestoreMap)?.await()
            firestore?.collection("users")?.document(uid)?.set(
                hashMapOf(
                    "uid" to uid,
                    "name" to enriched.fullName,
                    "email" to enriched.customer_email,
                    "phoneNumber" to enriched.customer_mobile_no,
                    "address" to enriched.fullFormattedAddress,
                    "role" to "CUSTOMER",
                    "updated_at" to enriched.updated_at
                )
            )?.await()
        } catch (e: Exception) {
            Log.e("KarivaRepo", "saveCustomerDetails firestore error: ${e.message}", e)
        }

        val updatedProfile = (_currentUser.value ?: UserProfile(id = uid, email = enriched.customer_email)).copy(
            displayName = enriched.fullName.ifBlank { _currentUser.value?.displayName ?: "Artisan Patron" },
            phoneNumber = enriched.customer_mobile_no,
            address = enriched.fullFormattedAddress,
            customerDetails = enriched
        )
        _currentUser.value = updatedProfile

        return Result.success(enriched)
    }

    fun signOut() {
        auth?.signOut()
        _currentUser.value = null
    }

    // Product CRUD operations
    fun addProduct(product: Product) {
        val newProduct = if (product.id.isBlank()) {
            product.copy(id = "prod_${System.currentTimeMillis()}")
        } else product

        val updated = listOf(newProduct) + _products.value
        _products.value = updated
        updateAnalytics()

        scope.launch {
            try {
                firestore?.collection("products")?.document(newProduct.id)?.set(newProduct)
            } catch (e: Exception) {
                Log.w("KarivaRepo", "Firestore addProduct failed: ${e.message}")
            }
        }

        addNotification(
            title = "New Handcrafted Piece Listed",
            message = "Added '${newProduct.title}' with stock ${newProduct.stock} to store catalog.",
            type = "inventory"
        )
    }

    fun updateProduct(product: Product) {
        val updated = _products.value.map {
            if (it.id == product.id) product else it
        }
        _products.value = updated
        updateAnalytics()

        scope.launch {
            try {
                firestore?.collection("products")?.document(product.id)?.set(product)
            } catch (e: Exception) {
                Log.w("KarivaRepo", "Firestore updateProduct failed: ${e.message}")
            }
        }

        if (product.isLowStock) {
            addNotification(
                title = "Low Stock Alert: ${product.title}",
                message = "Only ${product.stock} pieces remaining. Knit a new batch soon.",
                type = "inventory"
            )
        }
    }

    fun deleteProduct(productId: String) {
        _products.value = _products.value.filter { it.id != productId }
        _cart.value = _cart.value.filter { it.product.id != productId }
        updateAnalytics()

        scope.launch {
            try {
                firestore?.collection("products")?.document(productId)?.delete()
            } catch (e: Exception) {
                Log.w("KarivaRepo", "Firestore deleteProduct failed: ${e.message}")
            }
        }
    }

    fun updateStock(productId: String, newStock: Int) {
        val target = _products.value.find { it.id == productId } ?: return
        val updated = target.copy(stock = newStock.coerceAtLeast(0))
        updateProduct(updated)
    }

    // Cart operations
    fun addToCart(product: Product, tier: PricingTier, quantity: Int = 1) {
        val currentCart = _cart.value.toMutableList()
        val existingIndex = currentCart.indexOfFirst {
            it.product.id == product.id && it.selectedTier.id == tier.id
        }

        if (existingIndex >= 0) {
            val item = currentCart[existingIndex]
            currentCart[existingIndex] = item.copy(quantity = item.quantity + quantity)
        } else {
            currentCart.add(
                CartItem(
                    id = "cart_${System.currentTimeMillis()}",
                    product = product,
                    quantity = quantity,
                    selectedTier = tier
                )
            )
        }
        _cart.value = currentCart
    }

    fun updateCartItemQuantity(cartItemId: String, quantity: Int) {
        if (quantity <= 0) {
            removeFromCart(cartItemId)
        } else {
            _cart.value = _cart.value.map {
                if (it.id == cartItemId) it.copy(quantity = quantity) else it
            }
        }
    }

    fun removeFromCart(cartItemId: String) {
        _cart.value = _cart.value.filter { it.id != cartItemId }
    }

    fun clearCart() {
        _cart.value = emptyList()
    }

    // Wishlist operations
    fun toggleWishlist(productId: String) {
        val current = _wishlist.value.toMutableSet()
        if (current.contains(productId)) {
            current.remove(productId)
        } else {
            current.add(productId)
        }
        _wishlist.value = current
    }

    // Checkout and Order Placement
    fun placeOrder(deliveryAddress: String, paymentMethod: String): Order {
        val items = _cart.value
        val subtotal = items.sumOf { it.totalCost }
        val shipping = if (subtotal > 499) 0.0 else 50.0
        val total = subtotal + shipping

        val orderId = "ORD-${SimpleDateFormat("yyyyMMdd-HHmmss", Locale.getDefault()).format(Date())}"
        val user = _currentUser.value

        val newOrder = Order(
            id = orderId,
            userId = user?.id ?: "guest",
            userEmail = user?.email ?: "shopper@kariva.com",
            items = items,
            subtotal = subtotal,
            shipping = shipping,
            total = total,
            status = OrderStatus.ORDER_PLACED,
            placedAt = System.currentTimeMillis(),
            estimatedDelivery = "Expected delivery in 2-3 Days",
            trackingNumber = "KV-${(10000000..99999999).random()}",
            deliveryAddress = deliveryAddress.ifBlank { "Sector 14, Urban Estate, Gurugram" },
            paymentMethod = paymentMethod
        )

        // Decrement stock for ordered items
        items.forEach { cartItem ->
            val p = _products.value.find { it.id == cartItem.product.id }
            if (p != null) {
                val updatedStock = (p.stock - cartItem.quantity).coerceAtLeast(0)
                updateProduct(p.copy(stock = updatedStock))
            }
        }

        // Add to orders
        _orders.value = listOf(newOrder) + _orders.value
        clearCart()
        updateAnalytics()

        addNotification(
            title = "Order Confirmed: $orderId",
            message = "We have received your order of ₹${"%,.0f".format(total)}. Shikha has started handcrafting your woolen order.",
            type = "order"
        )

        scope.launch {
            try {
                firestore?.collection("orders")?.document(newOrder.id)?.set(newOrder)
            } catch (e: Exception) {
                Log.w("KarivaRepo", "Firestore placeOrder failed: ${e.message}")
            }
        }

        return newOrder
    }

    fun updateOrderStatus(orderId: String, newStatus: OrderStatus) {
        val updated = _orders.value.map {
            if (it.id == orderId) it.copy(status = newStatus) else it
        }
        _orders.value = updated
        updateAnalytics()

        val order = _orders.value.find { it.id == orderId }
        if (order != null) {
            addNotification(
                title = "Order Status: ${newStatus.label}",
                message = "Order ${order.id} has progressed to ${newStatus.label}.",
                type = "order"
            )
        }

        scope.launch {
            try {
                firestore?.collection("orders")?.document(orderId)?.update("status", newStatus.name)
            } catch (e: Exception) {
                Log.w("KarivaRepo", "Firestore updateOrderStatus failed: ${e.message}")
            }
        }
    }

    private fun addNotification(title: String, message: String, type: String) {
        val notif = NotificationItem(
            id = "notif_${System.currentTimeMillis()}_${(100..999).random()}",
            title = title,
            message = message,
            timestamp = System.currentTimeMillis(),
            type = type,
            isRead = false
        )
        _notifications.value = listOf(notif) + _notifications.value
    }

    fun markNotificationRead(notificationId: String) {
        _notifications.value = _notifications.value.map {
            if (it.id == notificationId) it.copy(isRead = true) else it
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: KarivaRepository? = null

        fun getInstance(context: Context): KarivaRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: KarivaRepository(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
