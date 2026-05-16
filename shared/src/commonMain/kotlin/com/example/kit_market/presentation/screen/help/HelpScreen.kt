package com.example.kit_market.presentation.screen.help

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.kit_market.presentation.theme.*

private data class FaqItem(
    val question: String,
    val answer: String
)

private val faqItems = listOf(
    FaqItem(
        question = "Как сделать заказ?",
        answer = "Выберите нужные товары на главном экране, добавьте их в корзину, затем перейдите в корзину и нажмите \"Оформить заказ\". Выберите способ оплаты и время самовывоза."
    ),
    FaqItem(
        question = "Как оплатить заказ?",
        answer = "Доступно два способа оплаты:\n\n- Картой онлайн — вы будете перенаправлены на безопасную страницу оплаты Тинькофф\n- Наличными при получении — оплатите при самовывозе"
    ),
    FaqItem(
        question = "Как выбрать время самовывоза?",
        answer = "При оформлении заказа выберите удобную дату и время. Заказ можно забрать с 9:00 до 22:00. Минимальное время — через 30 минут от текущего момента. Можно заказать на сегодня или на будущие дни."
    ),
    FaqItem(
        question = "Где забрать заказ?",
        answer = "Заказ можно забрать в нашем магазине по адресу, указанному на сайте. Приходите к выбранному времени — ваш заказ будет готов."
    ),
    FaqItem(
        question = "Как отменить заказ?",
        answer = "Откройте раздел \"Заказы\" в профиле, выберите нужный заказ и нажмите \"Отменить\". Отменить можно заказы в статусе \"Создан\" или \"Оплачен\". Если заказ был оплачен онлайн, деньги вернутся на карту."
    ),
    FaqItem(
        question = "Когда вернут деньги за отменённый заказ?",
        answer = "При отмене оплаченного заказа возврат происходит автоматически. Деньги поступят на карту в течение 1-5 рабочих дней в зависимости от вашего банка."
    ),
    FaqItem(
        question = "Какие статусы у заказа?",
        answer = "- Создан — заказ оформлен, ожидает оплаты или сборки\n- Оплачен — оплата прошла успешно\n- В сборке — мы собираем ваш заказ\n- Готов к выдаче — можно забирать\n- Выдан — заказ получен\n- Отменён — заказ отменён"
    ),
    FaqItem(
        question = "Могу ли я изменить заказ после оформления?",
        answer = "К сожалению, изменить состав заказа после оформления нельзя. Вы можете отменить текущий заказ и создать новый с нужными товарами."
    ),
    FaqItem(
        question = "Что делать, если товара нет в наличии?",
        answer = "Если товар закончился, он будет недоступен для заказа. Следите за обновлениями каталога — товары регулярно пополняются."
    ),
    FaqItem(
        question = "Как связаться с поддержкой?",
        answer = "Если у вас остались вопросы, напишите нам в Telegram: @skejtv или позвоните по телефону 8 (908) 444-37-77 в рабочие часы с 9:00 до 22:00."
    )
)

class HelpScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Помощь", fontWeight = FontWeight.Bold) },
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Text(
                        text = "Часто задаваемые вопросы",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = KitTextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                items(faqItems) { faq ->
                    FaqCard(faq)
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun FaqCard(faq: FaqItem) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = KitWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = faq.question,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = KitTextPrimary,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = KitBlue
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = KitGray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = faq.answer,
                        fontSize = 14.sp,
                        color = KitTextSecondary,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}
