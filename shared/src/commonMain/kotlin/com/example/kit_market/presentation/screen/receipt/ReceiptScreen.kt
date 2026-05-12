package com.example.kit_market.presentation.screen.receipt

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.kit_market.presentation.theme.*
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

data class ReceiptScreen(val orderId: Long) : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinInject<ReceiptViewModel> { parametersOf(orderId) }
        val state by viewModel.state.collectAsState()
        val uriHandler = LocalUriHandler.current
        val mono = FontFamily.Monospace

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Кассовый чек", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = KitWhite,
                        titleContentColor = KitTextPrimary
                    )
                )
            }
        ) { padding ->
            when {
                state.isLoading -> {
                    Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = KitBlue)
                    }
                }
                state.error != null -> {
                    Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                        Text(state.error!!, color = MaterialTheme.colorScheme.error, fontSize = 16.sp)
                    }
                }
                state.status == "wait" -> {
                    Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = KitBlue)
                            Spacer(Modifier.height(16.dp))
                            Text("Чек обрабатывается...", fontSize = 16.sp, color = KitTextSecondary)
                        }
                    }
                }
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(20.dp)
                            ) {
                                Text(
                                    text = "КАССОВЫЙ ЧЕК",
                                    fontFamily = mono,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "Приход",
                                    fontFamily = mono,
                                    fontSize = 14.sp,
                                    textAlign = TextAlign.Center,
                                    color = KitTextSecondary,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                if (state.receiptDatetime != null) {
                                    Text(
                                        text = state.receiptDatetime!!,
                                        fontFamily = mono,
                                        fontSize = 12.sp,
                                        textAlign = TextAlign.Center,
                                        color = KitTextSecondary,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }

                                Spacer(Modifier.height(12.dp))
                                DashedDivider()
                                Spacer(Modifier.height(12.dp))

                                state.items.forEach { item ->
                                    Text(
                                        text = item.productName,
                                        fontFamily = mono,
                                        fontSize = 13.sp,
                                        color = KitTextPrimary
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "${item.quantity} x ${"%.2f".format(item.price)}",
                                            fontFamily = mono,
                                            fontSize = 12.sp,
                                            color = KitTextSecondary
                                        )
                                        Text(
                                            text = "${"%.2f".format(item.price * item.quantity)}",
                                            fontFamily = mono,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = KitTextPrimary
                                        )
                                    }
                                    Spacer(Modifier.height(6.dp))
                                }

                                Spacer(Modifier.height(6.dp))
                                DashedDivider()
                                Spacer(Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "ИТОГО",
                                        fontFamily = mono,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Text(
                                        text = "${"%.2f".format(state.total)} \u20BD",
                                        fontFamily = mono,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                }

                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "Электронными",
                                    fontFamily = mono,
                                    fontSize = 12.sp,
                                    color = KitTextSecondary
                                )

                                Spacer(Modifier.height(12.dp))
                                DashedDivider()
                                Spacer(Modifier.height(12.dp))

                                if (state.fnNumber != null) {
                                    FiscalRow("ФН", state.fnNumber!!, mono)
                                }
                                if (state.fiscalDocumentNumber != null) {
                                    FiscalRow("ФД", state.fiscalDocumentNumber.toString(), mono)
                                }
                                if (state.fiscalDocumentAttribute != null) {
                                    FiscalRow("ФПД", state.fiscalDocumentAttribute.toString(), mono)
                                }
                            }
                        }

                        if (state.checkUrl != null) {
                            Spacer(Modifier.height(16.dp))
                            Button(
                                onClick = { uriHandler.openUri(state.checkUrl!!) },
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = KitBlue)
                            ) {
                                Text("Проверить чек", color = KitWhite, fontSize = 16.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DashedDivider() {
    Text(
        text = "- - - - - - - - - - - - - - - - - - - -",
        fontFamily = FontFamily.Monospace,
        fontSize = 12.sp,
        color = Color.LightGray,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun FiscalRow(label: String, value: String, mono: FontFamily) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontFamily = mono, fontSize = 12.sp, color = KitTextSecondary)
        Text(text = value, fontFamily = mono, fontSize = 12.sp, color = KitTextPrimary)
    }
    Spacer(Modifier.height(2.dp))
}
