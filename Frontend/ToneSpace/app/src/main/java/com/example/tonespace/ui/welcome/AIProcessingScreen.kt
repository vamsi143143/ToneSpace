package com.example.tonespace.ui.welcome

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first

@Composable
fun AIProcessingScreen(
    onAnalysisComplete: () -> Unit
) {
    var progress by remember { mutableStateOf(0f) }

    // Safely update the navigation callback
    val safeNavigate by rememberUpdatedState(onAnalysisComplete)

    // Effect to handle the progress simulation and navigation
    LaunchedEffect(Unit) {
        // Animate the progress from 0 to 1
        while (progress < 1f) {
            delay(40)
            progress = (progress + 0.01f).coerceAtMost(1.0f)
        }

        // Wait for the composition to be stable with progress at 1f
        snapshotFlow { progress }
            .filter { it >= 1f }
            .first()

        // Navigate after the progress bar is compositionally complete
        safeNavigate()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFE6DC))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Image(
            painter = painterResource(id = R.drawable.ic_ai_analysis),
            contentDescription = "AI Analysis",
            modifier = Modifier.size(220.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Analyzing Your Space",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Our AI is working its magic to find the perfect tone for your room.",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = Color(0xFF3B82F6),
            trackColor = Color(0xFFE5E7EB)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Scanning room layout ${(progress * 100).toInt()}%",
            fontSize = 12.sp,
            color = Color.DarkGray
        )
    }
}
