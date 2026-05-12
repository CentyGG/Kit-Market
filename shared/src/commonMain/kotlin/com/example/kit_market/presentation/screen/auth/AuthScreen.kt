package com.example.kit_market.presentation.screen.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import org.koin.compose.koinInject
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.kit_market.presentation.navigation.MainScreen
import com.example.kit_market.presentation.theme.KitBlue
import com.example.kit_market.presentation.theme.KitTextSecondary
import com.example.kit_market.presentation.theme.KitWhite

class AuthScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = koinInject<AuthViewModel>()
        val state by screenModel.state.collectAsState()
        val navigateToMain by screenModel.navigateToMain.collectAsState()

        LaunchedEffect(navigateToMain) {
            if (navigateToMain) {
                navigator.replaceAll(MainScreen())
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "\uD83D\uDC33",
                fontSize = 64.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Кит-Маркет",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = KitBlue
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Вход в аккаунт",
                fontSize = 16.sp,
                color = KitTextSecondary
            )
            Spacer(modifier = Modifier.height(32.dp))

            when (state.step) {
                AuthStep.PHONE_INPUT -> PhoneInput(
                    phone = state.phone,
                    isLoading = state.isLoading,
                    error = state.error,
                    onPhoneChange = { screenModel.onIntent(AuthIntent.EnterPhone(it)) },
                    onSubmit = { screenModel.onIntent(AuthIntent.SubmitPhone) }
                )
                AuthStep.CODE_INPUT -> CodeInput(
                    phone = state.phone,
                    code = state.code,
                    isLoading = state.isLoading,
                    error = state.error,
                    onCodeChange = { screenModel.onIntent(AuthIntent.EnterCode(it)) },
                    onSubmit = { screenModel.onIntent(AuthIntent.SubmitCode) }
                )
            }
        }
    }
}

@Composable
private fun PhoneInput(
    phone: String,
    isLoading: Boolean,
    error: String?,
    onPhoneChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = "+7",
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            modifier = Modifier.width(64.dp),
            shape = RoundedCornerShape(12.dp),
            textStyle = LocalTextStyle.current.copy(
                textAlign = TextAlign.Center,
                fontSize = 16.sp
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        OutlinedTextField(
            value = phone,
            onValueChange = { if (it.length <= 10 && it.all { c -> c.isDigit() }) onPhoneChange(it) },
            label = { Text("Номер телефона") },
            placeholder = { Text("") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp),
            isError = error != null
        )
    }
    if (error != null) {
        Text(
            text = error,
            color = MaterialTheme.colorScheme.error,
            fontSize = 12.sp,
            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
        )
    }
    Spacer(modifier = Modifier.height(16.dp))
    Button(
        onClick = onSubmit,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = KitBlue),
        enabled = !isLoading && phone.length == 10
    ) {
        if (isLoading) {
            CircularProgressIndicator(color = KitWhite, modifier = Modifier.size(24.dp))
        } else {
            Text("Получить код", color = KitWhite, fontSize = 16.sp)
        }
    }
}

@Composable
private fun CodeInput(
    phone: String,
    code: String,
    isLoading: Boolean,
    error: String?,
    onCodeChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    Text(
        text = "Код отправлен на номер\n+7$phone",
        fontSize = 14.sp,
        color = KitTextSecondary,
        textAlign = TextAlign.Center
    )
    Spacer(modifier = Modifier.height(16.dp))
    OutlinedTextField(
        value = code,
        onValueChange = { if (it.length <= 4) onCodeChange(it) },
        label = { Text("Код из СМС") },
        placeholder = { Text("0000") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        isError = error != null
    )
    if (error != null) {
        Text(
            text = error,
            color = MaterialTheme.colorScheme.error,
            fontSize = 12.sp,
            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
        )
    }
    Spacer(modifier = Modifier.height(16.dp))
    Button(
        onClick = onSubmit,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = KitBlue),
        enabled = !isLoading
    ) {
        if (isLoading) {
            CircularProgressIndicator(color = KitWhite, modifier = Modifier.size(24.dp))
        } else {
            Text("Войти", color = KitWhite, fontSize = 16.sp)
        }
    }
}
