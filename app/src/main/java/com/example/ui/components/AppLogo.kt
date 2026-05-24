package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AppLogo(
    modifier: Modifier = Modifier,
    showText: Boolean = true,
    fontSize: Int = 18
) {
    val accentRed = Color(0xFFFF2E2E)
    val accentGold = Color(0xFFFFD700)
    val darkBlackBg = Color(0xFF16141A)

    Row(
        modifier = modifier
            .wrapContentSize()
            .clip(RoundedCornerShape(12.dp))
            .background(darkBlackBg.copy(alpha = 0.6f))
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        // High Polished TV Logo with floating Soccer Ball
        Box(
            modifier = Modifier
                .size(34.dp)
                .background(
                    Brush.linearGradient(
                        colors = listOf(accentRed, Color(0xFF8B0000))
                    ),
                    shape = RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Tv,
                contentDescription = "تلفاز",
                tint = Color.White.copy(alpha = 0.85f),
                modifier = Modifier.size(24.dp)
            )
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .background(Color.Black, shape = RoundedCornerShape(3.dp))
                    .align(Alignment.BottomEnd)
                    .offset(x = (-1).dp, y = (-1).dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SportsSoccer,
                    contentDescription = "كرة قدم",
                    tint = accentRed,
                    modifier = Modifier.size(11.dp)
                )
            }
        }

        if (showText) {
            Spacer(modifier = Modifier.width(10.dp))
            Column(verticalArrangement = Arrangement.Center) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "أشرف",
                        color = Color.White,
                        fontSize = fontSize.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "النقاش",
                        color = accentRed,
                        fontSize = fontSize.sp,
                        fontWeight = FontWeight.Black
                    )
                }
                Text(
                    text = "بث حصري ومستمر ⚽",
                    color = Color.LightGray.copy(alpha = 0.7f),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 11.sp
                )
            }
        }
    }
}
