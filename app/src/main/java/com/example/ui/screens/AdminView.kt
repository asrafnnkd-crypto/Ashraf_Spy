package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.AppRepository
import com.example.model.Channel
import com.example.model.MatchEvent

enum class AdminTab {
    MATCHES,
    CHANNELS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminView(
    modifier: Modifier = Modifier
) {
    var passwordInput by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoggedIn by remember { mutableStateOf(false) }
    var loginError by remember { mutableStateOf(false) }

    val adminPassword = "2022165005441"

    val darkBlackBg = Color(0xFF0C0A0E)
    val cardSurface = Color(0xFF16141A)
    val accentRed = Color(0xFFFF2E2E)

    // Admin state Flow
    val matches by AppRepository.matches.collectAsStateWithLifecycle()
    val channels by AppRepository.streams.collectAsStateWithLifecycle()

    if (!isLoggedIn) {
        // LOGIN PAGE
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(darkBlackBg)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .border(BorderStroke(1.dp, accentRed.copy(alpha = 0.2f)), RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(containerColor = cardSurface),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = "قفل الأدمن",
                        tint = accentRed,
                        modifier = Modifier.size(64.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "تسجيل دخول لوحة التحكم",
                        fontSize = 20.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "يرجى إدخال رمز الأمان للتحكم في القنوات والمباريات والبث المباشر.",
                        fontSize = 12.sp,
                        color = Color.LightGray,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = passwordInput,
                        onValueChange = {
                            passwordInput = it
                            loginError = false
                        },
                        label = { Text("رمز الأمان المشرف", color = Color.Gray) },
                        placeholder = { Text("أدخل كلمة المرور هنا...", color = Color.DarkGray) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "عرض الرمز",
                                    tint = accentRed
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = accentRed,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                            cursorColor = accentRed,
                            focusedContainerColor = Color.Black.copy(alpha = 0.3f),
                            unfocusedContainerColor = Color.Black.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    if (loginError) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "رمز الأمان خاطئ! يرجى المحاولة مرة أخرى.",
                            color = accentRed,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            if (passwordInput == adminPassword) {
                                isLoggedIn = true
                                loginError = false
                            } else {
                                loginError = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = accentRed),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(imageVector = Icons.Default.LockOpen, contentDescription = "دخول")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "دخول المشرف الآمن",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    } else {
        // VIEW AFTER LOGIN (ADMIN PANEL)
        var selectedSubTab by remember { mutableStateOf(AdminTab.MATCHES) }
        var showAddMatchDialog by remember { mutableStateOf(false) }
        var showAddChannelDialog by remember { mutableStateOf(false) }

        var matchToEdit by remember { mutableStateOf<MatchEvent?>(null) }
        var channelToEdit by remember { mutableStateOf<Channel?>(null) }

        Column(
            modifier = modifier
                .fillMaxSize()
                .background(darkBlackBg)
        ) {
            // Header Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFF230707), Color(0xFF0C0A0E))
                        )
                    )
                    .border(BorderStroke(1.dp, accentRed.copy(alpha = 0.3f)))
                    .padding(horizontal = 16.dp, vertical = 20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = {
                            isLoggedIn = false
                            passwordInput = ""
                        },
                        modifier = Modifier.background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "تسجيل خروج",
                            tint = accentRed
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.Center) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "لوحة التحكم",
                            tint = accentRed,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "لوحة تحكم أشرف الأمنية",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    // Static icon or indicator
                    Badge(containerColor = accentRed) {
                        Text(
                            "نشط",
                            color = Color.White,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Tabs selector (Matches VS Channels)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { selectedSubTab = AdminTab.MATCHES },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedSubTab == AdminTab.MATCHES) accentRed else cardSurface
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Event,
                            contentDescription = "مباريات",
                            tint = if (selectedSubTab == AdminTab.MATCHES) Color.White else Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "إدارة المباريات",
                            color = if (selectedSubTab == AdminTab.MATCHES) Color.White else Color.LightGray,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                Button(
                    onClick = { selectedSubTab = AdminTab.CHANNELS },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedSubTab == AdminTab.CHANNELS) accentRed else cardSurface
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Tv,
                            contentDescription = "قنوات",
                            tint = if (selectedSubTab == AdminTab.CHANNELS) Color.White else Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "إدارة القنوات",
                            color = if (selectedSubTab == AdminTab.CHANNELS) Color.White else Color.LightGray,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Add action header button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (selectedSubTab == AdminTab.MATCHES) "المباريات المضافة حالياً (${matches.size}):" else "القنوات المضافة حالياً (${channels.size}):",
                    color = Color.LightGray,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                Button(
                    onClick = {
                        if (selectedSubTab == AdminTab.MATCHES) {
                            showAddMatchDialog = true
                        } else {
                            showAddChannelDialog = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = accentRed.copy(alpha = 0.15f)),
                    border = BorderStroke(1.dp, accentRed),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "إضافة", tint = accentRed, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (selectedSubTab == AdminTab.MATCHES) "مباراة جديدة" else "قناة جديدة",
                        color = accentRed,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Items List
            if (selectedSubTab == AdminTab.MATCHES) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (matches.isEmpty()) {
                        item {
                            EmptyPlaceholder("لا توجد مباريات حالية. اضغط إضافة لصناعة مباراة جديدة!")
                        }
                    } else {
                        items(matches) { match ->
                            MatchAdminItem(
                                match = match,
                                accentRed = accentRed,
                                cardSurface = cardSurface,
                                onEdit = { matchToEdit = match },
                                onDelete = { AppRepository.deleteMatch(match.id) }
                            )
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (channels.isEmpty()) {
                        item {
                            EmptyPlaceholder("لا توجد قنوات حالية. اضغط إضافة لصناعة قناة جديدة!")
                        }
                    } else {
                        items(channels) { ch ->
                            ChannelAdminItem(
                                channel = ch,
                                accentRed = accentRed,
                                cardSurface = cardSurface,
                                onEdit = { channelToEdit = ch },
                                onDelete = { AppRepository.deleteChannel(ch.id) }
                            )
                        }
                    }
                }
            }
        }

        // DIALOGS & FORMS FOR EDITING AND ADDING

        // Add Match Dialog
        if (showAddMatchDialog) {
            MatchFormDialog(
                title = "إضافة مباراة جديدة",
                onDismiss = { showAddMatchDialog = false },
                onSubmit = { newMatch ->
                    AppRepository.addMatch(newMatch)
                    showAddMatchDialog = false
                },
                accentRed = accentRed,
                cardSurface = cardSurface
            )
        }

        // Edit Match Dialog
        matchToEdit?.let { currentMatch ->
            MatchFormDialog(
                title = "تعديل تفاصيل المباراة",
                match = currentMatch,
                onDismiss = { matchToEdit = null },
                onSubmit = { editedMatch ->
                    AppRepository.updateMatch(editedMatch)
                    matchToEdit = null
                },
                accentRed = accentRed,
                cardSurface = cardSurface
            )
        }

        // Add Channel Dialog
        if (showAddChannelDialog) {
            ChannelFormDialog(
                title = "إضافة قناة بث جديدة",
                onDismiss = { showAddChannelDialog = false },
                onSubmit = { newChannel ->
                    AppRepository.addChannel(newChannel)
                    showAddChannelDialog = false
                },
                accentRed = accentRed,
                cardSurface = cardSurface
            )
        }

        // Edit Channel Dialog
        channelToEdit?.let { currentChannel ->
            ChannelFormDialog(
                title = "تعديل تفاصيل القناة",
                channel = currentChannel,
                onDismiss = { channelToEdit = null },
                onSubmit = { editedChannel ->
                    AppRepository.updateChannel(editedChannel)
                    channelToEdit = null
                },
                accentRed = accentRed,
                cardSurface = cardSurface
            )
        }
    }
}

@Composable
fun EmptyPlaceholder(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            color = Color.Gray,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(24.dp)
        )
    }
}

@Composable
fun MatchAdminItem(
    match: MatchEvent,
    accentRed: Color,
    cardSurface: Color,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var confirmDelete by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = cardSurface),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.04f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = match.tournament,
                    color = accentRed,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (match.isLive) {
                        Badge(containerColor = accentRed) {
                            Text("مباشر", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp))
                        }
                    } else {
                        Badge(containerColor = Color.DarkGray) {
                            Text("منتهية/لاحقاً", color = Color.LightGray, fontSize = 9.sp, modifier = Modifier.padding(horizontal = 4.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Teams & Score
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${match.teamHome}  vs  ${match.teamAway}",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.weight(1f)
                )

                if (match.scoreHome != null && match.scoreAway != null) {
                    Text(
                        text = "${match.scoreHome} - ${match.scoreAway}",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                } else {
                    Text(
                        text = "لا توجد نتيجة",
                        color = Color.Gray,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "قناة البث: ${match.channelName} | الوقت: ${match.time}",
                color = Color.LightGray,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            HorizontalDivider(color = Color.White.copy(alpha = 0.05f))

            Spacer(modifier = Modifier.height(8.dp))

            // Actions Buttons row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!confirmDelete) {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier
                            .size(34.dp)
                            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
                    ) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "تعديل", tint = Color.LightGray, modifier = Modifier.size(16.dp))
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    IconButton(
                        onClick = { confirmDelete = true },
                        modifier = Modifier
                            .size(34.dp)
                            .background(accentRed.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "حذف", tint = accentRed, modifier = Modifier.size(16.dp))
                    }
                } else {
                    Text(
                        text = "متأكد من الحذف؟",
                        color = accentRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(end = 12.dp)
                    )

                    Button(
                        onClick = {
                            onDelete()
                            confirmDelete = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = accentRed),
                        contentPadding = PaddingValues(horizontal = 8.dp),
                        modifier = Modifier.height(28.dp),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("نعم، احذف", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    OutlinedButton(
                        onClick = { confirmDelete = false },
                        border = BorderStroke(1.dp, Color.Gray),
                        contentPadding = PaddingValues(horizontal = 8.dp),
                        modifier = Modifier.height(28.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("إلغاء", color = Color.White, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun ChannelAdminItem(
    channel: Channel,
    accentRed: Color,
    cardSurface: Color,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var confirmDelete by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = cardSurface),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.04f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = channel.name,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "القسم: ${channel.type} | الجودة: ${channel.resolution}",
                    color = Color.LightGray,
                    fontSize = 11.sp
                )
                Text(
                    text = "الرابط: ${if (channel.streamUrl.isNotEmpty()) channel.streamUrl else "بث محاكاة (قريباً)"}",
                    color = Color.Gray,
                    fontSize = 9.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            if (!confirmDelete) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier
                            .size(34.dp)
                            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
                    ) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "تعديل", tint = Color.LightGray, modifier = Modifier.size(16.dp))
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = { confirmDelete = true },
                        modifier = Modifier
                            .size(34.dp)
                            .background(accentRed.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "حذف", tint = accentRed, modifier = Modifier.size(16.dp))
                    }
                }
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "حذف؟",
                        color = accentRed,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(end = 6.dp)
                    )

                    IconButton(
                        onClick = {
                            onDelete()
                            confirmDelete = false
                        },
                        modifier = Modifier
                            .size(28.dp)
                            .background(accentRed, CircleShape)
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = "تأكيد", tint = Color.White, modifier = Modifier.size(12.dp))
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = { confirmDelete = false },
                        modifier = Modifier
                            .size(28.dp)
                            .border(BorderStroke(1.dp, Color.Gray), CircleShape)
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "إلغاء", tint = Color.White, modifier = Modifier.size(12.dp))
                    }
                }
            }
        }
    }
}

