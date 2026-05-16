package com.example.kit_market

import com.example.kit_market.data.repository.CartRepositoryImpl
import com.example.kit_market.domain.model.Product
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CartRepositoryTest {

    private fun createProduct(id: Long, name: String = "Product $id", price: Double = 100.0) =
        Product(id = id, name = name, description = "", imageUrl = "", price = price)

    @Test
    fun cartStartsEmpty() = runTest {
        val repo = CartRepositoryImpl()
        val items = repo.getCart().first()
        assertTrue(items.isEmpty())
    }

    @Test
    fun addSameProductTwiceIncrementsQuantity() = runTest {
        val repo = CartRepositoryImpl()
        val product = createProduct(1)

        repo.addToCart(product)
        repo.addToCart(product)

        val items = repo.getCart().first()
        assertEquals(1, items.size)
        assertEquals(2, items[0].quantity)
    }

    @Test
    fun addToCartCreatesItemWithQuantityOne() = runTest {
        val repo = CartRepositoryImpl()
        val product = createProduct(1)

        repo.addToCart(product)

        val items = repo.getCart().first()
        assertEquals(1, items.size)
        assertEquals(1, items[0].quantity)
        assertEquals(1L, items[0].product.id)
    }

    @Test
    fun addDifferentProductsCreatesSeparateItems() = runTest {
        val repo = CartRepositoryImpl()

        repo.addToCart(createProduct(1))
        repo.addToCart(createProduct(2))

        val items = repo.getCart().first()
        assertEquals(2, items.size)
    }

    @Test
    fun updateQuantitySetsExactValue() = runTest {
        val repo = CartRepositoryImpl()
        repo.addToCart(createProduct(1))

        repo.updateQuantity(1L, 5)

        val items = repo.getCart().first()
        assertEquals(5, items[0].quantity)
    }

    @Test
    fun updateQuantityToZeroRemovesItem() = runTest {
        val repo = CartRepositoryImpl()
        repo.addToCart(createProduct(1))
        repo.addToCart(createProduct(2))

        repo.updateQuantity(1L, 0)

        val items = repo.getCart().first()
        assertEquals(1, items.size)
        assertEquals(2L, items[0].product.id)
    }

    @Test
    fun updateQuantityNegativeRemovesItem() = runTest {
        val repo = CartRepositoryImpl()
        repo.addToCart(createProduct(1))

        repo.updateQuantity(1L, -1)

        val items = repo.getCart().first()
        assertTrue(items.isEmpty())
    }

    @Test
    fun removeFromCartDeletesSpecificItem() = runTest {
        val repo = CartRepositoryImpl()
        repo.addToCart(createProduct(1))
        repo.addToCart(createProduct(2))
        repo.addToCart(createProduct(3))

        repo.removeFromCart(2L)

        val items = repo.getCart().first()
        assertEquals(2, items.size)
        assertEquals(listOf(1L, 3L), items.map { it.product.id })
    }

    @Test
    fun removeNonExistentProductDoesNothing() = runTest {
        val repo = CartRepositoryImpl()
        repo.addToCart(createProduct(1))

        repo.removeFromCart(999L)

        val items = repo.getCart().first()
        assertEquals(1, items.size)
    }

    @Test
    fun clearCartRemovesEverything() = runTest {
        val repo = CartRepositoryImpl()
        repo.addToCart(createProduct(1))
        repo.addToCart(createProduct(2))
        repo.addToCart(createProduct(3))

        repo.clearCart()

        val items = repo.getCart().first()
        assertTrue(items.isEmpty())
    }

    @Test
    fun updateQuantityForNonExistentProductDoesNothing() = runTest {
        val repo = CartRepositoryImpl()
        repo.addToCart(createProduct(1))

        repo.updateQuantity(999L, 5)

        val items = repo.getCart().first()
        assertEquals(1, items.size)
        assertEquals(1, items[0].quantity)
    }
}
