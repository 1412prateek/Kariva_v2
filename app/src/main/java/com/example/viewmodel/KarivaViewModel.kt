package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.KarivaRepository
import com.example.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class Screen {
    WELCOME,
    ONBOARDING,
    AUTH,
    HOME,
    CATEGORIES,
    PRODUCT_DETAIL,
    CART,
    WISHLIST,
    TRACK_ORDER,
    PROFILE,
    CREATOR_DASHBOARD,
    NOTIFICATIONS
}

class KarivaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = KarivaRepository.getInstance(application)

    // Current navigation state
    private val _currentScreen = MutableStateFlow(Screen.WELCOME)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val screenBackStack = mutableListOf<Screen>()

    // Selected product & order
    private val _selectedProduct = MutableStateFlow<Product?>(null)
    val selectedProduct: StateFlow<Product?> = _selectedProduct.asStateFlow()

    private val _selectedOrder = MutableStateFlow<Order?>(null)
    val selectedOrder: StateFlow<Order?> = _selectedOrder.asStateFlow()

    // Filters and search
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    // Auth screen mode (customer vs creator)
    private val _authRoleTarget = MutableStateFlow(UserRole.CUSTOMER)
    val authRoleTarget: StateFlow<UserRole> = _authRoleTarget.asStateFlow()

    // Data streams from repository
    val currentUser = repository.currentUser
    val products = repository.products
    val cart = repository.cart
    val wishlist = repository.wishlist
    val orders = repository.orders
    val notifications = repository.notifications
    val analytics = repository.analytics

    // UI Feedback
    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    // Dialog & Sheets
    private val _showCheckoutSheet = MutableStateFlow(false)
    val showCheckoutSheet: StateFlow<Boolean> = _showCheckoutSheet.asStateFlow()

    private val _showProductEditorDialog = MutableStateFlow<Product?>(null)
    val showProductEditorDialog: StateFlow<Product?> = _showProductEditorDialog.asStateFlow()

    // Filtered products
    val filteredProducts: StateFlow<List<Product>> = combine(
        products,
        searchQuery,
        selectedCategory
    ) { allProducts, query, category ->
        allProducts.filter { product ->
            val matchesCategory = (category == "All" || product.category.equals(category, ignoreCase = true))
            val matchesQuery = query.isBlank() || product.title.contains(query, ignoreCase = true) ||
                    product.description.contains(query, ignoreCase = true) ||
                    product.materials.contains(query, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadNotificationsCount: StateFlow<Int> = notifications.map { list ->
        list.count { !it.isRead }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val cartCount: StateFlow<Int> = cart.map { items ->
        items.sumOf { it.quantity }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val wishlistCount: StateFlow<Int> = wishlist.map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    init {
        // Automatically link latest order if available
        viewModelScope.launch {
            orders.collect { orderList ->
                if (_selectedOrder.value == null && orderList.isNotEmpty()) {
                    _selectedOrder.value = orderList.first()
                }
            }
        }
    }

    fun navigateTo(screen: Screen, addToBackStack: Boolean = true) {
        if (addToBackStack && _currentScreen.value != screen) {
            screenBackStack.add(_currentScreen.value)
        }
        _currentScreen.value = screen
    }

    fun navigateBack(): Boolean {
        if (screenBackStack.isNotEmpty()) {
            _currentScreen.value = screenBackStack.removeAt(screenBackStack.size - 1)
            return true
        }
        return false
    }

    fun setAuthTargetRole(role: UserRole) {
        _authRoleTarget.value = role
    }

    fun openProductDetail(product: Product) {
        _selectedProduct.value = product
        navigateTo(Screen.PRODUCT_DETAIL)
    }

    fun openOrderTrack(order: Order) {
        _selectedOrder.value = order
        navigateTo(Screen.TRACK_ORDER)
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(cat: String) {
        _selectedCategory.value = cat
    }

    fun showToast(msg: String) {
        _toastMessage.value = msg
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    // Auth actions
    fun login(email: String, pass: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val result = repository.signIn(email, pass)
            result.onSuccess { profile ->
                showToast("Welcome to Kariva, ${profile.displayName}!")
                if (profile.role == UserRole.CREATOR) {
                    navigateTo(Screen.CREATOR_DASHBOARD)
                } else {
                    navigateTo(Screen.HOME)
                }
                onSuccess()
            }.onFailure {
                onError(it.localizedMessage ?: "Authentication failed")
            }
        }
    }

    fun signup(name: String, email: String, pass: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val result = repository.signUp(name, email, pass)
            result.onSuccess { profile ->
                showToast("Account created successfully!")
                if (profile.role == UserRole.CREATOR) {
                    navigateTo(Screen.CREATOR_DASHBOARD)
                } else {
                    navigateTo(Screen.HOME)
                }
                onSuccess()
            }.onFailure {
                onError(it.localizedMessage ?: "Signup failed")
            }
        }
    }

    fun logout() {
        repository.signOut()
        showToast("Signed out")
        navigateTo(Screen.AUTH)
    }

    fun quickRoleSwitch(role: UserRole) {
        repository.switchRoleQuickDemo(role)
        showToast("Switched to ${if (role == UserRole.CREATOR) "Creator (Shikha)" else "Shopper"} role")
        if (role == UserRole.CREATOR) {
            navigateTo(Screen.CREATOR_DASHBOARD)
        } else {
            navigateTo(Screen.HOME)
        }
    }

    // Cart actions
    fun addToCart(product: Product, tier: PricingTier, quantity: Int = 1) {
        repository.addToCart(product, tier, quantity)
        showToast("Added ${product.title} to your bag")
    }

    fun updateCartQuantity(cartItemId: String, qty: Int) {
        repository.updateCartItemQuantity(cartItemId, qty)
    }

    fun removeFromCart(cartItemId: String) {
        repository.removeFromCart(cartItemId)
    }

    fun clearCart() {
        repository.clearCart()
    }

    fun setCheckoutSheetVisible(visible: Boolean) {
        _showCheckoutSheet.value = visible
    }

    fun completeCheckout(deliveryAddress: String, paymentMethod: String) {
        _showCheckoutSheet.value = false
        val order = repository.placeOrder(deliveryAddress, paymentMethod)
        _selectedOrder.value = order
        showToast("Order placed successfully! Tracking ${order.trackingNumber}")
        navigateTo(Screen.TRACK_ORDER)
    }

    // Wishlist actions
    fun toggleWishlist(product: Product) {
        repository.toggleWishlist(product.id)
        val isNowWishlisted = !repository.wishlist.value.contains(product.id)
        showToast(if (isNowWishlisted) "Saved to Wishlist" else "Removed from Wishlist")
    }

    // Creator actions
    fun openProductEditor(product: Product?) {
        _showProductEditorDialog.value = product ?: Product(
            id = "",
            title = "",
            category = "Necklaces",
            price = 5000.0,
            stock = 10,
            description = "",
            materials = "18K Gold"
        )
    }

    fun closeProductEditor() {
        _showProductEditorDialog.value = null
    }

    fun saveProduct(product: Product) {
        if (product.id.isBlank()) {
            repository.addProduct(product)
            showToast("Product created successfully")
        } else {
            repository.updateProduct(product)
            showToast("Product updated successfully")
        }
        closeProductEditor()
    }

    fun deleteProduct(productId: String) {
        repository.deleteProduct(productId)
        showToast("Product removed from catalog")
    }

    fun updateStock(productId: String, newStock: Int) {
        repository.updateStock(productId, newStock)
        showToast("Stock updated")
    }

    fun updateOrderStatus(orderId: String, newStatus: OrderStatus) {
        repository.updateOrderStatus(orderId, newStatus)
        showToast("Order status updated to ${newStatus.label}")
    }

    fun markNotificationRead(id: String) {
        repository.markNotificationRead(id)
    }
}
