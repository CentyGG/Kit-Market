package com.example.kit_market.presentation.screen.products

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.kit_market.domain.model.Product
import com.example.kit_market.domain.usecase.AddToCartUseCase
import com.example.kit_market.domain.usecase.GetCartUseCase
import com.example.kit_market.domain.usecase.GetProductsByCategoryUseCase
import com.example.kit_market.domain.usecase.UpdateCartItemQuantityUseCase
import com.example.kit_market.presentation.common.ProductCard
import com.example.kit_market.presentation.screen.productdetail.ProductDetailScreen
import com.example.kit_market.presentation.theme.KitBlue
import com.example.kit_market.presentation.theme.KitWhite
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

data class CategoryProductsScreen(
    val categoryName: String
) : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val scope = rememberCoroutineScope()
        val getProductsByCategoryUseCase = koinInject<GetProductsByCategoryUseCase>()
        val addToCartUseCase = koinInject<AddToCartUseCase>()
        val updateCartItemQuantityUseCase = koinInject<UpdateCartItemQuantityUseCase>()
        val getCartUseCase = koinInject<GetCartUseCase>()

        var products by remember { mutableStateOf<List<Product>>(emptyList()) }
        var cartQuantities by remember { mutableStateOf<Map<Long, Int>>(emptyMap()) }

        LaunchedEffect(categoryName) {
            products = getProductsByCategoryUseCase(categoryName)
        }
        LaunchedEffect(Unit) {
            getCartUseCase().collect { cartItems ->
                cartQuantities = cartItems.associate { it.product.id to it.quantity }
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(categoryName, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = KitWhite,
                        titleContentColor = KitBlue
                    )
                )
            }
        ) { padding ->
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(products, key = { it.id }) { product ->
                    ProductCard(
                        product = product,
                        quantityInCart = cartQuantities[product.id] ?: 0,
                        onAddToCart = { scope.launch { addToCartUseCase(product) } },
                        onIncrement = {
                            val qty = cartQuantities[product.id] ?: 0
                            scope.launch { updateCartItemQuantityUseCase(product.id, qty + 1) }
                        },
                        onDecrement = {
                            val qty = cartQuantities[product.id] ?: 0
                            scope.launch { updateCartItemQuantityUseCase(product.id, qty - 1) }
                        },
                        onClick = { navigator.push(ProductDetailScreen(product.id)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
