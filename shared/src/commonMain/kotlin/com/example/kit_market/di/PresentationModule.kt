package com.example.kit_market.di

import com.example.kit_market.presentation.screen.auth.AuthViewModel
import com.example.kit_market.presentation.screen.cart.CartViewModel
import com.example.kit_market.presentation.screen.checkout.CheckoutViewModel
import com.example.kit_market.presentation.screen.orderdetail.OrderDetailViewModel
import com.example.kit_market.presentation.screen.orders.OrdersViewModel
import com.example.kit_market.presentation.screen.productdetail.ProductDetailViewModel
import com.example.kit_market.presentation.screen.products.ProductsViewModel
import com.example.kit_market.presentation.screen.profile.ProfileViewModel
import com.example.kit_market.presentation.screen.receipt.ReceiptViewModel
import com.example.kit_market.presentation.screen.splash.SplashViewModel
import org.koin.dsl.module

val presentationModule = module {
    factory { SplashViewModel(get<com.example.kit_market.domain.repository.UserRepository>()) }
    factory { AuthViewModel(get(), get()) }
    factory { ProductsViewModel(get(), get(), get(), get(), get(), get()) }
    factory { params -> ProductDetailViewModel(params.get(), get(), get(), get(), get()) }
    factory { CartViewModel(get(), get(), get()) }
    factory { ProfileViewModel(get(), get(), get()) }
    factory { OrdersViewModel(get()) }
    factory { CheckoutViewModel(get(), get(), get(), get()) }
    factory { params -> OrderDetailViewModel(params.get(), get()) }
    factory { params -> ReceiptViewModel(params.get(), get(), get()) }
}
