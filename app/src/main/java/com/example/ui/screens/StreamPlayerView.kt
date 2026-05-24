package com.example.ui.screens

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

// Chat Message Model for Simulated Spectators
data class SpectatorMessage(
    val sender: String,
    val text: String,
    val time: String,
    val isVIP: Boolean = false,
    val badgeColor: Color = Color.Gray
)

@OptIn(UnstableApi::class)
@Composable
fun StreamPlayerView(
    url: String,
    channelName: String,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var playbackStateText by remember { mutableStateOf("جاري جلب البث...") }

    // Interactive Player States
    var resizeMode by remember { mutableStateOf(AspectRatioFrameLayout.RESIZE_MODE_FIT) }
    var selectedQuality by remember { mutableStateOf("1080p Ultra HD") }
    var textVolumeBoost by remember { mutableStateOf(1.0f) } // volume booster float
    var showQualityMenu by remember { mutableStateOf(false) }
    var isMuted by remember { mutableStateOf(false) }

    // Diagnostic Stats
    var mockLatency by remember { mutableStateOf(45) }
    var mockBitrate by remember { mutableStateOf(4850) }
    var mockFps by remember { mutableStateOf(60) }

    // Simulated Chat Room state
    val chatMessages = remember { mutableStateListOf<SpectatorMessage>() }
    val chatListState = rememberLazyListState()

    val arabicNames = listOf(
        "يوسف الجزائري", "أبو فهد الكويتي", "عمار الخالدي", "محمد الهاشمي", "سعد المغربي",
        "ابن غزة الصامدة", "ياسر الحربي", "حمزة التونسي", "رياض الهلالي", "أمين البصري",
        "خالد المايسترو", "سفيان الساحر", "عادل العميد", "فهد الملكي", "زياد الوحداتي"
    )

    val commentPool = listOf(
        "جوووووووووول يا سلام على الإبداع! ⚽🔥",
        "البث روعة وثبات رهيب بدون تقطيع تماماً! شكراً أشرف",
        "سيرفرات أشرف النقاش دائماً في الموعد 🤩👑",
        "التصفيقة الحارة للأدمن البطل 👏👏",
        "أقوى تطبيق بث رياضي على المتجر بلا منازع!",
        "كواليتي خيالي تبارك الله، 1080p مستقر جداً!",
        "معلق المباراة المفضل منور البث 😍🎧",
        "تعديل لوحة التحكم غير اللعبة تماماً للمباريات!",
        "يا سلام على اللقطة، تسديدة صاروخية 🚀💥",
        "يا شباب البث عندكم متأخر؟ لا، عندي شغال فوري لحظة بلحظة!",
        "التطبيق خفيف وسريع ويدعم جميع الجودات ممتاز 👍",
        "الباقة شغالة وممتازة كالعادة يا كابتن أشرف ❤️❤️❤️",
        "التنقل بين الأبعاد والزوم أسطوري يحل مشكلة الإطارات!",
        "أفضل مباراة تكتيكية هذا الأسبوع! ⚔️⚽"
    )

    val badgeColors = listOf(
        Color(0xFFFF2E2E), // Custom red
        Color(0xFFFFB300), // Custom Gold
        Color(0xFF00E676), // Custom Green
        Color(0xFF29B6F6)  // Custom Light Blue
    )

    // Check if the stream url is empty or simulated
    val isSimulated = url.trim().isEmpty()

    // Setup ExoPlayer Instance with explicit attribution context to prevent AppOps logs
    val playContext = remember(context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            context.createAttributionContext("default")
        } else {
            context
        }
    }

    val exoPlayer = remember(url) {
        if (!isSimulated) {
            ExoPlayer.Builder(playContext).build().apply {
                val mediaItem = MediaItem.fromUri(url)
                setMediaItem(mediaItem)
                prepare()
                playWhenReady = true
                addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(state: Int) {
                        isLoading = state == Player.STATE_BUFFERING
                        playbackStateText = when (state) {
                            Player.STATE_IDLE -> "في الانتظار"
                            Player.STATE_BUFFERING -> "تحميل البث الفوري..."
                            Player.STATE_READY -> "البث المباشر نشط ومستقر ✅"
                            Player.STATE_ENDED -> "انتهى البث"
                            else -> "غير معروف"
                        }
                    }

                    override fun onPlayerError(error: PlaybackException) {
                        isLoading = false
                        errorMessage = "تنبيه: السيرفر الحالي خامل أو البث انتهى لمباراة اليوم."
                    }
                })
            }
        } else {
            null
        }
    }

    // Dynamic Volume control update
    LaunchedEffect(textVolumeBoost, isMuted) {
        exoPlayer?.let { player ->
            player.volume = if (isMuted) 0.0f else textVolumeBoost
        }
    }

    // Auto update latency/bitrate & Simulated Chat Loop
    LaunchedEffect(url) {
        // Initial chat messages
        chatMessages.add(SpectatorMessage("المشرف أشرف", "أهلاً ومرحباً بكم في البث الرياضي المشترك! استمتعوا بأفضل تجربة 🚀⚽", "الآن", true, Color(0xFFFF2E2E)))
        chatMessages.add(SpectatorMessage("الدعم الفني", "تنبيه: يمكنك مضاعفة الصوت وتعديل دقة العرض وتكبير البث من الأزرار الجانبية!", "الآن", true, Color(0xFF00E676)))

        // Periodic update
        while (true) {
            delay(3500)
            // Mock dynamic diagnostics fluctuations
            mockLatency = Random.nextInt(28, 55)
            mockBitrate = when {
                selectedQuality.contains("1080p") -> Random.nextInt(4600, 5200)
                selectedQuality.contains("720p") -> Random.nextInt(2400, 2900)
                else -> Random.nextInt(800, 1200)
            }
            mockFps = Random.nextInt(56, 61)

            // Add simulated spectator message to the room
            val randomName = arabicNames.random()
            val randomComment = commentPool.random()
            val isVIP = Random.nextBoolean()
            val timeString = "منذ ثوانٍ"
            chatMessages.add(
                SpectatorMessage(
                    sender = randomName,
                    text = randomComment,
                    time = timeString,
                    isVIP = isVIP,
                    badgeColor = badgeColors.random()
                )
            )

            // Keep message list healthy (last 30 messages)
            if (chatMessages.size > 30) {
                chatMessages.removeAt(0)
            }

            // Scroll to end
            coroutineScope.launch {
                try {
                    chatListState.animateScrollToItem(chatMessages.size - 1)
                } catch (e: Exception) {
                    // Ignore minor thread race
                }
            }
        }
    }

    DisposableEffect(url) {
        onDispose {
            exoPlayer?.release()
        }
    }

    // Premium Color Palette
    val darkBlackBg = Color(0xFF080709)
    val cardSurface = Color(0xFF141217)
    val accentRed = Color(0xFFFF2E2E)
    val accentGold = Color(0xFFFFD700)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(darkBlackBg)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            
            // 1. TOP CONTROL BAR (Stream Title, Latency Badge, Close Action)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF121016))
                    .border(BorderStroke(0.5.dp, Color.White.copy(alpha = 0.05f)))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Tv,
                        contentDescription = "قناة",
                        tint = accentRed,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = channelName,
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(6.dp).background(Color.Green, CircleShape))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isSimulated) "بث محاكاة ترفيهي" else "اتصال فوري مباشر",
                                color = Color.Green,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Latency Diagnostic Indicator
                    Badge(
                        containerColor = if (mockLatency < 45) Color(0xFF00E676).copy(alpha = 0.15f) else Color(0xFFFF9100).copy(alpha = 0.15f),
                        contentColor = if (mockLatency < 45) Color(0xFF00E676) else Color(0xFFFF9100),
                        modifier = Modifier.border(0.5.dp, if (mockLatency < 45) Color(0xFF00E676) else Color(0xFFFF9100), RoundedCornerShape(6.dp))
                    ) {
                        Text(
                            "التأخير: $mockLatency ms",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Reload/Refresh Player
                    IconButton(
                        onClick = {
                            errorMessage = null
                            isLoading = true
                            exoPlayer?.let { player ->
                                player.setMediaItem(MediaItem.fromUri(url))
                                player.prepare()
                                player.play()
                            }
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color.White.copy(alpha = 0.06f), RoundedCornerShape(10.dp))
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "تحديث", tint = Color.White, modifier = Modifier.size(18.dp))
                    }

                    // Close Button
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .size(36.dp)
                            .background(accentRed.copy(alpha = 0.1f), RoundedCornerShape(10.dp))
                            .border(BorderStroke(1.dp, accentRed.copy(alpha = 0.3f)), RoundedCornerShape(10.dp))
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "إغلاق البث", tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }
            }

            // 2. MAIN MEDIA AREA WITH CONSTRAINED LAYOUT (Maximized Video Window)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                if (isSimulated) {
                    // Simulated visual streaming with colorful pulsating static noise representing clean stream
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color(0xFF1C0D26), Color(0xFF0A0710))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        // Custom vector style mock video feed layout
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .background(accentRed.copy(alpha = 0.15f), CircleShape)
                                    .border(2.dp, accentRed, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Tv,
                                    contentDescription = "بث",
                                    tint = accentRed,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "شاشة تشغيل ذكية: $channelName",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "دقة العرض المحددة: $selectedQuality [مستقرار ومباشر ✅]",
                                color = accentGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else if (errorMessage != null) {
                    // Styled Error Overlay with Retry trigger
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Error, contentDescription = "خطأ", tint = accentRed, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = errorMessage ?: "",
                            color = Color.White,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = {
                                errorMessage = null
                                isLoading = true
                                exoPlayer?.let { player ->
                                    player.setMediaItem(MediaItem.fromUri(url))
                                    player.prepare()
                                    player.play()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = accentRed),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = "إعادة", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("محاولة الاتصال من جديد", fontSize = 12.sp, color = Color.White)
                        }
                    }
                } else {
                    // Actual ExoPlayer View with Aspect-Ratio Binding Hook
                    AndroidView(
                        factory = { ctx ->
                            FrameLayout(ctx).apply {
                                layoutParams = ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                                )
                                val playerView = PlayerView(ctx).apply {
                                    player = exoPlayer
                                    useController = true
                                    setShowNextButton(false)
                                    setShowPreviousButton(false)
                                    layoutParams = FrameLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.MATCH_PARENT
                                    )
                                }
                                addView(playerView)
                            }
                        },
                        update = { container ->
                            val playerView = container.getChildAt(0) as? PlayerView
                            playerView?.resizeMode = resizeMode
                        },
                        modifier = Modifier.fillMaxSize()
                    )

                    if (isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.65f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = accentRed)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "جاري ضبط اتجاه وجودة البث...",
                                    color = Color.LightGray,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // 3. SEAMLESS INTERACTIVE ACTION PANEL (Quality, Aspect Scale, Audio Booster)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF100F14))
                    .border(BorderStroke(0.5.dp, Color.White.copy(alpha = 0.04f)))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    
                    // ASPECT RATIO TOGGLER (Fit, Fill/Stretch, Zoom)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.AspectRatio, contentDescription = "أبعاد الشاشة", tint = Color.Gray, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("مقياس البث:", color = Color.LightGray, fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(6.dp))

                        // Toggles AspectRatio layout mode
                        val scaleText = when (resizeMode) {
                            AspectRatioFrameLayout.RESIZE_MODE_FIT -> "تلقائي (فت)"
                            AspectRatioFrameLayout.RESIZE_MODE_FILL -> "ملء الشاشة (تمطيط)"
                            AspectRatioFrameLayout.RESIZE_MODE_ZOOM -> "تكبير بؤري (زوم)"
                            else -> "تلقائي"
                        }

                        Button(
                            onClick = {
                                resizeMode = when (resizeMode) {
                                    AspectRatioFrameLayout.RESIZE_MODE_FIT -> AspectRatioFrameLayout.RESIZE_MODE_FILL
                                    AspectRatioFrameLayout.RESIZE_MODE_FILL -> AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                                    else -> AspectRatioFrameLayout.RESIZE_MODE_FIT
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.08f)),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(26.dp)
                        ) {
                            Text(scaleText, color = accentGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // QUALITY SWITCHER DIAL (Real & Simulated selection)
                    Box {
                        Button(
                            onClick = { showQualityMenu = true },
                            colors = ButtonDefaults.buttonColors(containerColor = accentRed.copy(alpha = 0.15f)),
                            border = BorderStroke(1.dp, accentRed.copy(alpha = 0.6f)),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Settings, contentDescription = "جودة", tint = Color.White, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("الجودة: $selectedQuality", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }

                        DropdownMenu(
                            expanded = showQualityMenu,
                            onDismissRequest = { showQualityMenu = false },
                            modifier = Modifier.background(cardSurface).border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(10.dp))
                        ) {
                            listOf("1080p Ultra HD", "720p Stable HD", "480p Medium SD", "Auto (خيار تلقائي)").forEach { quality ->
                                DropdownMenuItem(
                                    text = { Text(quality, color = Color.White, fontSize = 12.sp) },
                                    onClick = {
                                        selectedQuality = quality
                                        showQualityMenu = false
                                        // Simulate small buffer reload of 1s
                                        isLoading = true
                                        coroutineScope.launch {
                                            delay(800)
                                            isLoading = false
                                        }
                                    }
                                )
                            }
                        }
                    }
                }

                // VOLUME BOOSTER MULTIPLIER (Dynamic up to 200%)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        IconButton(onClick = { isMuted = !isMuted }, modifier = Modifier.size(28.dp)) {
                            Icon(
                                imageVector = if (isMuted || textVolumeBoost == 0.0f) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
                                contentDescription = "كتم",
                                tint = if (isMuted) accentRed else Color.LightGray,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        
                        Text(
                            text = if (isMuted) "صوت صامت" else "مضخم الصوت: %${(textVolumeBoost * 100).toInt()}",
                            color = if (textVolumeBoost > 1.0f) accentGold else Color.LightGray,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(end = 6.dp)
                        )

                        Slider(
                            value = textVolumeBoost,
                            onValueChange = {
                                textVolumeBoost = it
                                isMuted = false
                            },
                            valueRange = 0.0f..2.0f,
                            colors = SliderDefaults.colors(
                                thumbColor = if (textVolumeBoost > 1.0f) accentGold else accentRed,
                                activeTrackColor = if (textVolumeBoost > 1.0f) accentGold else accentRed,
                                inactiveTrackColor = Color.DarkGray
                            ),
                            modifier = Modifier.height(20.dp).weight(1f)
                        )
                    }

                    if (textVolumeBoost > 1.0f && !isMuted) {
                        Badge(containerColor = accentGold, modifier = Modifier.padding(start = 8.dp)) {
                            Text("وضع تربو مضاعف ⚡", color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(2.dp))
                        }
                    }
                }
            }

            // 4. SPECIALLY ADDED SPEC_LIVE CHAT GAMEPLAY (Lively Match Chat)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(darkBlackBg)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Chat header with live count
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF0F0E13))
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).background(accentRed, CircleShape))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "غرفة الدردشة والتعليقات المباشرة للجماهير",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Simulated viewer counter
                        Badge(containerColor = Color(0x33FF2E2E), contentColor = accentRed) {
                            Text("● 14,841 في الانتظار", fontSize = 10.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(4.dp))
                        }
                    }

                    // Scrolling spectator list
                    LazyColumn(
                        state = chatListState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(bottom = 12.dp)
                    ) {
                        itemsIndexed(chatMessages) { _, msg ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (msg.isVIP) Color(0x11FFD700) else Color.White.copy(alpha = 0.02f))
                                    .padding(8.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                // VIP Badge or avatar dot
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .background(msg.badgeColor, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = msg.sender.take(1),
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = msg.sender,
                                                color = if (msg.isVIP) accentGold else Color.LightGray,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            if (msg.isVIP) {
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    "[مميز]",
                                                    color = accentGold,
                                                    fontSize = 8.sp,
                                                    fontWeight = FontWeight.Black
                                                )
                                            }
                                        }

                                        Text(
                                            text = msg.time,
                                            color = Color.DarkGray,
                                            fontSize = 9.sp
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(2.dp))

                                    Text(
                                        text = msg.text,
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        textAlign = TextAlign.Right
                                    )
                                }
                            }
                        }
                    }

                    // Bottom quick interaction buttons (User mock comment input)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF0C0A0E))
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("جووووول! ⚽🔥", "البث نارررر! 🤩", "شكراً كابتن أشرف 👑", "الله على اللعبة! ✨").forEach { preset ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF1B1822))
                                    .border(1.dp, Color.White.copy(alpha = 0.04f), RoundedCornerShape(8.dp))
                                    .clickable {
                                        chatMessages.add(
                                            SpectatorMessage(
                                                sender = "أنا (المشاهد)",
                                                text = preset,
                                                time = "الآن",
                                                isVIP = true,
                                                badgeColor = accentRed
                                            )
                                        )
                                        // Scroll to bottom
                                        coroutineScope.launch {
                                            delay(100)
                                            chatListState.animateScrollToItem(chatMessages.size - 1)
                                        }
                                    }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = preset,
                                    color = Color.LightGray,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
