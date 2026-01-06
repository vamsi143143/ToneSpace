package com.example.tonespace.ui.welcome

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
fun AIDesignExplanationScreen(
    vibe: String,
    onNextClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(20.dp)
    ) {

        Text(
            text = "AI Design Insight",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF6F6F6)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {

                Text(
                    text = "Selected Vibe",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Text(
                    text = vibe,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = when (vibe.lowercase()) {
                        "minimal" ->
                            "This design focuses on clean lines, neutral tones, and uncluttered spaces to create calm and clarity."
                        "cozy" ->
                            "Warm textures, soft lighting, and comfortable furniture create a welcoming atmosphere."
                        "luxury" ->
                            "Rich materials, statement lighting, and refined finishes elevate the space."
                        "playful" ->
                            "Bold colors, creative decor, and dynamic layouts add personality and energy."
                        "focus" ->
                            "Simple layouts and muted tones reduce distractions and enhance productivity."
                        "romantic" ->
                            "Soft colors, gentle lighting, and elegant decor set a soothing mood."
                        else ->
                            "This style balances color, layout, and decor to match your selected vibe."
                    },
                    fontSize = 14.sp,
                    color = Color.DarkGray,
                    lineHeight = 20.sp
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onNextClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF19B5FE))
        ) {
            Text("View Furniture", color = Color.White, fontSize = 16.sp)
        }
    }
}
