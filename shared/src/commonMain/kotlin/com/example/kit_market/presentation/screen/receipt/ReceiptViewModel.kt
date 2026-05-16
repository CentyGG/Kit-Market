package com.example.kit_market.presentation.screen.receipt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kit_market.data.remote.ApiService
import com.example.kit_market.domain.model.OrderItem
import com.example.kit_market.domain.repository.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ReceiptViewModel(
    private val orderId: Long,
    private val apiService: ApiService,
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ReceiptState())
    val state = _state.asStateFlow()

    init {
        loadReceipt()
    }

    private fun loadReceipt() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val order = orderRepository.getOrderById(orderId)
                val receipt = apiService.getReceipt(orderId)

                _state.update {
                    it.copy(
                        isLoading = false,
                        status = receipt.status,
                        items = order.items,
                        total = (receipt.total ?: order.totalPrice.let { p -> (p * 100).toLong() }) / 100.0,
                        receiptDatetime = receipt.receiptDatetime,
                        fnNumber = receipt.fnNumber,
                        fiscalDocumentNumber = receipt.fiscalDocumentNumber,
                        fiscalDocumentAttribute = receipt.fiscalDocumentAttribute,
                        checkUrl = receipt.checkUrl
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(isLoading = false, error = "Не удалось загрузить чек. Проверьте интернет-соединение.")
                }
            }
        }
    }

    fun retry() {
        loadReceipt()
    }
}