// MATCH EVENT DIALOG FORM
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchFormDialog(
    title: String,
    match: MatchEvent? = null,
    onDismiss: () -> Unit,
    onSubmit: (MatchEvent) -> Unit,
    accentRed: Color,
    cardSurface: Color
) {
    var teamHome by remember { mutableStateOf(match?.teamHome ?: "") }
    var teamAway by remember { mutableStateOf(match?.teamAway ?: "") }
    var scoreHome by remember { mutableStateOf(match?.scoreHome ?: "") }
    var scoreAway by remember { mutableStateOf(match?.scoreAway ?: "") }
    var tournament by remember { mutableStateOf(match?.tournament ?: "") }
    var timeDetails by remember { mutableStateOf(match?.time ?: "") }
    var channelName by remember { mutableStateOf(match?.channelName ?: "") }
    var isLive by remember { mutableStateOf(match?.isLive ?: false) }
    var streamUrl by remember { mutableStateOf(match?.streamUrl ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                fontSize = 18.sp,
                color = Color.White,
                fontWeight = FontWeight.Black,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Team Home
                OutlinedTextField(
                    value = teamHome,
                    onValueChange = { teamHome = it },
                    label = { Text("الفريق الأول (صاحب الأرض)", color = Color.Gray, fontSize = 12.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = accentRed,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                        cursorColor = accentRed
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Team Away
                OutlinedTextField(
                    value = teamAway,
                    onValueChange = { teamAway = it },
                    label = { Text("الفريق الثاني (الضيف)", color = Color.Gray, fontSize = 12.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = accentRed,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                        cursorColor = accentRed
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Score Home
                    OutlinedTextField(
                        value = scoreHome,
                        onValueChange = { scoreHome = it },
                        label = { Text("أهداف الأول", color = Color.Gray, fontSize = 11.sp) },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = accentRed,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                            cursorColor = accentRed
                        )
                    )

                    // Score Away
                    OutlinedTextField(
                        value = scoreAway,
                        onValueChange = { scoreAway = it },
                        label = { Text("أهداف الثاني", color = Color.Gray, fontSize = 11.sp) },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = accentRed,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                            cursorColor = accentRed
                        )
                    )
                }

                // Tournament Name
                OutlinedTextField(
                    value = tournament,
                    onValueChange = { tournament = it },
                    label = { Text("اسم الدوري أو البطولة", color = Color.Gray, fontSize = 12.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = accentRed,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                        cursorColor = accentRed
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Time Details (e.g. 21:00 or 75')
                OutlinedTextField(
                    value = timeDetails,
                    onValueChange = { timeDetails = it },
                    label = { Text("تفاصيل التوقيت أو الشوط", color = Color.Gray, fontSize = 12.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = accentRed,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                        cursorColor = accentRed
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Channel Name
                OutlinedTextField(
                    value = channelName,
                    onValueChange = { channelName = it },
                    label = { Text("القناة الناقلة للمباراة", color = Color.Gray, fontSize = 12.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = accentRed,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                        cursorColor = accentRed
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Stream URL
                OutlinedTextField(
                    value = streamUrl,
                    onValueChange = { streamUrl = it },
                    label = { Text("رابط البث المباشر المباشر", color = Color.Gray, fontSize = 12.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = accentRed,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                        cursorColor = accentRed
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Is Live Toggle Switch
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("هل المباراة مباشرة الآن؟", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Switch(
                        checked = isLive,
                        onCheckedChange = { isLive = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = accentRed,
                            uncheckedThumbColor = Color.Gray,
                            uncheckedTrackColor = Color.DarkGray
                        )
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (teamHome.trim().isNotEmpty() && teamAway.trim().isNotEmpty() && tournament.trim().isNotEmpty()) {
                        val finalMatch = MatchEvent(
                            id = match?.id ?: "match_${System.currentTimeMillis()}",
                            teamHome = teamHome,
                            teamAway = teamAway,
                            scoreHome = if (scoreHome.trim().isEmpty()) null else scoreHome,
                            scoreAway = if (scoreAway.trim().isEmpty()) null else scoreAway,
                            tournament = tournament,
                            time = timeDetails,
                            channelName = channelName,
                            isLive = isLive,
                            streamUrl = streamUrl
                        )
                        onSubmit(finalMatch)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = accentRed),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("حفظ التغييرات", fontWeight = FontWeight.Bold, color = Color.White)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                border = BorderStroke(1.dp, Color.Gray),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
            ) {
                Text("إلغاء", color = Color.White)
            }
        },
        containerColor = cardSurface,
        shape = RoundedCornerShape(16.dp)
    )
}

// CHANNEL DIALOG FORM
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChannelFormDialog(
    title: String,
    channel: Channel? = null,
    onDismiss: () -> Unit,
    onSubmit: (Channel) -> Unit,
    accentRed: Color,
    cardSurface: Color
) {
    var name by remember { mutableStateOf(channel?.name ?: "") }
    var streamUrl by remember { mutableStateOf(channel?.streamUrl ?: "") }
    var type by remember { mutableStateOf(channel?.type ?: "beIN Sports") }
    var resolution by remember { mutableStateOf(channel?.resolution ?: "1080p") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                fontSize = 18.sp,
                color = Color.White,
                fontWeight = FontWeight.Black,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Name
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("اسم القناة", color = Color.Gray, fontSize = 12.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = accentRed,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                        cursorColor = accentRed
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Stream URL
                OutlinedTextField(
                    value = streamUrl,
                    onValueChange = { streamUrl = it },
                    label = { Text("رابط البث (HTTP Live Stream)", color = Color.Gray, fontSize = 12.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = accentRed,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                        cursorColor = accentRed
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Type / Category selector (beIN Sports, Al Kass, KSA, Arabic, Quran, MBC, etc.)
                var expandedType by remember { mutableStateOf(false) }
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { expandedType = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("القسم: $type", color = Color.White, fontSize = 13.sp)
                            Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "قائمة", tint = accentRed)
                        }
                    }

                    DropdownMenu(
                        expanded = expandedType,
                        onDismissRequest = { expandedType = false },
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .background(cardSurface)
                    ) {
                        val categoriesList = listOf("beIN Sports", "Al Kass", "KSA", "Arabic", "Quran", "MBC")
                        categoriesList.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat, color = Color.White) },
                                onClick = {
                                    type = cat
                                    expandedType = false
                                }
                            )
                        }
                    }
                }

                // Resolution selector (1080p, 720p, 360p, 244p, 4K)
                var expandedRes by remember { mutableStateOf(false) }
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { expandedRes = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("الدقة: $resolution", color = Color.White, fontSize = 13.sp)
                            Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "قائمة", tint = accentRed)
                        }
                    }

                    DropdownMenu(
                        expanded = expandedRes,
                        onDismissRequest = { expandedRes = false },
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .background(cardSurface)
                    ) {
                        val resList = listOf("1080p", "720p", "360p", "244p", "2160p (4K)")
                        resList.forEach { res ->
                            DropdownMenuItem(
                                text = { Text(res, color = Color.White) },
                                onClick = {
                                    resolution = res
                                    expandedRes = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.trim().isNotEmpty()) {
                        val finalChannel = Channel(
                            id = channel?.id ?: "channel_${System.currentTimeMillis()}",
                            name = name,
                            streamUrl = streamUrl,
                            type = type,
                            resolution = resolution
                        )
                        onSubmit(finalChannel)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = accentRed),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("حفظ التفاصيل", fontWeight = FontWeight.Bold, color = Color.White)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                border = BorderStroke(1.dp, Color.Gray),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
            ) {
                Text("إلغاء", color = Color.White)
            }
        },
        containerColor = cardSurface,
        shape = RoundedCornerShape(16.dp)
    )
}
