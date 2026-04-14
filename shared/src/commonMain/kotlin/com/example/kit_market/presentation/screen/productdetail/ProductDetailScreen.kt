package com.example.kit_market.presentation.screen.productdetail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import com.example.kit_market.presentation.common.QuantityCounter
import com.example.kit_market.presentation.theme.*
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

data class ProductDetailScreen(val productId: Long) : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = koinInject<ProductDetailScreenModel>(parameters = { parametersOf(productId) })
        val state by screenModel.state.collectAsState()

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(state.product?.name ?: "", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = KitWhite,
                        titleContentColor = KitTextPrimary
                    )
                )
            }
        ) { padding ->
            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = KitBlue)
                }
            } else {
                val product = state.product ?: return@Scaffold
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    AsyncImage(
                        model = product.imageUrl,
                        contentDescription = product.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = product.name,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = KitTextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "${product.price} \u20BD",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = KitBlue
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Quantity counter
                    if (state.quantityInCart > 0) {
                        QuantityCounter(
                            count = state.quantityInCart,
                            onIncrement = { screenModel.onIntent(ProductDetailIntent.Increment) },
                            onDecrement = { screenModel.onIntent(ProductDetailIntent.Decrement) }
                        )
                    } else {
                        Button(
                            onClick = { screenModel.onIntent(ProductDetailIntent.AddToCart) },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = KitBlue)
                        ) {
                            Text("В корзину", color = KitWhite, fontSize = 16.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Description
                    Text(
                        text = "Описание",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = KitTextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = product.description,
                        fontSize = 14.sp,
                        color = KitTextSecondary,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    // Nutritional info
                    Text(
                        text = "Пищевая ценность (на 100 г)",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = KitTextPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        NutritionItem("Калории", "${product.calories}", "ккал")
                        NutritionItem("Белки", "${product.protein}", "г")
                        NutritionItem("Жиры", "${product.fat}", "г")
                        NutritionItem("Углеводы", "${product.carbs}", "г")
                    }
                }
            }
        }
    }
}

@Composable
private fun NutritionItem(label: String, value: String, unit: String) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = KitBlueLight)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = KitBlue
            )
            Text(
                text = unit,
                fontSize = 12.sp,
                color = KitTextSecondary
            )
            Text(
                text = label,
                fontSize = 12.sp,
                color = KitTextSecondary
            )
        }
    }
}
