package com.example.kit_market.presentation.screen.products

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.example.kit_market.presentation.theme.KitBlueLight
import com.example.kit_market.presentation.theme.KitGrayLight
import com.example.kit_market.presentation.theme.KitTextPrimary
import com.example.kit_market.presentation.theme.KitTextSecondary
import com.example.kit_market.presentation.theme.KitWhite
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

enum class PriceSort { NONE, LOW_TO_HIGH, HIGH_TO_LOW }

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

        var allProducts by remember { mutableStateOf<List<Product>>(emptyList()) }
        var cartQuantities by remember { mutableStateOf<Map<Long, Int>>(emptyMap()) }

        // Применённые фильтры
        var selectedSubcategory by remember { mutableStateOf<String?>(null) }
        var priceSort by remember { mutableStateOf(PriceSort.NONE) }

        // Временные фильтры (внутри bottom sheet до нажатия "Применить")
        var tempSubcategory by remember { mutableStateOf<String?>(null) }
        var tempPriceSort by remember { mutableStateOf(PriceSort.NONE) }

        var showFilterSheet by remember { mutableStateOf(false) }
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        LaunchedEffect(categoryName) {
            allProducts = getProductsByCategoryUseCase(categoryName)
        }
        LaunchedEffect(Unit) {
            getCartUseCase().collect { cartItems ->
                cartQuantities = cartItems.associate { it.product.id to it.quantity }
            }
        }

        val subcategories = remember(allProducts) {
            allProducts.map { it.subcategory }
                .filter { it.isNotBlank() }
                .distinct()
                .sorted()
        }

        val filteredProducts = remember(allProducts, selectedSubcategory, priceSort) {
            var result = if (selectedSubcategory == null) allProducts
            else allProducts.filter { it.subcategory == selectedSubcategory }
            when (priceSort) {
                PriceSort.LOW_TO_HIGH -> result = result.sortedBy { it.price }
                PriceSort.HIGH_TO_LOW -> result = result.sortedByDescending { it.price }
                PriceSort.NONE -> {}
            }
            result
        }

        val hasActiveFilters = selectedSubcategory != null || priceSort != PriceSort.NONE
        val title = if (selectedSubcategory != null) selectedSubcategory!! else categoryName

        // Bottom Sheet с фильтрами
        if (showFilterSheet) {
            ModalBottomSheet(
                onDismissRequest = { showFilterSheet = false },
                sheetState = sheetState,
                containerColor = KitWhite,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            ) {
                FilterSheetContent(
                    subcategories = subcategories,
                    selectedSubcategory = tempSubcategory,
                    priceSort = tempPriceSort,
                    onSubcategorySelected = { tempSubcategory = it },
                    onPriceSortSelected = { tempPriceSort = it },
                    onApply = {
                        selectedSubcategory = tempSubcategory
                        priceSort = tempPriceSort
                        showFilterSheet = false
                    },
                    onReset = {
                        tempSubcategory = null
                        tempPriceSort = PriceSort.NONE
                    }
                )
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(title, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                        }
                    },
                    actions = {
                        if (hasActiveFilters) {
                            TextButton(onClick = {
                                selectedSubcategory = null
                                priceSort = PriceSort.NONE
                                tempSubcategory = null
                                tempPriceSort = PriceSort.NONE
                            }) {
                                Text("Сброс", color = KitBlue, fontSize = 14.sp)
                            }
                        }
                        IconButton(onClick = {
                            tempSubcategory = selectedSubcategory
                            tempPriceSort = priceSort
                            showFilterSheet = true
                        }) {
                            BadgedBox(
                                badge = {
                                    if (hasActiveFilters) {
                                        Badge(containerColor = KitBlue)
                                    }
                                }
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.List,
                                    contentDescription = "Фильтры",
                                    tint = KitBlue
                                )
                            }
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
                items(filteredProducts, key = { it.id }) { product ->
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

@Composable
private fun FilterSheetContent(
    subcategories: List<String>,
    selectedSubcategory: String?,
    priceSort: PriceSort,
    onSubcategorySelected: (String?) -> Unit,
    onPriceSortSelected: (PriceSort) -> Unit,
    onApply: () -> Unit,
    onReset: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 400.dp)
            .padding(horizontal = 20.dp)
            .padding(bottom = 24.dp)
    ) {
        // Заголовок
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Фильтры",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = KitTextPrimary
            )
            TextButton(onClick = onReset) {
                Text("Сбросить", color = KitTextSecondary, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Прокручиваемый контент
        LazyColumn(
            modifier = Modifier.weight(1f, fill = false),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            // Сортировка по цене
            item {
                Text(
                    text = "Сортировка по цене",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = KitTextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SortChip(
                        label = "Сначала дешёвые",
                        selected = priceSort == PriceSort.LOW_TO_HIGH,
                        onClick = {
                            onPriceSortSelected(
                                if (priceSort == PriceSort.LOW_TO_HIGH) PriceSort.NONE else PriceSort.LOW_TO_HIGH
                            )
                        }
                    )
                    SortChip(
                        label = "Сначала дорогие",
                        selected = priceSort == PriceSort.HIGH_TO_LOW,
                        onClick = {
                            onPriceSortSelected(
                                if (priceSort == PriceSort.HIGH_TO_LOW) PriceSort.NONE else PriceSort.HIGH_TO_LOW
                            )
                        }
                    )
                }
            }

            // Подкатегории
            if (subcategories.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Вид товаров",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = KitTextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
                items(subcategories) { subcategory ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSubcategorySelected(
                                    if (selectedSubcategory == subcategory) null else subcategory
                                )
                            }
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedSubcategory == subcategory,
                            onClick = {
                                onSubcategorySelected(
                                    if (selectedSubcategory == subcategory) null else subcategory
                                )
                            },
                            modifier = Modifier.size(20.dp),
                            colors = RadioButtonDefaults.colors(selectedColor = KitBlue)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = subcategory,
                            fontSize = 13.sp,
                            color = KitTextPrimary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Кнопка "Применить"
        Button(
            onClick = onApply,
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = KitBlue)
        ) {
            Text(
                text = "Применить",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun SortChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = if (selected) KitBlueLight else KitGrayLight,
        border = if (selected) BorderStroke(1.dp, KitBlue) else null
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            fontSize = 13.sp,
            color = if (selected) KitBlue else KitTextSecondary,
            fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal
        )
    }
}
