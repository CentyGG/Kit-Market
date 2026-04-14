package com.example.kit_market.presentation.screen.products

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.kit_market.domain.model.Category
import com.example.kit_market.domain.model.Product
import com.example.kit_market.presentation.common.ProductCard
import com.example.kit_market.presentation.screen.productdetail.ProductDetailScreen
import com.example.kit_market.presentation.theme.KitBlue
import com.example.kit_market.presentation.theme.KitTextPrimary
import com.example.kit_market.presentation.theme.KitTextSecondary

class ProductsScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = koinScreenModel<ProductsScreenModel>()
        val state by screenModel.state.collectAsState()

        Column(modifier = Modifier.fillMaxSize().padding(top = 16.dp)) {
            // Search bar
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = { screenModel.onIntent(ProductsIntent.Search(it)) },
                placeholder = { Text("Поиск продуктов") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = KitBlue)
                }
            } else if (state.isSearching) {
                // Search results
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    items(
                        items = state.searchResults,
                        key = { it.id }
                    ) { product ->
                        SearchResultItem(
                            product = product,
                            quantityInCart = state.cartQuantities[product.id] ?: 0,
                            onAddToCart = { screenModel.onIntent(ProductsIntent.AddToCart(product)) },
                            onIncrement = { screenModel.onIntent(ProductsIntent.Increment(product.id)) },
                            onDecrement = { screenModel.onIntent(ProductsIntent.Decrement(product.id)) },
                            onClick = { navigator.push(ProductDetailScreen(product.id)) }
                        )
                    }
                }
            } else {
                // Categories with products
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    state.categories.forEach { category ->
                        val products = state.productsByCategory[category.id] ?: emptyList()
                        item(key = "header_${category.id}") {
                            CategoryHeader(
                                category = category,
                                onSeeAll = { navigator.push(CategoryProductsScreen(category.id, category.name)) }
                            )
                        }
                        item(key = "products_${category.id}") {
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(
                                    items = products,
                                    key = { it.id }
                                ) { product ->
                                    ProductCard(
                                        product = product,
                                        quantityInCart = state.cartQuantities[product.id] ?: 0,
                                        onAddToCart = { screenModel.onIntent(ProductsIntent.AddToCart(product)) },
                                        onIncrement = { screenModel.onIntent(ProductsIntent.Increment(product.id)) },
                                        onDecrement = { screenModel.onIntent(ProductsIntent.Decrement(product.id)) },
                                        onClick = { navigator.push(ProductDetailScreen(product.id)) }
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryHeader(
    category: Category,
    onSeeAll: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = category.name,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = KitTextPrimary
        )
        Text(
            text = "Посмотреть все",
            fontSize = 14.sp,
            color = KitBlue,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.clickable(onClick = onSeeAll)
        )
    }
}

@Composable
private fun SearchResultItem(
    product: Product,
    quantityInCart: Int,
    onAddToCart: () -> Unit,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onClick: () -> Unit
) {
    ProductCard(
        product = product,
        quantityInCart = quantityInCart,
        onAddToCart = onAddToCart,
        onIncrement = onIncrement,
        onDecrement = onDecrement,
        onClick = onClick,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}
