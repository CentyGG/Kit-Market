package com.example.kit_market.presentation.screen.worker

import com.example.kit_market.domain.model.Order

enum class WorkerTab(val title: String) {
    NEW("Новые"),
    ASSEMBLING("В сборке"),
    READY("К выдаче"),
    HISTORY("История")
}

data class WorkerOrdersState(
    val selectedTab: WorkerTab = WorkerTab.NEW,
    val orders: List<Order> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
