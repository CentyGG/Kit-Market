package com.example.kit_market.di

import com.example.kit_market.data.repository.CartRepositoryImpl
import com.example.kit_market.data.repository.OrderRepositoryImpl
import com.example.kit_market.data.repository.ProductRepositoryImpl
import com.example.kit_market.data.repository.UserRepositoryImpl
import com.example.kit_market.domain.repository.CartRepository
import com.example.kit_market.domain.repository.OrderRepository
import com.example.kit_market.domain.repository.ProductRepository
import com.example.kit_market.domain.repository.UserRepository
import org.koin.dsl.module

val dataModule = module {
    single<ProductRepository> { ProductRepositoryImpl() }
    single<CartRepository> { CartRepositoryImpl() }
    single<UserRepository> { UserRepositoryImpl() }
    single<OrderRepository> { OrderRepositoryImpl() }
}
