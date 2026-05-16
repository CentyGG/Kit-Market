package com.example.kit_market.presentation.screen.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.*
import cafe.adriel.voyager.core.screen.Screen
import org.koin.compose.koinInject
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.kit_market.notification.NotificationManager
import com.example.kit_market.presentation.screen.auth.AuthScreen
import com.example.kit_market.presentation.screen.help.HelpScreen
import com.example.kit_market.presentation.screen.orders.OrdersScreen
import com.example.kit_market.presentation.theme.*

class ProfileScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = koinInject<ProfileViewModel>()
        val state by screenModel.state.collectAsState()
        val loggedOut by screenModel.loggedOut.collectAsState()

        LaunchedEffect(loggedOut) {
            if (loggedOut) {
                // Navigate to root navigator (parent of tab navigator)
                var nav = navigator
                while (nav.parent != null) {
                    nav = nav.parent!!
                }
                nav.replaceAll(AuthScreen())
            }
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Ошибка загрузки профиля (нет данных и нет интернета)
            if (state.user == null && state.error != null) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = state.error!!,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { screenModel.retryLoadProfile() },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = KitBlue)
                    ) {
                        Text("Повторить", color = KitWhite)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // User name section
            if (state.isEditing) {
                OutlinedTextField(
                    value = state.editFirstName,
                    onValueChange = { screenModel.onIntent(ProfileIntent.UpdateFirstName(it)) },
                    label = { Text("Имя") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = state.editLastName,
                    onValueChange = { screenModel.onIntent(ProfileIntent.UpdateLastName(it)) },
                    label = { Text("Фамилия") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (state.error != null) {
                    Text(
                        text = state.error!!,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
                Button(
                    onClick = { screenModel.onIntent(ProfileIntent.SaveName) },
                    modifier = Modifier.fillMaxWidth().height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = KitBlue)
                ) {
                    Text("Сохранить", color = KitWhite)
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val displayName = buildString {
                        val first = state.user?.firstName ?: ""
                        val last = state.user?.lastName ?: ""
                        if (first.isNotBlank() || last.isNotBlank()) {
                            append("$first $last".trim())
                        } else {
                            append("Пользователь")
                        }
                    }
                    Text(
                        text = displayName,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = KitTextPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { screenModel.onIntent(ProfileIntent.ToggleEdit) }) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Редактировать",
                            tint = KitBlue
                        )
                    }
                }
                Text(
                    text = state.user?.phone ?: "",
                    fontSize = 14.sp,
                    color = KitTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Menu buttons
            ProfileMenuButton(
                icon = Icons.AutoMirrored.Filled.List,
                text = "Заказы",
                onClick = { navigator.push(OrdersScreen()) }
            )
            Spacer(modifier = Modifier.height(12.dp))
            ProfileMenuButton(
                icon = Icons.Default.Info,
                text = "Помощь",
                onClick = { navigator.push(HelpScreen()) }
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Notification toggle
            val notificationsEnabled by NotificationManager.enabled.collectAsState()
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = KitWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = KitTextPrimary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Уведомления",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = KitTextPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = notificationsEnabled,
                        onCheckedChange = { NotificationManager.setEnabled(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = KitWhite,
                            checkedTrackColor = KitBlue,
                            uncheckedThumbColor = KitWhite,
                            uncheckedTrackColor = KitGray
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Logout button
            OutlinedButton(
                onClick = { screenModel.onIntent(ProfileIntent.Logout) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Выйти", fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ProfileMenuButton(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (enabled) KitWhite else KitGrayLight
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (enabled) KitWhite else KitGrayLight,
                contentColor = if (enabled) KitTextPrimary else KitTextSecondary,
                disabledContainerColor = KitGrayLight,
                disabledContentColor = KitTextSecondary
            ),
            enabled = enabled
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(text, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}
