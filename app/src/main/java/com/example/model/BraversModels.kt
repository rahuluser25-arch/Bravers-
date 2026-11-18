package com.example.model

import androidx.annotation.DrawableRes

enum class BraversCategory(val label: String, val shortDesc: String) {
    ALL("All", "Complete Bravers collection"),
    MENS("Mens", "Drop-shoulder & oversized streetwear"),
    WOMENS("Womens", "Contemporary daily & statement wear"),
    TEES_BOTTOM("Tees & Bottom", "Everyday tees, cargos & joggers"),
    KURTI("Kurti", "Modern cotton-linen fusion kurtis"),
    TOP_TANK("Top Tank", "Ribbed & cropped sleeveless cuts")
}

enum class SortOption(val label: String) {
    FEATURED("Featured"),
    PRICE_LOW_HIGH("Price: Low to High"),
    PRICE_HIGH_LOW("Price: High to Low"),
    TOP_RATED("Top Rated")
}

enum class ThemePreference(val label: String) {
    SYSTEM("System"),
    LIGHT("Bone Light"),
    DARK("Obsidian Dark")
}

data class ProductColor(
    val name: String,
    val hex: Long
)

data class Product(
    val id: String,
    val title: String,
    val subtitle: String,
    val category: BraversCategory,
    val priceInr: Int,
    val mrpInr: Int,
    val badge: String?,
    val description: String,
    val fabricSpec: String,
    val fitType: String,
    @DrawableRes val imageRes: Int,
    val sizes: List<String>,
    val colors: List<ProductColor>,
    val isFeatured: Boolean = false,
    val rating: Double = 4.8,
    val reviewCount: Int = 42
) {
    val discountPercent: Int
        get() = if (mrpInr > priceInr) (((mrpInr - priceInr) * 100) / mrpInr) else 0
}

data class CartItem(
    val product: Product,
    val selectedSize: String,
    val selectedColorName: String,
    val quantity: Int
) {
    val lineTotalInr: Int
        get() = product.priceInr * quantity
}

data class OrderRecord(
    val orderId: String,
    val dateLabel: String,
    val itemSummary: String,
    val totalInr: Int,
    val paymentMethod: String,
    val status: String,
    val deliveryAddress: String
)

enum class CheckoutPaymentMethod(val label: String) {
    UPI("UPI (GPay / PhonePe)"),
    CARD("Credit / Debit Card"),
    COD("Cash on Delivery")
}
