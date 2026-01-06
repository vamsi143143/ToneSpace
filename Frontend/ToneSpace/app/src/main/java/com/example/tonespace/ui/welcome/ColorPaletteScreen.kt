package com.example.tonespace.ui.welcome

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ColorPaletteScreen(
    vibe: String,
    onBackClick: () -> Unit,
    onFinishClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(20.dp)
    ) {

        Text(
            text = "Color Palette",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Recommended colors for $vibe style",
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(24.dp))

        val colors = when (vibe.lowercase()) {
            "minimal" -> listOf(
                Color(0xFFEDEDED),
                Color(0xFFBDBDBD),
                Color(0xFF8D8D8D)
            )
            "cozy" -> listOf(
                Color(0xFFFFE0B2),
                Color(0xFFD7CCC8),
                Color(0xFFBCAAA4)
            )
            "luxury" -> listOf(
                Color(0xFF212121),
                Color(0xFFD4AF37),
                Color(0xFF424242)
            )
            "playful" -> listOf(
                Color(0xFFFF7043),
                Color(0xFF29B6F6),
                Color(0xFFAB47BC)
            )
            "focus" -> listOf(
                Color(0xFFE3F2FD),
                Color(0xFF90CAF9),
                Color(0xFF64B5F6)
            )
            "romantic" -> listOf(
                Color(0xFFFFCDD2),
                Color(0xFFF8BBD0),
                Color(0xFFD1C4E9)
            )
            else -> listOf(
                Color.Gray,
                Color.LightGray,
                Color.DarkGray
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            colors.forEach { color ->
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(color, CircleShape)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedButton(
                onClick = onBackClick,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text("Back")
            }

            Button(
                onClick = onFinishClick,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF19B5FE)
                )
            ) {
                Text("Finish", color = Color.White)
            }
        }
    }
}
