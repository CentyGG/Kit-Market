package com.example.kit_market

import com.example.kit_market.domain.model.OrderStatus
import kotlin.test.Test
import kotlin.test.assertEquals

class OrderStatusTest {

    @Test
    fun fromStringParsesAllStatuses() {
        assertEquals(OrderStatus.CREATED, OrderStatus.fromString("created"))
        assertEquals(OrderStatus.PAID, OrderStatus.fromString("paid"))
        assertEquals(OrderStatus.ASSEMBLING, OrderStatus.fromString("assembling"))
        assertEquals(OrderStatus.READY, OrderStatus.fromString("ready"))
        assertEquals(OrderStatus.COMPLETED, OrderStatus.fromString("completed"))
        assertEquals(OrderStatus.CANCELLED, OrderStatus.fromString("cancelled"))
    }

    @Test
    fun fromStringIsCaseInsensitive() {
        assertEquals(OrderStatus.PAID, OrderStatus.fromString("PAID"))
        assertEquals(OrderStatus.PAID, OrderStatus.fromString("Paid"))
        assertEquals(OrderStatus.CANCELLED, OrderStatus.fromString("CANCELLED"))
    }

    @Test
    fun fromStringUnknownDefaultsToCreated() {
        assertEquals(OrderStatus.CREATED, OrderStatus.fromString("unknown"))
        assertEquals(OrderStatus.CREATED, OrderStatus.fromString(""))
        assertEquals(OrderStatus.CREATED, OrderStatus.fromString("garbage"))
    }

    @Test
    fun toRussianCreated() {
        assertEquals("Создан", OrderStatus.CREATED.toRussian())
    }

    @Test
    fun toRussianPaid() {
        assertEquals("Оплачен", OrderStatus.PAID.toRussian())
    }

    @Test
    fun toRussianAssembling() {
        assertEquals("В сборке", OrderStatus.ASSEMBLING.toRussian())
    }

    @Test
    fun toRussianReady() {
        assertEquals("Готов к выдаче", OrderStatus.READY.toRussian())
    }

    @Test
    fun toRussianCompleted() {
        assertEquals("Выдан", OrderStatus.COMPLETED.toRussian())
    }

    @Test
    fun toRussianCancelled() {
        assertEquals("Отменён", OrderStatus.CANCELLED.toRussian())
    }

    @Test
    fun allStatusesHaveRussianTranslation() {
        OrderStatus.entries.forEach { status ->
            val russian = status.toRussian()
            assert(russian.isNotBlank()) { "Status $status has blank Russian translation" }
        }
    }
}
