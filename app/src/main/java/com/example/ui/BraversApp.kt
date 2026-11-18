package com.example.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.BraversCatalog
import com.example.model.BraversCategory
import com.example.model.ThemePreference
import com.example.ui.screens.BagScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ProductDetailScreen
import com.example.ui.screens.SavedScreen
import com.example.ui.screens.ShopScreen
import com.example.ui.screens.WebStoreScreen
import com.example.ui.theme.BraversTheme

object BraversRoutes {
    const val HOME = "home"
    const val SHOP = "shop"
    const val BAG = "bag"
    const val SAVED = "saved"
    const val WEB = "web"
    const val PRODUCT_DETAIL = "product/{productId}"

    fun productDetail(productId: String): String = "product/$productId"
}

private data class BraversTopLevelDest(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

private val topLevelDestinations = listOf(
    BraversTopLevelDest(
        route = BraversRoutes.HOME,
        label = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
        testTag = "nav_home"
    ),
    BraversTopLevelDest(
        route = BraversRoutes.SHOP,
        label = "Shop",
        selectedIcon = Icons.Filled.Storefront,
        unselectedIcon = Icons.Outlined.Storefront,
        testTag = "nav_shop"
    ),
    BraversTopLevelDest(
        route = BraversRoutes.BAG,
        label = "Bag",
        selectedIcon = Icons.Filled.ShoppingBag,
        unselectedIcon = Icons.Outlined.ShoppingBag,
        testTag = "nav_bag"
    ),
    BraversTopLevelDest(
        route = BraversRoutes.SAVED,
        label = "Saved",
        selectedIcon = Icons.Filled.Favorite,
        unselectedIcon = Icons.Outlined.FavoriteBorder,
        testTag = "nav_saved"
    ),
    BraversTopLevelDest(
        route = BraversRoutes.WEB,
        label = "Live Site",
        selectedIcon = Icons.Filled.Language,
        unselectedIcon = Icons.Outlined.Language,
        testTag = "nav_web"
    )
)

@Composable
fun BraversApp(
    viewModel: BraversViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val systemDark = isSystemInDarkTheme()
    val darkTheme = when (uiState.themePreference) {
        ThemePreference.SYSTEM -> systemDark
        ThemePreference.DARK -> true
        ThemePreference.LIGHT -> false
    }

    BraversTheme(darkTheme = darkTheme) {
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        val isDetailRoute = currentDestination?.route == BraversRoutes.PRODUCT_DETAIL
        val snackbarHostState = remember { SnackbarHostState() }
        var savedInitialTab by rememberSaveable { mutableIntStateOf(0) }

        LaunchedEffect(uiState.snackbarMessage) {
            uiState.snackbarMessage?.let { message ->
                snackbarHostState.showSnackbar(message)
                viewModel.clearSnackbar()
            }
        }

        fun navigateToTopLevel(route: String) {
            navController.navigate(route) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }

        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val useNavRail = maxWidth >= 600.dp

            Scaffold(
                contentWindowInsets = WindowInsets.safeDrawing,
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                bottomBar = {
                    if (!useNavRail && !isDetailRoute) {
                        NavigationBar(
                            windowInsets = WindowInsets.navigationBars,
                            containerColor = MaterialTheme.colorScheme.surface
                        ) {
                            topLevelDestinations.forEach { dest ->
                                val selected = currentDestination?.hierarchy?.any { it.route == dest.route } == true
                                NavigationBarItem(
                                    selected = selected,
                                    onClick = { navigateToTopLevel(dest.route) },
                                    icon = {
                                        BraversNavIcon(
                                            dest = dest,
                                            selected = selected,
                                            bagCount = uiState.cartItemCount
                                        )
                                    },
                                    label = { Text(dest.label) },
                                    modifier = Modifier.testTag(dest.testTag)
                                )
                            }
                        }
                    }
                }
            ) { innerPadding ->
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    if (useNavRail && !isDetailRoute) {
                        NavigationRail(
                            containerColor = MaterialTheme.colorScheme.surface
                        ) {
                            topLevelDestinations.forEach { dest ->
                                val selected = currentDestination?.hierarchy?.any { it.route == dest.route } == true
                                NavigationRailItem(
                                    selected = selected,
                                    onClick = { navigateToTopLevel(dest.route) },
                                    icon = {
                                        BraversNavIcon(
                                            dest = dest,
                                            selected = selected,
                                            bagCount = uiState.cartItemCount
                                        )
                                    },
                                    label = { Text(dest.label) },
                                    modifier = Modifier.testTag("${dest.testTag}_rail")
                                )
                            }
                        }
                    }

                    NavHost(
                        navController = navController,
                        startDestination = BraversRoutes.HOME,
                        modifier = Modifier.weight(1f)
                    ) {
                        composable(BraversRoutes.HOME) {
                            HomeScreen(
                                uiState = uiState,
                                onCategoryClick = { category ->
                                    viewModel.selectCategory(category)
                                    navigateToTopLevel(BraversRoutes.SHOP)
                                },
                                onProductClick = { product ->
                                    navController.navigate(BraversRoutes.productDetail(product.id))
                                },
                                onToggleWishlist = viewModel::toggleWishlist,
                                onOpenShop = {
                                    viewModel.selectCategory(BraversCategory.ALL)
                                    navigateToTopLevel(BraversRoutes.SHOP)
                                },
                                onOpenLiveWebsite = {
                                    navigateToTopLevel(BraversRoutes.WEB)
                                },
                                onApplyOff20 = {
                                    viewModel.applyPromoCode(BraversCatalog.PROMO_CODE_OFF20)
                                },
                                onCycleTheme = viewModel::cycleThemePreference
                            )
                        }

                        composable(BraversRoutes.SHOP) {
                            ShopScreen(
                                uiState = uiState,
                                onSelectCategory = viewModel::selectCategory,
                                onSearchQueryChange = viewModel::updateSearchQuery,
                                onSelectSort = viewModel::selectSortOption,
                                onProductClick = { product ->
                                    navController.navigate(BraversRoutes.productDetail(product.id))
                                },
                                onToggleWishlist = viewModel::toggleWishlist
                            )
                        }

                        composable(BraversRoutes.BAG) {
                            BagScreen(
                                uiState = uiState,
                                onUpdateQuantity = viewModel::updateCartQuantity,
                                onRemoveItem = viewModel::removeFromBag,
                                onApplyPromo = viewModel::applyPromoCode,
                                onClearPromo = viewModel::clearPromoCode,
                                onPlaceOrder = viewModel::placeOrder,
                                onDismissConfirmation = viewModel::dismissOrderConfirmation,
                                onProductClick = { product ->
                                    navController.navigate(BraversRoutes.productDetail(product.id))
                                },
                                onExploreShop = {
                                    navigateToTopLevel(BraversRoutes.SHOP)
                                },
                                onViewOrders = {
                                    savedInitialTab = 1
                                    navigateToTopLevel(BraversRoutes.SAVED)
                                }
                            )
                        }

                        composable(BraversRoutes.SAVED) {
                            SavedScreen(
                                uiState = uiState,
                                initialTab = savedInitialTab,
                                onProductClick = { product ->
                                    navController.navigate(BraversRoutes.productDetail(product.id))
                                },
                                onToggleWishlist = viewModel::toggleWishlist,
                                onExploreShop = {
                                    navigateToTopLevel(BraversRoutes.SHOP)
                                }
                            )
                        }

                        composable(BraversRoutes.WEB) {
                            WebStoreScreen(
                                onReturnToNativeStore = {
                                    navigateToTopLevel(BraversRoutes.SHOP)
                                }
                            )
                        }

                        composable(
                            route = BraversRoutes.PRODUCT_DETAIL,
                            arguments = listOf(navArgument("productId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val productId = backStackEntry.arguments?.getString("productId").orEmpty()
                            val product = BraversCatalog.getById(productId) ?: BraversCatalog.products.first()
                            ProductDetailScreen(
                                product = product,
                                isWishlisted = product.id in uiState.wishlistIds,
                                onBack = { navController.popBackStack() },
                                onToggleWishlist = viewModel::toggleWishlist,
                                onAddToBag = viewModel::addToBag,
                                onGoToBag = {
                                    navController.popBackStack()
                                    navigateToTopLevel(BraversRoutes.BAG)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BraversNavIcon(
    dest: BraversTopLevelDest,
    selected: Boolean,
    bagCount: Int
) {
    val iconVector = if (selected) dest.selectedIcon else dest.unselectedIcon
    if (dest.route == BraversRoutes.BAG && bagCount > 0) {
        BadgedBox(
            badge = {
                Badge(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                ) {
                    Text(bagCount.toString())
                }
            }
        ) {
            Icon(imageVector = iconVector, contentDescription = dest.label)
        }
    } else {
        Icon(imageVector = iconVector, contentDescription = dest.label)
    }
}
