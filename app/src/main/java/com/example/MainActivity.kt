package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.AdminPanelSettings
import com.example.model.AppRepository
import com.example.model.Channel
import com.example.ui.screens.AdminView
import com.example.ui.screens.AssistantView
import com.example.ui.screens.ChannelsView
import com.example.ui.screens.MatchesView
import com.example.ui.screens.StreamPlayerView
import com.example.ui.theme.MyApplicationTheme

enum class YacineTab {
    LIVE_TV,      // القنوات المباشرة
    LIVE_EVENTS,  // جدول المباريات
    AI_ASSISTANT, // مساعد الذكاء الاصطناعي
    ADMIN_PANEL   // لوحة التحكم المشرف
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize dynamic database repository
        AppRepository.init(applicationContext)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme(darkTheme = false, dynamicColor = false) {
                var currentTab by remember { mutableStateOf(YacineTab.LIVE_TV) }
                var activeStream by remember { mutableStateOf<Channel?>(null) }

                // Handle back events gracefully (e.g. close active stream)
                BackHandler(enabled = activeStream != null) {
                    activeStream = null
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        NavigationBar(
                            containerColor = Color(0xFF121016), // Dark Matte Black color
                            tonalElevation = 10.dp
                        ) {
                            NavigationBarItem(
                                selected = currentTab == YacineTab.LIVE_TV,
                                onClick = { currentTab = YacineTab.LIVE_TV },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.Tv,
                                        contentDescription = "القنوات"
                                    )
                                },
                                label = {
                                    Text(
                                        "البث المباشر",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color(0xFFFF2E2E), // Brilliant Accent Red
                                    selectedTextColor = Color(0xFFFF2E2E),
                                    unselectedIconColor = Color(0xFF9E9EA8),
                                    unselectedTextColor = Color(0xFF9E9EA8),
                                    indicatorColor = Color(0xFFFF2E2E).copy(alpha = 0.15f)
                                )
                            )

                            NavigationBarItem(
                                selected = currentTab == YacineTab.LIVE_EVENTS,
                                onClick = { currentTab = YacineTab.LIVE_EVENTS },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.EventNote,
                                        contentDescription = "جدول المباريات"
                                    )
                                },
                                label = {
                                    Text(
                                        "مباريات اليوم",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color(0xFFFF2E2E),
                                    selectedTextColor = Color(0xFFFF2E2E),
                                    unselectedIconColor = Color(0xFF9E9EA8),
                                    unselectedTextColor = Color(0xFF9E9EA8),
                                    indicatorColor = Color(0xFFFF2E2E).copy(alpha = 0.15f)
                                )
                            )

                            NavigationBarItem(
                                selected = currentTab == YacineTab.AI_ASSISTANT,
                                onClick = { currentTab = YacineTab.AI_ASSISTANT },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.SupportAgent,
                                        contentDescription = "مساعد ذكي"
                                    )
                                },
                                label = {
                                    Text(
                                        "مساعد أشرف",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color(0xFFFF2E2E),
                                    selectedTextColor = Color(0xFFFF2E2E),
                                    unselectedIconColor = Color(0xFF9E9EA8),
                                    unselectedTextColor = Color(0xFF9E9EA8),
                                    indicatorColor = Color(0xFFFF2E2E).copy(alpha = 0.15f)
                                )
                            )

                            NavigationBarItem(
                                selected = currentTab == YacineTab.ADMIN_PANEL,
                                onClick = { currentTab = YacineTab.ADMIN_PANEL },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.AdminPanelSettings,
                                        contentDescription = "لوحة التحكم"
                                    )
                                },
                                label = {
                                    Text(
                                        "الأدمن",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color(0xFFFF2E2E),
                                    selectedTextColor = Color(0xFFFF2E2E),
                                    unselectedIconColor = Color(0xFF9E9EA8),
                                    unselectedTextColor = Color(0xFF9E9EA8),
                                    indicatorColor = Color(0xFFFF2E2E).copy(alpha = 0.15f)
                                )
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        // Switch between different bottom states
                        when (currentTab) {
                            YacineTab.LIVE_TV -> {
                                ChannelsView(
                                    onSelectChannel = { channel ->
                                        activeStream = channel
                                    }
                                )
                            }
                            YacineTab.LIVE_EVENTS -> {
                                MatchesView(
                                    onSelectStream = { channel ->
                                        activeStream = channel
                                    }
                                )
                            }
                            YacineTab.AI_ASSISTANT -> {
                                AssistantView()
                            }
                            YacineTab.ADMIN_PANEL -> {
                                AdminView()
                            }
                        }

                        // Full Screen media streamer overlay when stream is clicked
                        activeStream?.let { stream ->
                            StreamPlayerView(
                                url = stream.streamUrl,
                                channelName = stream.name,
                                onClose = { activeStream = null },
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }
    }
}
