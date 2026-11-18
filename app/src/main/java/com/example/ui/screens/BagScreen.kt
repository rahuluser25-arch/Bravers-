package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.data.BraversCatalog
import com.example.model.CartItem
import com.example.model.CheckoutPaymentMethod
import com.example.model.OrderRecord
import com.example.model.Product
import com.example.ui.BraversUiState
import com.example.ui.components.formatInr

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BagScreen(
    uiState: BraversUiState,
    onUpdateQuantity: (CartItem, Int) -> Unit,
    onRemoveItem: (CartItem) -> Unit,
    onApplyPromo: (String) -> Unit,
    onClearPromo: () -> Unit,
    onPlaceOrder: (String, String, String, String, CheckoutPaymentMethod) -> Unit,
    onDismissConfirmation: () -> Unit,
    onProductClick: (Product) -> Unit,
    onExploreShop: () -> Unit,
    onViewOrders: () -> Unit,
    modifier: Modifier = Modifier
) {
    var promoInput by remember(uiState.appliedPromoCode) { mutableStateOf(uiState.appliedPromoCode) }
    var showCheckoutSheet by remember { mutableStateOf(false) }

    uiState.latestOrderConfirmation?.let { order ->
        OrderConfirmationDialog(
            order = order,
            onDismiss = onDismissConfirmation,
            onViewOrders = {
                onDismissConfirmation()
                onViewOrders()
            }
        )
    }

    if (showCheckoutSheet) {
        CheckoutBottomSheet(
            grandTotalInr = uiState.grandTotalInr,
            onDismiss = { showCheckoutSheet = false },
            onConfirmOrder = { name, phone, address, cityPin, method ->
                showCheckoutSheet = false
                onPlaceOrder(name, phone, address, cityPin, method)
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("bag_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Text(
                text = "Shopping Bag",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = if (uiState.cartItemCount == 0) {
                    "Your Bravers bag is empty"
                } else {
                    "${uiState.cartItemCount} item(s) ready for express dispatch"
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (uiState.cartItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ShoppingBag,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(56.dp)
                    )
                    Text(
                        text = "Nothing in your bag yet",
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Add drop-shoulder tees, kurtis, or tanks and apply code OFF20 for 20% off orders ₹499+.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Button(
                        onClick = onExploreShop,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("empty_bag_shop_button")
                    ) {
                        Text("Explore Bravers Drops")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 28.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Free Shipping Progress Card
                item {
                    val progress = (uiState.discountedSubtotalInr.toFloat() / BraversCatalog.FREE_SHIPPING_THRESHOLD_INR.toFloat())
                        .coerceIn(0f, 1f)
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.LocalShipping,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = if (uiState.freeShippingRemainingInr == 0) {
                                        "Free Pan-India Express Shipping unlocked!"
                                    } else {
                                        "Add ${formatInr(uiState.freeShippingRemainingInr)} more for FREE shipping"
                                    },
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }

                // Bag Items
                items(
                    items = uiState.cartItems,
                    key = { "${it.product.id}_${it.selectedSize}_${it.selectedColorName}" }
                ) { item ->
                    BagItemCard(
                        item = item,
                        onIncrement = { onUpdateQuantity(item, 1) },
                        onDecrement = { onUpdateQuantity(item, -1) },
                        onRemove = { onRemoveItem(item) },
                        onClick = { onProductClick(item.product) }
                    )
                }

                // Promo Code Card
                item {
                    Card(
                        shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.LocalOffer,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "Bravers Promo Code",
                                    style = MaterialTheme.typography.titleSmall
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = promoInput,
                                    onValueChange = { promoInput = it.uppercase() },
                                    placeholder = { Text("Enter OFF20") },
                                    singleLine = true,
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("bag_promo_input")
                                )
                                if (uiState.appliedPromoCode.isNotEmpty()) {
                                    OutlinedButton(
                                        onClick = {
                                            promoInput = ""
                                            onClearPromo()
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.height(54.dp)
                                    ) {
                                        Icon(Icons.Outlined.Close, contentDescription = "Clear promo")
                                    }
                                } else {
                                    Button(
                                        onClick = { onApplyPromo(promoInput) },
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier
                                            .height(54.dp)
                                            .testTag("bag_apply_promo_button")
                                    ) {
                                        Text("Apply")
                                    }
                                }
                            }

                            if (uiState.appliedPromoCode != BraversCatalog.PROMO_CODE_OFF20) {
                                AssistChip(
                                    onClick = {
                                        promoInput = BraversCatalog.PROMO_CODE_OFF20
                                        onApplyPromo(BraversCatalog.PROMO_CODE_OFF20)
                                    },
                                    label = { Text("Tap to apply OFF20 (20% OFF ₹499+)") },
                                    modifier = Modifier.testTag("bag_quick_off20_chip")
                                )
                            }

                            uiState.promoMessage?.let { msg ->
                                Text(
                                    text = msg,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (uiState.promoIsError) {
                                        MaterialTheme.colorScheme.error
                                    } else {
                                        MaterialTheme.colorScheme.secondary
                                    }
                                )
                            }
                        }
                    }
                }

                // Price Summary + Checkout Button
                item {
                    Card(
                        shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "Order Summary",
                                style = MaterialTheme.typography.titleMedium
                            )
                            SummaryRow("Bag Subtotal", formatInr(uiState.subtotalInr))
                            if (uiState.discountInr > 0) {
                                SummaryRow(
                                    label = "Promo (${uiState.appliedPromoCode})",
                                    value = "-${formatInr(uiState.discountInr)}",
                                    highlight = true
                                )
                            }
                            SummaryRow(
                                label = "Delivery",
                                value = if (uiState.shippingInr == 0) "FREE" else formatInr(uiState.shippingInr),
                                highlight = uiState.shippingInr == 0
                            )
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Total Payable",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = formatInr(uiState.grandTotalInr),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Button(
                                onClick = { showCheckoutSheet = true },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondary,
                                    contentColor = MaterialTheme.colorScheme.onSecondary
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .testTag("bag_checkout_button")
                            ) {
                                Text(
                                    text = "Proceed to Checkout • ${formatInr(uiState.grandTotalInr)}",
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BagItemCard(
    item: CartItem,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onRemove: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)),
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = item.product.imageRes),
                contentDescription = item.product.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(86.dp)
                    .clip(RoundedCornerShape(10.dp))
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = item.product.title,
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = onRemove,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .size(32.dp)
                            .testTag("bag_remove_${item.product.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.DeleteOutline,
                            contentDescription = "Remove item",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Text(
                    text = "Size: ${item.selectedSize} • ${item.selectedColorName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatInr(item.lineTotalInr),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = onDecrement,
                                modifier = Modifier
                                    .size(36.dp)
                                    .testTag("bag_dec_${item.product.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Remove,
                                    contentDescription = "Decrease quantity",
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Text(
                                text = "${item.quantity}",
                                style = MaterialTheme.typography.labelLarge,
                                modifier = Modifier.padding(horizontal = 6.dp)
                            )
                            IconButton(
                                onClick = onIncrement,
                                modifier = Modifier
                                    .size(36.dp)
                                    .testTag("bag_inc_${item.product.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Add,
                                    contentDescription = "Increase quantity",
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    highlight: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (highlight) FontWeight.SemiBold else FontWeight.Normal,
            color = if (highlight) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CheckoutBottomSheet(
    grandTotalInr: Int,
    onDismiss: () -> Unit,
    onConfirmOrder: (String, String, String, String, CheckoutPaymentMethod) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var cityPin by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf(CheckoutPaymentMethod.UPI) }
    var validationError by remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Bravers Express Checkout",
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = "Delivering across India from bravers.in",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Name") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("checkout_name_input")
            )
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it.filter { ch -> ch.isDigit() }.take(10) },
                label = { Text("Mobile Number (10 digits)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("checkout_phone_input")
            )
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Street / Flat / Landmark") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("checkout_address_input")
            )
            OutlinedTextField(
                value = cityPin,
                onValueChange = { cityPin = it },
                label = { Text("City & PIN Code") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("checkout_city_input")
            )

            Text(
                text = "Payment Method",
                style = MaterialTheme.typography.titleSmall
            )
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                CheckoutPaymentMethod.entries.forEach { method ->
                    FilterChip(
                        selected = paymentMethod == method,
                        onClick = { paymentMethod = method },
                        label = { Text(method.label) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            validationError?.let { err ->
                Text(
                    text = err,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Button(
                onClick = {
                    when {
                        name.isBlank() -> validationError = "Please enter your full name."
                        phone.length < 10 -> validationError = "Please enter a valid 10-digit mobile number."
                        address.isBlank() -> validationError = "Please enter your delivery address."
                        cityPin.isBlank() -> validationError = "Please enter your city and PIN code."
                        else -> onConfirmOrder(name, phone, address, cityPin, paymentMethod)
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("checkout_place_order_button")
            ) {
                Text("Place Order • ${formatInr(grandTotalInr)}")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun OrderConfirmationDialog(
    order: OrderRecord,
    onDismiss: () -> Unit,
    onViewOrders: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Outlined.CheckCircle,
                contentDescription = "Order Confirmed",
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(36.dp)
            )
        },
        title = {
            Text(
                text = "Order Confirmed!",
                style = MaterialTheme.typography.headlineMedium
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Order ID: ${order.orderId}",
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = order.itemSummary,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Total Paid: ${formatInr(order.totalInr)} (${order.paymentMethod})",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(onClick = onViewOrders) {
                Text("Track in Orders")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Continue Shopping")
            }
        }
    )
}
