package com.example.kit_market.presentation.screen.products

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import org.koin.compose.koinInject
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
        val screenModel = koinInject<ProductsViewModel>()
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
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = KitBlue)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Загрузка товаров...",
                            fontSize = 14.sp,
                            color = KitTextSecondary
                        )
                    }
                }
            } else if (state.isError) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Не удалось загрузить товары",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = KitTextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Проверьте подключение к сети",
                            fontSize = 14.sp,
                            color = KitTextSecondary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { screenModel.onIntent(ProductsIntent.Retry) },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = KitBlue)
                        ) {
                            Text("Повторить")
                        }
                    }
                }
            } else if (state.isSearching) {
                // Search results — сетка по 2 в ряд
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = state.searchResults,
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
            } else {
                // Categories with products
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    state.categories.forEach { category ->
                        val products = state.productsByCategory[category.name] ?: emptyList()
                        item(key = "header_${category.name}") {
                            CategoryHeader(
                                category = category,
                                onSeeAll = { navigator.push(CategoryProductsScreen(category.name)) }
                            )
                        }
                        item(key = "products_${category.name}") {
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

