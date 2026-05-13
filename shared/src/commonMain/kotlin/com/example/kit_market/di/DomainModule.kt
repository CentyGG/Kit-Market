package com.example.kit_market.di

import com.example.kit_market.domain.usecase.*
import org.koin.dsl.module

val domainModule = module {
    factory { GetCategoriesUseCase(get()) }
    factory { GetProductsByCategoryUseCase(get()) }
    factory { SearchProductsUseCase(get()) }
    factory { GetProductByIdUseCase(get()) }
    factory { AddToCartUseCase(get()) }
    factory { RemoveFromCartUseCase(get()) }
    factory { UpdateCartItemQuantityUseCase(get()) }
    factory { GetCartUseCase(get()) }
    factory { ClearCartUseCase(get()) }
    factory { LoginUseCase(get()) }
    factory { VerifyCodeUseCase(get()) }
    factory { GetUserUseCase(get()) }
    factory { UpdateUserUseCase(get()) }
    factory { LogoutUseCase(get()) }
    factory { PlaceOrderUseCase(get()) }
    factory { GetOrdersUseCase(get()) }
    factory { GetOrderByIdUseCase(get()) }
    factory { CreatePaymentUseCase(get()) }
    factory { GetPaymentStatusUseCase(get()) }
    factory { CancelOrderUseCase(get()) }
}
