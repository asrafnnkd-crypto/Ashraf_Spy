package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.AppRepository
import com.example.model.Channel
import com.example.model.ChannelData
import com.example.model.MatchEvent
import com.example.ui.components.AppLogo

@Composable
fun MatchesView(
    onSelectStream: (Channel) -> Unit,
    modifier: Modifier = Modifier
) {
    val matchesState by AppRepository.matches.collectAsStateWithLifecycle()
    val streamsState by AppRepository.streams.collectAsStateWithLifecycle()

    var filterLiveOnly by remember { mutableStateOf(false) }

    val filteredMatches = if (filterLiveOnly) {
        matchesState.filter { it.isLive }
    } else {
        matchesState
    }

    // Dynamic stats calculated instantly
    val totalLiveMatches = matchesState.count { it.isLive }
    val totalUpcomingMatches = matchesState.count { !it.isLive && !it.time.contains("انتهت") }

    // Infinite animation for flashing live indicator
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val livePulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    // Colors
    val darkBlackBg = Color(0xFF0C0A0E)
    val accentRed = Color(0xFFFF2E2E)
    val cardSurface = Color(0xFF16141A)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(darkBlackBg)
    ) {
        // Red-Black Premium Header Bar with brand new App Logo
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
                AppLogo(showText = true, modifier = Modifier.weight(1f))

                // Mini stats badge
                Badge(
                    containerColor = accentRed.copy(alpha = 0.15f),
                    contentColor = accentRed,
                    modifier = Modifier.border(0.5.dp, accentRed.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                ) {
                    Text(
                        text = "مباريات اليوم: ${matchesState.size}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // Live Mode Filter Selector styled beautifully
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "تصفية جدول المباريات:",
                color = Color.Gray,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "المباريات المباشرة فقط",
                    color = if (filterLiveOnly) accentRed else Color.LightGray,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Switch(
                    checked = filterLiveOnly,
                    onCheckedChange = { filterLiveOnly = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = accentRed,
                        uncheckedThumbColor = Color.Gray,
                        uncheckedTrackColor = Color.DarkGray.copy(alpha = 0.5f)
                    )
                )
            }
        }

        // Live Stats Summary Banner - (أشياء تساعد مستخدم وتزيد المتعة)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF131116))
                .border(0.5.dp, Color.White.copy(alpha = 0.04f), RoundedCornerShape(10.dp))
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("المباريات الجارية", color = Color.Gray, fontSize = 10.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(6.dp).background(accentRed, CircleShape))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("$totalLiveMatches مباريات", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }

            Box(modifier = Modifier.width(1.dp).height(24.dp).background(Color.White.copy(alpha = 0.1f)))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("المباريات القادمة اليوم", color = Color.Gray, fontSize = 10.sp)
                Text("$totalUpcomingMatches مواجهات", color = Color(0xFFFFD700), fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(filteredMatches) { match ->
                MatchCard(
                    match = match,
                    livePulseAlpha = livePulseAlpha,
                    onClickWatch = {
                        val streamChannel = streamsState.find { it.name.contains(match.channelName, true) || it.streamUrl == match.streamUrl }
                            ?: Channel("dynamic_match_stream", match.channelName, match.streamUrl, "DynamicSports")
                        onSelectStream(streamChannel)
                    }
                )
            }
        }
    }
}

@Composable
fun MatchCard(
    match: MatchEvent,
    livePulseAlpha: Float,
    onClickWatch: () -> Unit
) {
    val cardSurface = Color(0xFF16141A)
    val accentRed = Color(0xFFFF2E2E)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(BorderStroke(1.dp, Color(0xFFFF2E2E).copy(alpha = 0.12f)), RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = cardSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Card Header (League & Live flag)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(
                        imageVector = Icons.Default.SportsSoccer,
                        contentDescription = "دوري",
                        tint = accentRed,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = match.tournament,
                        fontSize = 12.sp,
                        color = Color.LightGray,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Right
                    )
                }

                if (match.isLive) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.alpha(livePulseAlpha)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(accentRed, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "مباشر الآن",
                            color = accentRed,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                } else if (match.time.contains("انتهت")) {
                    Text(
                        text = "انتهت",
                        color = Color.Gray,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Text(
                        text = "قريباً",
                        color = Color(0xFFFFD700), // Gold
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Scoreboard Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Home Team
                Text(
                    text = match.teamHome,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // SCORE container
                Box(
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .background(Color(0xFF26242D), RoundedCornerShape(8.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    if (match.scoreHome != null && match.scoreAway != null) {
                        Text(
                            text = "${match.scoreHome}  -  ${match.scoreAway}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            color = if (match.isLive) accentRed else Color.White
                        )
                    } else {
                        Text(
                            text = "VS",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = accentRed
                        )
                    }
                }

                // Away Team
                Text(
                    text = match.teamAway,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            HorizontalDivider(color = Color.White.copy(alpha = 0.08f))

            Spacer(modifier = Modifier.height(10.dp))

            // Footer (Time / Channel & Stream button)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "القناة الناقلة: ${match.channelName}",
                        fontSize = 11.sp,
                        color = Color.LightGray,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "التوقيت / الحالة: ${match.time}",
                        fontSize = 11.sp,
                        color = if (match.isLive) accentRed else Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (match.streamUrl.isNotEmpty()) {
                    Button(
                        onClick = onClickWatch,
                        colors = ButtonDefaults.buttonColors(containerColor = accentRed),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayCircle,
                            contentDescription = "شاهد",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "شاهد البث",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
