package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.BraversCatalog
import com.example.data.BraversPreferencesRepository
import com.example.model.BraversCategory
import com.example.model.CartItem
import com.example.model.CheckoutPaymentMethod
import com.example.model.OrderRecord
import com.example.model.Product
import com.example.model.SortOption
import com.example.model.ThemePreference
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

data class BraversUiState(
    val allProducts: List<Product> = BraversCatalog.products,
    val selectedCategory: BraversCategory = BraversCategory.ALL,
    val searchQuery: String = "",
    val sortOption: SortOption = SortOption.FEATURED,
    val wishlistIds: Set<String> = emptySet(),
    val cartItems: List<CartItem> = emptyList(),
    val appliedPromoCode: String = "",
    val promoMessage: String? = null,
    val promoIsError: Boolean = false,
    val orders: List<OrderRecord> = emptyList(),
    val latestOrderConfirmation: OrderRecord? = null,
    val themePreference: ThemePreference = ThemePreference.SYSTEM,
    val snackbarMessage: String? = null
) {
    val filteredProducts: List<Product>
        get() {
            val base = allProducts.filter { product ->
                val matchesCategory = when (selectedCategory) {
                    BraversCategory.ALL -> true
                    BraversCategory.WOMENS -> product.category == BraversCategory.WOMENS ||
                        product.category == BraversCategory.KURTI ||
                        product.category == BraversCategory.TOP_TANK
                    else -> product.category == selectedCategory
                }
                val q = searchQuery.trim().lowercase()
                val matchesSearch = q.isEmpty() ||
                    product.title.lowercase().contains(q) ||
                    product.subtitle.lowercase().contains(q) ||
                    product.category.label.lowercase().contains(q)
                matchesCategory && matchesSearch
            }
            return when (sortOption) {
                SortOption.FEATURED -> base.sortedByDescending { it.isFeatured }
                SortOption.PRICE_LOW_HIGH -> base.sortedBy { it.priceInr }
                SortOption.PRICE_HIGH_LOW -> base.sortedByDescending { it.priceInr }
                SortOption.TOP_RATED -> base.sortedByDescending { it.rating }
            }
        }

    val wishlistProducts: List<Product>
        get() = allProducts.filter { it.id in wishlistIds }

    val cartItemCount: Int
        get() = cartItems.sumOf { it.quantity }

    val subtotalInr: Int
        get() = cartItems.sumOf { it.lineTotalInr }

    val discountInr: Int
        get() = when (appliedPromoCode.uppercase()) {
            BraversCatalog.PROMO_CODE_OFF20 -> if (subtotalInr >= 499) (subtotalInr * 0.20).roundToInt() else 0
            BraversCatalog.PROMO_CODE_BRAVERS10 -> (subtotalInr * 0.10).roundToInt()
            else -> 0
        }

    val discountedSubtotalInr: Int
        get() = (subtotalInr - discountInr).coerceAtLeast(0)

    val shippingInr: Int
        get() = when {
            cartItems.isEmpty() -> 0
            discountedSubtotalInr >= BraversCatalog.FREE_SHIPPING_THRESHOLD_INR -> 0
            else -> BraversCatalog.STANDARD_SHIPPING_INR
        }

    val grandTotalInr: Int
        get() = discountedSubtotalInr + shippingInr

    val freeShippingRemainingInr: Int
        get() = (BraversCatalog.FREE_SHIPPING_THRESHOLD_INR - discountedSubtotalInr).coerceAtLeast(0)
}

private data class TransientShopUi(
    val selectedCategory: BraversCategory = BraversCategory.ALL,
    val searchQuery: String = "",
    val sortOption: SortOption = SortOption.FEATURED,
    val promoMessage: String? = null,
    val promoIsError: Boolean = false,
    val latestOrderConfirmation: OrderRecord? = null,
    val snackbarMessage: String? = null
)

class BraversViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = BraversPreferencesRepository(application.applicationContext)
    private val transientState = MutableStateFlow(TransientShopUi())

    val uiState: StateFlow<BraversUiState> = combine(
        transientState,
        repository.wishlistIdsFlow,
        repository.cartItemsFlow,
        repository.promoCodeFlow,
        combine(repository.ordersFlow, repository.themePreferenceFlow) { orders, theme ->
            orders to theme
        }
    ) { transient, wishlist, cart, promoCode, ordersAndTheme ->
        BraversUiState(
            selectedCategory = transient.selectedCategory,
            searchQuery = transient.searchQuery,
            sortOption = transient.sortOption,
            wishlistIds = wishlist,
            cartItems = cart,
            appliedPromoCode = promoCode,
            promoMessage = transient.promoMessage,
            promoIsError = transient.promoIsError,
            orders = ordersAndTheme.first,
            latestOrderConfirmation = transient.latestOrderConfirmation,
            themePreference = ordersAndTheme.second,
            snackbarMessage = transient.snackbarMessage
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = BraversUiState()
    )

    fun selectCategory(category: BraversCategory) {
        transientState.update { it.copy(selectedCategory = category) }
    }

    fun updateSearchQuery(query: String) {
        transientState.update { it.copy(searchQuery = query) }
    }

    fun selectSortOption(option: SortOption) {
        transientState.update { it.copy(sortOption = option) }
    }

    fun toggleWishlist(productId: String) {
        viewModelScope.launch {
            val wasSaved = productId in uiState.value.wishlistIds
            repository.toggleWishlist(productId)
            showToast(if (wasSaved) "Removed from Saved" else "Saved to Wishlist")
        }
    }

    fun addToBag(product: Product, size: String, colorName: String) {
        viewModelScope.launch {
            val current = uiState.value.cartItems.toMutableList()
            val index = current.indexOfFirst {
                it.product.id == product.id &&
                    it.selectedSize == size &&
                    it.selectedColorName == colorName
            }
            if (index >= 0) {
                val existing = current[index]
                current[index] = existing.copy(quantity = (existing.quantity + 1).coerceAtMost(20))
            } else {
                current.add(
                    CartItem(
                        product = product,
                        selectedSize = size,
                        selectedColorName = colorName,
                        quantity = 1
                    )
                )
            }
            repository.saveCart(current)
            showToast("Added ${product.title} ($size) to Bag")
        }
    }

    fun updateCartQuantity(item: CartItem, delta: Int) {
        viewModelScope.launch {
            val current = uiState.value.cartItems.toMutableList()
            val index = current.indexOfFirst {
                it.product.id == item.product.id &&
                    it.selectedSize == item.selectedSize &&
                    it.selectedColorName == item.selectedColorName
            }
            if (index >= 0) {
                val newQty = current[index].quantity + delta
                if (newQty <= 0) {
                    current.removeAt(index)
                } else {
                    current[index] = current[index].copy(quantity = newQty.coerceAtMost(20))
                }
                repository.saveCart(current)
            }
        }
    }

    fun removeFromBag(item: CartItem) {
        viewModelScope.launch {
            val updated = uiState.value.cartItems.filterNot {
                it.product.id == item.product.id &&
                    it.selectedSize == item.selectedSize &&
                    it.selectedColorName == item.selectedColorName
            }
            repository.saveCart(updated)
            showToast("Item removed from Bag")
        }
    }

    fun applyPromoCode(rawCode: String) {
        val code = rawCode.trim().uppercase()
        viewModelScope.launch {
            val subtotal = uiState.value.subtotalInr
            when (code) {
                BraversCatalog.PROMO_CODE_OFF20 -> {
                    if (subtotal < 499) {
                        transientState.update {
                            it.copy(
                                promoMessage = "OFF20 requires orders of ₹499 or more.",
                                promoIsError = true
                            )
                        }
                    } else {
                        repository.savePromoCode(code)
                        transientState.update {
                            it.copy(
                                promoMessage = "OFF20 applied — 20% instant savings unlocked!",
                                promoIsError = false
                            )
                        }
                    }
                }
                BraversCatalog.PROMO_CODE_BRAVERS10 -> {
                    repository.savePromoCode(code)
                    transientState.update {
                        it.copy(
                            promoMessage = "BRAVERS10 applied — 10% instant savings unlocked!",
                            promoIsError = false
                        )
                    }
                }
                "" -> {
                    repository.savePromoCode("")
                    transientState.update {
                        it.copy(promoMessage = null, promoIsError = false)
                    }
                }
                else -> {
                    transientState.update {
                        it.copy(
                            promoMessage = "Invalid code. Try OFF20 (orders ₹499+) or BRAVERS10.",
                            promoIsError = true
                        )
                    }
                }
            }
        }
    }

    fun clearPromoCode() {
        viewModelScope.launch {
            repository.savePromoCode("")
            transientState.update { it.copy(promoMessage = null, promoIsError = false) }
        }
    }

    fun placeOrder(
        customerName: String,
        phone: String,
        addressLine: String,
        cityAndPin: String,
        paymentMethod: CheckoutPaymentMethod
    ) {
        val snapshot = uiState.value
        if (snapshot.cartItems.isEmpty()) return

        viewModelScope.launch {
            val timestamp = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date())
            val orderNum = "BRV-${(100000..999999).random()}"
            val summary = snapshot.cartItems.joinToString(", ") {
                "${it.quantity}× ${it.product.title} (${it.selectedSize})"
            }
            val newOrder = OrderRecord(
                orderId = orderNum,
                dateLabel = timestamp,
                itemSummary = summary,
                totalInr = snapshot.grandTotalInr,
                paymentMethod = paymentMethod.label,
                status = "Confirmed • Dispatching via Bravers Express",
                deliveryAddress = "${customerName.trim()}, ${phone.trim()} • ${addressLine.trim()}, ${cityAndPin.trim()}"
            )
            repository.addOrder(newOrder)
            repository.saveCart(emptyList())
            repository.savePromoCode("")
            transientState.update {
                it.copy(
                    latestOrderConfirmation = newOrder,
                    promoMessage = null,
                    promoIsError = false
                )
            }
        }
    }

    fun dismissOrderConfirmation() {
        transientState.update { it.copy(latestOrderConfirmation = null) }
    }

    fun cycleThemePreference() {
        val current = uiState.value.themePreference
        val next = when (current) {
            ThemePreference.SYSTEM -> ThemePreference.DARK
            ThemePreference.DARK -> ThemePreference.LIGHT
            ThemePreference.LIGHT -> ThemePreference.SYSTEM
        }
        viewModelScope.launch {
            repository.setThemePreference(next)
            showToast("Theme: ${next.label}")
        }
    }

    fun clearSnackbar() {
        transientState.update { it.copy(snackbarMessage = null) }
    }

    private fun showToast(message: String) {
        transientState.update { it.copy(snackbarMessage = message) }
    }
}
