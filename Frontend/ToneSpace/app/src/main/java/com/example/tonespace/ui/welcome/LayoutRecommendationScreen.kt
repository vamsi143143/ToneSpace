package com.example.tonespace.ui.welcome

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tonespace.R

@Composable
fun LayoutRecommendationsScreen(
    onBackClick: () -> Unit,
    onNextClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        // 🔙 Back Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Black
                )
            }
        }

        // 🛋 Furniture Image
        Image(
            painter = painterResource(id = R.drawable.furniture_layout),
            contentDescription = "Furniture Layout",
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(28.dp))

        // 📝 Title
        Text(
            text = "Furniture Layout\nRecommendations",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.weight(1f))

        // 🔘 Page Indicator
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            IndicatorDot(false)
            IndicatorDot(false)
            IndicatorDot(true)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ➡ Next Button
        Button(
            onClick = onNextClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .height(54.dp),
            shape = RoundedCornerShape(30.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF3B4BFF)
            )
        ) {
            Text(
                text = "Next",
                fontSize = 16.sp,
                color = Color.White
            )
        }
    }
}

@Composable
private fun IndicatorDot(isActive: Boolean) {
    Box(
        modifier = Modifier
            .padding(horizontal = 6.dp)
            .size(if (isActive) 10.dp else 8.dp)
            .background(
                color = if (isActive) Color(0xFF3B4BFF) else Color.LightGray,
                shape = CircleShape
            )
    )
}
