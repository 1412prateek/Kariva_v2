package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.UserRole
import com.example.ui.components.KarivaBottomNavigation
import com.example.ui.screens.*
import com.example.ui.theme.KarivaCreamBg
import com.example.ui.theme.KarivaTerracotta
import com.example.ui.theme.KarivaTerracottaContainer
import com.example.ui.theme.KarivaTheme
import com.example.viewmodel.KarivaViewModel
import com.example.viewmodel.Screen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KarivaTheme {
                KarivaApp()
            }
        }
    }
}

@Composable
fun KarivaApp(viewModel: KarivaViewModel = viewModel()) {
    val context = LocalContext.current
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val filteredProducts by viewModel.filteredProducts.collectAsStateWithLifecycle()
    val allProducts by viewModel.products.collectAsStateWithLifecycle()
    val cart by viewModel.cart.collectAsStateWithLifecycle()
    val wishlist by viewModel.wishlist.collectAsStateWithLifecycle()
    val orders by viewModel.orders.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val analytics by viewModel.analytics.collectAsStateWithLifecycle()

    val selectedProduct by viewModel.selectedProduct.collectAsStateWithLifecycle()
    val selectedOrder by viewModel.selectedOrder.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val cartCount by viewModel.cartCount.collectAsStateWithLifecycle()
    val wishlistCount by viewModel.wishlistCount.collectAsStateWithLifecycle()
    val unreadNotifCount by viewModel.unreadNotificationsCount.collectAsStateWithLifecycle()
    val productEditorTarget by viewModel.showProductEditorDialog.collectAsStateWithLifecycle()

    val toastMsg by viewModel.toastMessage.collectAsStateWithLifecycle()

    // Handle toast messages
    LaunchedEffect(toastMsg) {
        toastMsg?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
    }

    // Hardware and gesture BackHandler
    BackHandler(enabled = currentScreen != Screen.WELCOME && currentScreen != Screen.HOME) {
        if (!viewModel.navigateBack()) {
            viewModel.navigateTo(Screen.HOME, addToBackStack = false)
        }
    }

    // Responsive container supporting both phone and wide screens
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEFE8DF)),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .widthIn(max = 600.dp)
                .fillMaxWidth()
                .background(KarivaCreamBg)
        ) {
            Scaffold(
                topBar = {
                    // Show a discrete banner if Creator is previewing the customer storefront
                    if (currentUser?.role == UserRole.CREATOR && currentScreen in listOf(
                            Screen.HOME, Screen.CATEGORIES, Screen.PRODUCT_DETAIL, Screen.CART, Screen.WISHLIST
                        )
                    ) {
                        Surface(
                            color = KarivaTerracottaContainer,
                            modifier = Modifier
                                .fillMaxWidth()
                                .statusBarsPadding()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "👑 Creator Preview Mode",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = KarivaTerracotta
                                )
                                Text(
                                    text = "Back to Studio →",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = KarivaTerracotta,
                                    modifier = Modifier.clickable {
                                        viewModel.navigateTo(Screen.CREATOR_DASHBOARD)
                                    }
                                )
                            }
                        }
                    }
                },
                bottomBar = {
                    val showBottomNav = currentScreen in listOf(
                        Screen.HOME,
                        Screen.CATEGORIES,
                        Screen.WISHLIST,
                        Screen.CART,
                        Screen.PROFILE
                    )
                    if (showBottomNav) {
                        KarivaBottomNavigation(
                            currentScreen = currentScreen,
                            onNavigate = { screen -> viewModel.navigateTo(screen) },
                            cartBadgeCount = cartCount,
                            wishlistBadgeCount = wishlistCount
                        )
                    }
                },
                containerColor = KarivaCreamBg,
                contentWindowInsets = WindowInsets(0, 0, 0, 0)
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    AnimatedContent(
                        targetState = currentScreen,
                        transitionSpec = {
                            fadeIn() togetherWith fadeOut()
                        },
                        label = "ScreenTransition"
                    ) { targetScreen ->
                        when (targetScreen) {
                            Screen.WELCOME -> {
                                WelcomeScreen(
                                    onGetStarted = { viewModel.navigateTo(Screen.ONBOARDING) },
                                    onSignIn = {
                                        viewModel.setAuthTargetRole(UserRole.CUSTOMER)
                                        viewModel.navigateTo(Screen.AUTH)
                                    }
                                )
                            }

                            Screen.ONBOARDING -> {
                                OnboardingScreen(
                                    onGetStarted = {
                                        viewModel.setAuthTargetRole(UserRole.CUSTOMER)
                                        viewModel.navigateTo(Screen.AUTH)
                                    },
                                    onLogin = {
                                        viewModel.setAuthTargetRole(UserRole.CUSTOMER)
                                        viewModel.navigateTo(Screen.AUTH)
                                    }
                                )
                            }

                            Screen.AUTH -> {
                                AuthScreen(
                                    initialIsCreator = viewModel.authRoleTarget.value == UserRole.CREATOR,
                                    onCustomerLogin = { email, pass, onError ->
                                        viewModel.login(
                                            email, pass,
                                            onSuccess = {},
                                            onError = onError
                                        )
                                    },
                                    onNavigateToCustomerSignUp = { email ->
                                        viewModel.navigateToCustomerSignup(email)
                                    },
                                    onCreatorLogin = { email, pass, onError ->
                                        if (email.trim().equals("shikha@kariva.com", ignoreCase = true) && pass == "Shikha@1810") {
                                            viewModel.login(
                                                email, pass,
                                                onSuccess = {
                                                    viewModel.navigateTo(Screen.CREATOR_DASHBOARD)
                                                },
                                                onError = onError
                                            )
                                        } else {
                                            onError("Invalid creator credentials. Use authorized creator account.")
                                        }
                                    },
                                    onBack = { viewModel.navigateTo(Screen.WELCOME) }
                                )
                            }

                            Screen.CUSTOMER_SIGNUP_DETAILS -> {
                                val prefilledEmail by viewModel.prefilledSignupEmail.collectAsStateWithLifecycle()
                                CustomerSignupDetailsScreen(
                                    initialEmail = prefilledEmail,
                                    isExistingUser = currentUser != null && currentUser?.email?.isNotBlank() == true,
                                    existingDetails = currentUser?.customerDetails,
                                    onBack = {
                                        if (!viewModel.navigateBack()) {
                                            viewModel.navigateTo(Screen.AUTH)
                                        }
                                    },
                                    onSubmit = { details, password, onError ->
                                        if (currentUser != null && currentUser?.email?.isNotBlank() == true) {
                                            viewModel.saveCustomerDetails(
                                                details = details.copy(
                                                    customer_uuid = currentUser?.id ?: details.customer_uuid,
                                                    customer_email = currentUser?.email ?: details.customer_email
                                                ),
                                                onSuccess = {
                                                    viewModel.navigateTo(Screen.HOME)
                                                },
                                                onError = onError
                                            )
                                        } else {
                                            viewModel.completeCustomerRegistration(
                                                details = details,
                                                pass = password,
                                                onSuccess = {},
                                                onError = onError
                                            )
                                        }
                                    }
                                )
                            }

                            Screen.HOME -> {
                                HomeScreen(
                                    products = filteredProducts,
                                    wishlist = wishlist,
                                    cartCount = cartCount,
                                    searchQuery = searchQuery,
                                    selectedCategory = selectedCategory,
                                    onSearchChange = { viewModel.setSearchQuery(it) },
                                    onCategorySelect = { viewModel.setSelectedCategory(it) },
                                    onProductClick = { viewModel.openProductDetail(it) },
                                    onWishlistToggle = { viewModel.toggleWishlist(it) },
                                    onCartClick = { viewModel.navigateTo(Screen.CART) },
                                    onWishlistClick = { viewModel.navigateTo(Screen.WISHLIST) },
                                    onSeeAllCategories = { viewModel.navigateTo(Screen.CATEGORIES) },
                                    onMenuClick = { viewModel.navigateTo(Screen.CATEGORIES) }
                                )
                            }

                            Screen.CATEGORIES -> {
                                CategoriesScreen(
                                    onCategoryClick = { cat ->
                                        viewModel.setSelectedCategory(cat)
                                        viewModel.navigateTo(Screen.HOME)
                                    },
                                    onCartClick = { viewModel.navigateTo(Screen.CART) },
                                    onWishlistClick = { viewModel.navigateTo(Screen.WISHLIST) },
                                    cartCount = cartCount,
                                    wishlistCount = wishlistCount
                                )
                            }

                            Screen.PRODUCT_DETAIL -> {
                                ProductDetailScreen(
                                    product = selectedProduct,
                                    isWishlisted = selectedProduct?.let { wishlist.contains(it.id) } == true,
                                    onBack = { viewModel.navigateBack() },
                                    onWishlistToggle = {
                                        selectedProduct?.let { viewModel.toggleWishlist(it) }
                                    },
                                    onAddToCart = { product, tier, qty ->
                                        viewModel.addToCart(product, tier, qty)
                                    }
                                )
                            }

                            Screen.CART -> {
                                CartScreen(
                                    cartItems = cart,
                                    onBack = { viewModel.navigateBack() },
                                    onQuantityChange = { id, qty -> viewModel.updateCartQuantity(id, qty) },
                                    onRemoveItem = { id -> viewModel.removeFromCart(id) },
                                    onClearCart = { viewModel.clearCart() },
                                    onProceedToCheckout = { address, payment ->
                                        viewModel.completeCheckout(address, payment)
                                    },
                                    onShopNow = { viewModel.navigateTo(Screen.HOME) }
                                )
                            }

                            Screen.WISHLIST -> {
                                val wishlistedProducts = allProducts.filter { wishlist.contains(it.id) }
                                WishlistScreen(
                                    wishlistedProducts = wishlistedProducts,
                                    onBack = { viewModel.navigateBack() },
                                    onProductClick = { viewModel.openProductDetail(it) },
                                    onRemoveFromWishlist = { viewModel.toggleWishlist(it) },
                                    onAddToCart = { product ->
                                        val tier = product.pricingTiers.firstOrNull()
                                            ?: com.example.model.PricingTier("default", "Standard", product.price)
                                        viewModel.addToCart(product, tier, 1)
                                    },
                                    onExplore = { viewModel.navigateTo(Screen.HOME) }
                                )
                            }

                            Screen.TRACK_ORDER -> {
                                TrackOrderScreen(
                                    order = selectedOrder ?: orders.firstOrNull(),
                                    onBack = { viewModel.navigateBack() }
                                )
                            }

                            Screen.PROFILE -> {
                                val lowStockList = allProducts.filter { it.isLowStock }
                                ProfileScreen(
                                    user = currentUser,
                                    latestOrder = orders.firstOrNull(),
                                    lowStockProducts = lowStockList,
                                    unreadNotifCount = unreadNotifCount,
                                    onOrdersClick = {
                                        orders.firstOrNull()?.let { viewModel.openOrderTrack(it) }
                                    },
                                    onWishlistClick = { viewModel.navigateTo(Screen.WISHLIST) },
                                    onNotificationsClick = { viewModel.navigateTo(Screen.NOTIFICATIONS) },
                                    onTrackOrderClick = { viewModel.openOrderTrack(it) },
                                    onEditAddressClick = { viewModel.navigateToCustomerSignup(currentUser?.email ?: "") },
                                    onLogout = { viewModel.logout() }
                                )
                            }

                            Screen.CREATOR_DASHBOARD -> {
                                CreatorDashboardScreen(
                                    analytics = analytics,
                                    products = allProducts,
                                    orders = orders,
                                    onAddProduct = { viewModel.openProductEditor(null) },
                                    onEditProduct = { viewModel.openProductEditor(it) },
                                    onDeleteProduct = { viewModel.deleteProduct(it) },
                                    onUpdateStock = { id, stock -> viewModel.updateStock(id, stock) },
                                    onUpdateOrderStatus = { id, status -> viewModel.updateOrderStatus(id, status) },
                                    onSwitchToShopperView = {
                                        viewModel.navigateTo(Screen.HOME)
                                    },
                                    onLogout = { viewModel.logout() }
                                )
                            }

                            Screen.NOTIFICATIONS -> {
                                NotificationScreen(
                                    notifications = notifications,
                                    onBack = { viewModel.navigateBack() },
                                    onMarkAsRead = { viewModel.markNotificationRead(it) }
                                )
                            }
                        }
                    }
                }
            }

            // Creator Product Editor Modal
            if (productEditorTarget != null) {
                ProductEditorDialog(
                    initialProduct = productEditorTarget,
                    onDismiss = { viewModel.closeProductEditor() },
                    onSave = { product -> viewModel.saveProduct(product) }
                )
            }
        }
    }
}
