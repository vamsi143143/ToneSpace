package com.example.tonespace.ui.welcome

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tonespace.R

@Composable
fun VibeResultScreen(
    vibe: String,
    onColorPaletteClick: () -> Unit,
    onFurnitureClick: () -> Unit,
    onDoneClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Title
        Text(
            text = "$vibe Style Result",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Result image
        Image(
            painter = painterResource(id = R.drawable.sample_room),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Summary
        Text(
            text = "Design Summary",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text =
                "• Color palette suited for $vibe mood\n" +
                        "• Furniture aligned with $vibe aesthetics\n" +
                        "• Lighting and decor optimized",
            color = Color.Gray,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Color Palette button
        Button(
            onClick = onColorPaletteClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF6D4C41)
            )
        ) {
            Text("View Color Palette", color = Color.White)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Furniture Recommendation button
        OutlinedButton(
            onClick = onFurnitureClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(26.dp)
        ) {
            Text("Furniture Recommendations")
        }

        Spacer(modifier = Modifier.weight(1f))

        // Back to Home
        Button(
            onClick = onDoneClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(26.dp)
        ) {
            Text("Back to Home")
        }
    }
}
