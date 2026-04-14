package com.example.kit_market.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.example.kit_market.presentation.screen.cart.CartScreen
import com.example.kit_market.presentation.screen.products.ProductsScreen
import com.example.kit_market.presentation.screen.profile.ProfileScreen
import com.example.kit_market.presentation.theme.KitBlue
import com.example.kit_market.presentation.theme.KitTextSecondary
import com.example.kit_market.presentation.theme.KitWhite

class MainScreen : Screen {

    @Composable
    override fun Content() {
        TabNavigator(HomeTab) {
            Scaffold(
                bottomBar = {
                    NavigationBar(containerColor = KitWhite) {
                        TabNavigationItem(HomeTab)
                        TabNavigationItem(CartTab)
                        TabNavigationItem(ProfileTab)
                    }
                }
            ) { padding ->
                Box(modifier = Modifier.padding(padding)) {
                    CurrentTab()
                }
            }
        }
    }
}

@Composable
private fun RowScope.TabNavigationItem(tab: Tab) {
    val tabNavigator = LocalTabNavigator.current
    val selected = tabNavigator.current == tab

    NavigationBarItem(
        selected = selected,
        onClick = { tabNavigator.current = tab },
        icon = {
            tab.options.icon?.let {
                Icon(painter = it, contentDescription = tab.options.title)
            }
        },
        label = { Text(tab.options.title) },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = KitBlue,
            selectedTextColor = KitBlue,
            unselectedIconColor = KitTextSecondary,
            unselectedTextColor = KitTextSecondary,
            indicatorColor = KitWhite
        )
    )
}

object HomeTab : Tab {
    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.Default.Home)
            return remember { TabOptions(index = 0u, title = "Продукты", icon = icon) }
        }

    @Composable
    override fun Content() {
        ProductsScreen().Content()
    }
}

object CartTab : Tab {
    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.Default.ShoppingCart)
            return remember { TabOptions(index = 1u, title = "Корзина", icon = icon) }
        }

    @Composable
    override fun Content() {
        CartScreen().Content()
    }
}

object ProfileTab : Tab {
    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.Default.Person)
            return remember { TabOptions(index = 2u, title = "Профиль", icon = icon) }
        }

    @Composable
    override fun Content() {
        ProfileScreen().Content()
    }
}
