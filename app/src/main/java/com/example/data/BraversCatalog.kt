package com.example.data

import com.example.R
import com.example.model.BraversCategory
import com.example.model.Product
import com.example.model.ProductColor

object BraversCatalog {

    const val OFFICIAL_SITE_URL = "https://bravers.in"
    const val PROMO_CODE_OFF20 = "OFF20"
    const val PROMO_CODE_BRAVERS10 = "BRAVERS10"
    const val FREE_SHIPPING_THRESHOLD_INR = 999
    const val STANDARD_SHIPPING_INR = 79

    val products: List<Product> = listOf(
        Product(
            id = "brv-001",
            title = "In God We Trust — Oversized Tee",
            subtitle = "Signature Drop-Shoulder Edition",
            category = BraversCategory.MENS,
            priceInr = 1000,
            mrpInr = 1500,
            badge = "BESTSELLER",
            description = "Our flagship Bravers oversized silhouette engineered in heavyweight combed cotton. Features a clean chest emblem and bold back typography designed for all-day Indian urban comfort.",
            fabricSpec = "240 GSM Combed French Terry Cotton",
            fitType = "Drop-Shoulder Oversized",
            imageRes = R.drawable.img_product_mens_tee,
            sizes = listOf("S", "M", "L", "XL", "XXL"),
            colors = listOf(
                ProductColor("Obsidian Black", 0xFF161618),
                ProductColor("Warm Bone", 0xFFEFECE5),
                ProductColor("Charcoal Wash", 0xFF36363B)
            ),
            isFeatured = true,
            rating = 4.9,
            reviewCount = 148
        ),
        Product(
            id = "brv-002",
            title = "Bravers Crest Drop-Shoulder Tee",
            subtitle = "Heavyweight Streetwear Cut",
            category = BraversCategory.MENS,
            priceInr = 899,
            mrpInr = 1299,
            badge = "NEW DROP",
            description = "Relaxed boxy drape with ribbed collar retention and crisp puff-print Bravers insignia. Built to pair effortlessly with wide cargos or denim.",
            fabricSpec = "220 GSM Bio-Washed Jersey",
            fitType = "Relaxed Drop-Shoulder",
            imageRes = R.drawable.img_hero_bravers,
            sizes = listOf("S", "M", "L", "XL", "XXL"),
            colors = listOf(
                ProductColor("Jet Black", 0xFF121212),
                ProductColor("Vermilion", 0xFFD93829)
            ),
            isFeatured = true,
            rating = 4.8,
            reviewCount = 94
        ),
        Product(
            id = "brv-003",
            title = "Terracotta Handloom Fusion Kurti",
            subtitle = "Contemporary Everyday Ethnic",
            category = BraversCategory.KURTI,
            priceInr = 1199,
            mrpInr = 1699,
            badge = "TRENDING",
            description = "Breathable cotton-linen weave tailored into a sharp modern kurti silhouette with subtle woven texture and clean side slits for effortless movement.",
            fabricSpec = "100% Breathable Cotton-Linen Blend",
            fitType = "Tailored Straight Cut",
            imageRes = R.drawable.img_product_kurti,
            sizes = listOf("XS", "S", "M", "L", "XL"),
            colors = listOf(
                ProductColor("Terracotta Crimson", 0xFFC24634),
                ProductColor("Ivory Cream", 0xFFF3EFE6)
            ),
            isFeatured = true,
            rating = 4.9,
            reviewCount = 112
        ),
        Product(
            id = "brv-004",
            title = "Bone Ribbed Sculpt Top Tank",
            subtitle = "Minimal Sleeveless Essential",
            category = BraversCategory.TOP_TANK,
            priceInr = 549,
            mrpInr = 799,
            badge = "ESSENTIAL",
            description = "Soft-stretch ribbed cotton tank designed for warm weather layering or solo styling. Holds shape wash after wash with clean bound edges.",
            fabricSpec = "210 GSM 2x2 Ribbed Stretch Cotton",
            fitType = "Contoured Regular Fit",
            imageRes = R.drawable.img_product_bottoms,
            sizes = listOf("XS", "S", "M", "L", "XL"),
            colors = listOf(
                ProductColor("Warm Bone", 0xFFF2EFE9),
                ProductColor("Espresso", 0xFF3E3028),
                ProductColor("Obsidian", 0xFF151517)
            ),
            isFeatured = true,
            rating = 4.7,
            reviewCount = 76
        ),
        Product(
            id = "brv-005",
            title = "Utility Wide-Leg Cargo & Tee Set",
            subtitle = "Complete Streetwear Co-ord",
            category = BraversCategory.TEES_BOTTOM,
            priceInr = 1699,
            mrpInr = 2299,
            badge = "VALUE SET",
            description = "Paired Bravers heavyweight tee and multi-pocket olive-charcoal wide-leg utility trousers. Ready-to-wear streetwear uniform with adjustable ankle toggles.",
            fabricSpec = "Cotton Twill Bottom + 220 GSM Tee",
            fitType = "Wide-Leg Relaxed",
            imageRes = R.drawable.img_product_bottoms,
            sizes = listOf("S", "M", "L", "XL"),
            colors = listOf(
                ProductColor("Olive Charcoal", 0xFF3E4238),
                ProductColor("Slate Black", 0xFF202124)
            ),
            isFeatured = false,
            rating = 4.8,
            reviewCount = 63
        ),
        Product(
            id = "brv-006",
            title = "Aarna Minimal Slit Kurti",
            subtitle = "Womens Studio Collection",
            category = BraversCategory.WOMENS,
            priceInr = 1049,
            mrpInr = 1499,
            badge = "WOMENS",
            description = "Clean-lined modern kurti crafted for college, studio, and weekend wear. Lightweight drape with reinforced placket stitching and deep side pockets.",
            fabricSpec = "160 GSM Slub Cotton",
            fitType = "Relaxed A-Line",
            imageRes = R.drawable.img_product_kurti,
            sizes = listOf("XS", "S", "M", "L", "XL"),
            colors = listOf(
                ProductColor("Crimson Clay", 0xFFB84336),
                ProductColor("Sandstone", 0xFFDCCBB8)
            ),
            isFeatured = false,
            rating = 4.8,
            reviewCount = 58
        ),
        Product(
            id = "brv-007",
            title = "Obsidian Boxy Core Tee",
            subtitle = "Everyday Heavy Jersey",
            category = BraversCategory.TEES_BOTTOM,
            priceInr = 699,
            mrpInr = 999,
            badge = "UNDER ₹799",
            description = "Zero-graphic minimalist boxy tee in deep matte black. Pre-shrunk and enzyme-washed for an ultra-soft handfeel straight out of the parcel.",
            fabricSpec = "230 GSM Ring-Spun Cotton",
            fitType = "Boxy Cropped-Hem",
            imageRes = R.drawable.img_product_mens_tee,
            sizes = listOf("S", "M", "L", "XL", "XXL"),
            colors = listOf(
                ProductColor("Matte Obsidian", 0xFF141416),
                ProductColor("Warm Stone", 0xFFD9D2C5)
            ),
            isFeatured = false,
            rating = 4.7,
            reviewCount = 89
        ),
        Product(
            id = "brv-008",
            title = "Studio Ribbed Racer Tank",
            subtitle = "High-Neck Womens Fit",
            category = BraversCategory.WOMENS,
            priceInr = 599,
            mrpInr = 899,
            badge = "ACTIVE CHIC",
            description = "High-neck racerback tank tailored with breathable 4-way recovery rib knit. Transitions cleanly from morning movement to layered evening fits.",
            fabricSpec = "220 GSM Cotton-Elastane Rib",
            fitType = "Fitted High-Neck",
            imageRes = R.drawable.img_product_bottoms,
            sizes = listOf("XS", "S", "M", "L"),
            colors = listOf(
                ProductColor("Alabaster", 0xFFF5F2EC),
                ProductColor("Graphite", 0xFF2B2B30)
            ),
            isFeatured = false,
            rating = 4.6,
            reviewCount = 41
        )
    )

    fun getById(productId: String): Product? = products.find { it.id == productId }
}
