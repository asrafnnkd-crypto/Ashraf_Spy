package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tv
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.AppRepository
import com.example.model.Channel
import com.example.model.ChannelCategory
import com.example.model.ChannelData
import com.example.ui.components.AppLogo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChannelsView(
    onSelectChannel: (Channel) -> Unit,
    modifier: Modifier = Modifier
) {
    val categoriesState by AppRepository.categories.collectAsStateWithLifecycle()
    val streamsState by AppRepository.streams.collectAsStateWithLifecycle()

    var selectedCategory by remember { mutableStateOf<ChannelCategory?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    // Premium Red & Black Dark Theme Colors
    val darkBlackBg = Color(0xFF0C0A0E)
    val cardSurface = Color(0xFF16141A)
    val accentRed = Color(0xFFFF2E2E)
    val borderRed = Color(0xFFB71C1C)

    val premiumRedGradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF2E0808),
            Color(0xFF140303)
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(darkBlackBg)
    ) {
        // Red-Black Premium Header Bar with Brand Logo / Back Title Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF1E0A0A), Color(0xFF0C0A0E))
                    )
                )
                .border(BorderStroke(1.dp, Color(0xFFFF2E2E).copy(alpha = 0.2f)))
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (selectedCategory != null) {
                    IconButton(
                        onClick = { selectedCategory = null },
                        modifier = Modifier.background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "الرجوع للباقات الرئيسية",
                            tint = Color.White
                        )
                    }

                    Text(
                        text = selectedCategory!!.nameArabic,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    AppLogo(showText = true, modifier = Modifier.weight(1f))
                }

                IconButton(
                    onClick = { /* Search Action */ },
                    modifier = Modifier.background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "بحث",
                        tint = Color.White
                    )
                }
            }
        }

        // Stylish Dark Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("ابحث عن قناتك المفضلة والمباشرة...", fontSize = 14.sp, color = Color.Gray) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = accentRed,
                unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                cursorColor = accentRed,
                focusedContainerColor = cardSurface,
                unfocusedContainerColor = cardSurface
            ),
            shape = RoundedCornerShape(14.dp)
        )

        if (selectedCategory == null) {
            // Render beautiful Matte Black cards with glowing Accent Red indicators
            val filteredCats = categoriesState.filter {
                it.nameArabic.contains(searchQuery, ignoreCase = true)
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(top = 4.dp, bottom = 24.dp)
            ) {
                // Interactive Welcome & Guide Card for extra help & fun (أشياء تساعد المستخدم وتزيد المتعة)
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(BorderStroke(1.dp, Color(0xFFFFD700).copy(alpha = 0.2f)), RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(containerColor = cardSurface),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "تلميح",
                                tint = Color(0xFFFFD700),
                                modifier = Modifier
                                    .size(24.dp)
                                    .padding(top = 2.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "بوابة المشاهدين المميزين 🤩",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "اضغط على أي قناة لبدء بث فوري فائق السرعة! داخل المشغل الجديد، يمكنك مضاعفة شدة الصوت لـ 200%، وتكبير الشاشة، والتفاعل مع آلاف المشاهدين بالدردشة الحية!",
                                    color = Color.LightGray,
                                    fontSize = 11.sp,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
                items(filteredCats) { cat ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(72.dp)
                            .clickable { selectedCategory = cat }
                            .border(
                                BorderStroke(1.dp, Color(0xFFFF2E2E).copy(alpha = 0.15f)),
                                RoundedCornerShape(16.dp)
                            ),
                        colors = CardDefaults.cardColors(containerColor = cardSurface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Red accent bar on the left representing active category
                            Box(
                                modifier = Modifier
                                    .width(6.dp)
                                    .height(26.dp)
                                    .background(accentRed, RoundedCornerShape(10.dp))
                            )

                            Text(
                                text = cat.nameArabic,
                                color = Color.White,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Right,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 16.dp)
                            )

                            Icon(
                                imageVector = Icons.Default.Tv,
                                contentDescription = "باقة قنوات",
                                tint = accentRed.copy(alpha = 0.8f),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }
        } else {
            // Drill down into Channels List screen
            val catId = selectedCategory!!.id

            val filteredChannels = streamsState.filter { ch ->
                val typeMatches = when (catId) {
                    "bein_1080" -> ch.type == "beIN Sports" && ch.resolution == "1080p"
                    "bein_720" -> ch.type == "beIN Sports" && (ch.resolution == "720p" || ch.resolution == "1080p")
                    "bein_360" -> ch.type == "beIN Sports"
                    "bein_244" -> ch.type == "beIN Sports"
                    "alkass_ksa" -> ch.type == "Al Kass" || ch.type == "KSA"
                    "arabic_news" -> ch.type == "Arabic"
                    "mbc_entertainment" -> ch.type == "MBC"
                    "quran_sunnah" -> ch.type == "Quran"
                    else -> false
                }
                typeMatches && ch.name.contains(searchQuery, ignoreCase = true)
            }

            if (filteredChannels.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "لا توجد قنوات نشطة في هذا القسم حالياً.",
                        color = Color.Gray,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(top = 4.dp, bottom = 24.dp)
                ) {
                    items(filteredChannels) { channel ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp)
                                .clickable { onSelectChannel(channel) }
                                .border(
                                    BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
                                    RoundedCornerShape(14.dp)
                                ),
                            colors = CardDefaults.cardColors(containerColor = cardSurface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Red indicator badge
                                Badge(
                                    containerColor = if (channel.streamUrl.isEmpty()) Color.DarkGray else accentRed,
                                    modifier = Modifier.padding(end = 4.dp)
                                ) {
                                    Text(
                                        text = if (channel.streamUrl.isEmpty()) "قريباً" else "مباشر 4K",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }

                                Text(
                                    text = channel.name,
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Right,
                                    modifier = Modifier.weight(1f)
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                Icon(
                                    imageVector = Icons.Default.Tv,
                                    contentDescription = "قناة",
                                    tint = accentRed,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
