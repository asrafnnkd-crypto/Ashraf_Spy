package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.api.GeminiService
import kotlinx.coroutines.launch

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssistantView(
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    var inputMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val chatMessages = remember {
        mutableStateListOf(
            ChatMessage(
                text = "مرحباً بك في مساعد أشرف الرياضي الذكي! ⚽✨\nأنا هنا للرد على استفساراتك وتوقعاتك الرياضية، ومساعدتك في تصفح البث المباشر وجدول المباريات بالكامل.\n\nكيف يمكنني دعمك ومساعدتك اليوم؟ 🏆",
                isUser = false
            )
        )
    }

    // Arabic instant suggested prompts
    val suggestedPrompts = listOf(
        "ما هي قنوات بي إن سبورتس المتاحة؟",
        "توقع نتيجة ديربي الدار البيضاء",
        "كيف أشغل قنوات القرآن الكريم؟",
        "هل البث مستقر اليوم؟"
    )

    val darkBlackBg = Color(0xFF0C0A0E)
    val cardSurface = Color(0xFF16141A)
    val accentRed = Color(0xFFFF2E2E)

    fun sendMessage(msg: String) {
        if (msg.trim().isEmpty()) return
        chatMessages.add(ChatMessage(msg, isUser = true))
        inputMessage = ""
        isLoading = true

        coroutineScope.launch {
            // Smooth scroll to the end
            try {
                listState.animateScrollToItem(chatMessages.size - 1)
            } catch (e: Exception) { }

            val response = GeminiService.getArabicReply(msg)
            isLoading = false
            chatMessages.add(ChatMessage(response, isUser = false))

            try {
                listState.animateScrollToItem(chatMessages.size - 1)
            } catch (e: Exception) { }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(darkBlackBg)
    ) {
        // AI Assistant Top Bar header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF1E0A0A), Color(0xFF0C0A0E))
                    )
                )
                .border(BorderStroke(1.dp, Color(0xFFFF2E2E).copy(alpha = 0.2f)))
                .padding(horizontal = 16.dp, vertical = 18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                     imageVector = Icons.Default.SupportAgent,
                     contentDescription = "مساعد أشرف",
                     tint = accentRed,
                     modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "مساعد أشرف الرياضي الذكي (AI)",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }

        // List of conversation bubbles
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(chatMessages) { chat ->
                ChatBubble(chat)
            }

            if (isLoading) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = cardSurface),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
                            shape = RoundedCornerShape(topStart = 0.dp, topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 16.dp),
                            modifier = Modifier.widthIn(max = 280.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    color = accentRed,
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "جاري الاستشارة وصياغة الرد البديل...",
                                    fontSize = 12.sp,
                                    color = Color.LightGray,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }

        // Suggestions block of tags (Only visible when not loading)
        if (!isLoading) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                suggestedPrompts.forEach { prompt ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(Color(0xFFFF2E2E).copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                            .border(BorderStroke(1.dp, Color(0xFFFF2E2E).copy(alpha = 0.2f)), RoundedCornerShape(12.dp))
                            .clickable { sendMessage(prompt) }
                            .padding(horizontal = 8.dp, vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = prompt,
                            color = Color(0xFFFF4D4D),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            lineHeight = 15.sp,
                            maxLines = 2
                        )
                    }
                }
            }
        }

        // Bottom Message Input row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF121016))
                .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)))
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { sendMessage(inputMessage) },
                enabled = inputMessage.trim().isNotEmpty() && !isLoading,
                modifier = Modifier
                    .size(46.dp)
                    .background(
                        if (inputMessage.trim().isNotEmpty() && !isLoading) accentRed else Color.DarkGray,
                        RoundedCornerShape(12.dp)
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "إرسال",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            OutlinedTextField(
                value = inputMessage,
                onValueChange = { inputMessage = it },
                placeholder = { Text("اطرح استفسارك الرياضي أو توقع مباراة...", fontSize = 14.sp, color = Color.Gray) },
                singleLine = true,
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = accentRed,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                    cursorColor = accentRed,
                    focusedContainerColor = cardSurface,
                    unfocusedContainerColor = cardSurface
                ),
                shape = RoundedCornerShape(14.dp)
            )
        }
    }
}

@Composable
fun ChatBubble(chat: ChatMessage) {
    val accentRed = Color(0xFFFF2E2E)
    val containerColor = if (chat.isUser) accentRed else Color(0xFF16141A)
    val textColor = Color.White
    val shape = if (chat.isUser) {
        RoundedCornerShape(topStart = 16.dp, topEnd = 0.dp, bottomEnd = 16.dp, bottomStart = 16.dp)
    } else {
        RoundedCornerShape(topStart = 0.dp, topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 16.dp)
    }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = if (chat.isUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Card(
            shape = shape,
            colors = CardDefaults.cardColors(containerColor = containerColor),
            border = if (!chat.isUser) BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)) else null,
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.widthIn(max = 300.dp)
        ) {
            Text(
                text = chat.text,
                color = textColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 22.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                textAlign = TextAlign.Right
            )
        }
    }
}
