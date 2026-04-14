package com.example.kit_market.di

import com.example.kit_market.presentation.screen.auth.AuthScreenModel
import com.example.kit_market.presentation.screen.cart.CartScreenModel
import com.example.kit_market.presentation.screen.orders.OrdersScreenModel
import com.example.kit_market.presentation.screen.productdetail.ProductDetailScreenModel
import com.example.kit_market.presentation.screen.products.ProductsScreenModel
import com.example.kit_market.presentation.screen.profile.ProfileScreenModel
import com.example.kit_market.presentation.screen.splash.SplashScreenModel
import org.koin.dsl.module

val presentationModule = module {
    factory { SplashScreenModel(get()) }
    factory { AuthScreenModel(get(), get()) }
    factory { ProductsScreenModel(get(), get(), get(), get(), get(), get()) }
    factory { params -> ProductDetailScreenModel(params.get(), get(), get(), get(), get()) }
    factory { CartScreenModel(get(), get(), get()) }
    factory { ProfileScreenModel(get(), get(), get()) }
    factory { OrdersScreenModel(get()) }
}
