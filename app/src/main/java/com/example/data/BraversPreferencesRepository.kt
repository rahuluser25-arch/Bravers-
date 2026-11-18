package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.model.CartItem
import com.example.model.OrderRecord
import com.example.model.ThemePreference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.braversDataStore: DataStore<Preferences> by preferencesDataStore(name = "bravers_store_prefs")

class BraversPreferencesRepository(private val context: Context) {

    private object Keys {
        val WISHLIST_IDS = stringSetPreferencesKey("wishlist_ids")
        val CART_ENTRIES = stringPreferencesKey("cart_entries")
        val PROMO_CODE = stringPreferencesKey("promo_code")
        val ORDERS = stringPreferencesKey("orders_history")
        val THEME_PREF = stringPreferencesKey("theme_preference")
    }

    val wishlistIdsFlow: Flow<Set<String>> = context.braversDataStore.data.map { prefs ->
        prefs[Keys.WISHLIST_IDS] ?: setOf("brv-001", "brv-003")
    }

    val cartItemsFlow: Flow<List<CartItem>> = context.braversDataStore.data.map { prefs ->
        decodeCart(prefs[Keys.CART_ENTRIES].orEmpty())
    }

    val promoCodeFlow: Flow<String> = context.braversDataStore.data.map { prefs ->
        prefs[Keys.PROMO_CODE].orEmpty()
    }

    val ordersFlow: Flow<List<OrderRecord>> = context.braversDataStore.data.map { prefs ->
        decodeOrders(prefs[Keys.ORDERS].orEmpty())
    }

    val themePreferenceFlow: Flow<ThemePreference> = context.braversDataStore.data.map { prefs ->
        val raw = prefs[Keys.THEME_PREF] ?: ThemePreference.SYSTEM.name
        ThemePreference.entries.firstOrNull { it.name == raw } ?: ThemePreference.SYSTEM
    }

    suspend fun toggleWishlist(productId: String) {
        context.braversDataStore.edit { prefs ->
            val current = prefs[Keys.WISHLIST_IDS] ?: setOf("brv-001", "brv-003")
            prefs[Keys.WISHLIST_IDS] = if (productId in current) current - productId else current + productId
        }
    }

    suspend fun saveCart(items: List<CartItem>) {
        context.braversDataStore.edit { prefs ->
            prefs[Keys.CART_ENTRIES] = encodeCart(items)
        }
    }

    suspend fun savePromoCode(code: String) {
        context.braversDataStore.edit { prefs ->
            prefs[Keys.PROMO_CODE] = code
        }
    }

    suspend fun addOrder(order: OrderRecord) {
        context.braversDataStore.edit { prefs ->
            val existing = decodeOrders(prefs[Keys.ORDERS].orEmpty())
            prefs[Keys.ORDERS] = encodeOrders(listOf(order) + existing)
        }
    }

    suspend fun setThemePreference(preference: ThemePreference) {
        context.braversDataStore.edit { prefs ->
            prefs[Keys.THEME_PREF] = preference.name
        }
    }

    private fun encodeCart(items: List<CartItem>): String {
        return items.joinToString(separator = "\n") { item ->
            listOf(
                item.product.id,
                sanitize(item.selectedSize),
                sanitize(item.selectedColorName),
                item.quantity.toString()
            ).joinToString(separator = "|")
        }
    }

    private fun decodeCart(raw: String): List<CartItem> {
        if (raw.isBlank()) return emptyList()
        return raw.lines().mapNotNull { line ->
            val parts = line.split("|")
            if (parts.size != 4) return@mapNotNull null
            val product = BraversCatalog.getById(parts[0]) ?: return@mapNotNull null
            val qty = parts[3].toIntOrNull()?.coerceIn(1, 20) ?: return@mapNotNull null
            CartItem(
                product = product,
                selectedSize = parts[1],
                selectedColorName = parts[2],
                quantity = qty
            )
        }
    }

    private fun encodeOrders(orders: List<OrderRecord>): String {
        return orders.joinToString(separator = "\n") { order ->
            listOf(
                sanitize(order.orderId),
                sanitize(order.dateLabel),
                sanitize(order.itemSummary),
                order.totalInr.toString(),
                sanitize(order.paymentMethod),
                sanitize(order.status),
                sanitize(order.deliveryAddress)
            ).joinToString(separator = "|")
        }
    }

    private fun decodeOrders(raw: String): List<OrderRecord> {
        if (raw.isBlank()) return emptyList()
        return raw.lines().mapNotNull { line ->
            val parts = line.split("|")
            if (parts.size != 7) return@mapNotNull null
            val total = parts[3].toIntOrNull() ?: return@mapNotNull null
            OrderRecord(
                orderId = parts[0],
                dateLabel = parts[1],
                itemSummary = parts[2],
                totalInr = total,
                paymentMethod = parts[4],
                status = parts[5],
                deliveryAddress = parts[6]
            )
        }
    }

    private fun sanitize(input: String): String =
        input.replace("|", "/").replace("\n", " ").trim()
}
