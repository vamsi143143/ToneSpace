package com.example.tonespace.ui.welcome

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tonespace.R

@Composable
fun HomeScreen(
    onStartAnalyzing: () -> Unit,
    onExploreMoodThemes: () -> Unit,
    onMySavedDesigns: () -> Unit,
    onLayout: () -> Unit,
    onSaved: () -> Unit,
    onProfile: () -> Unit
) {
    Scaffold(
        containerColor = Color(0xFF2B1708),
        bottomBar = {
            BottomNavigationBar(
                onLayout = onLayout,
                onSaved = onSaved,
                onProfile = onProfile
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {

            Text(
                text = "Welcome to ToneSpace",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    Text("Analyze a New Room", fontWeight = FontWeight.Bold)

                    Text(
                        "Upload a photo to get AI-powered design suggestions",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = onStartAnalyzing,
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFB37A)
                        )
                    ) {
                        Text("Start Analyzing")
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            ImageCard(
                image = R.drawable.project_livingroom,
                title = "Explore Mood Themes",
                subtitle = "Find your perfect style",
                onClick = onExploreMoodThemes
            )

            // ✅ Uses onMySavedDesigns
            ImageCard(
                image = R.drawable.project_bedroom,
                title = "My Saved Designs",
                subtitle = "Revisit your projects",
                onClick = onMySavedDesigns
            )
        }
    }
}

@Composable
private fun ImageCard(
    image: Int,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .clickable { onClick() }
    ) {
        Column {
            Image(
                painter = painterResource(image),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            )
            Column(modifier = Modifier.padding(12.dp)) {
                Text(title, fontWeight = FontWeight.SemiBold)
                Text(subtitle, fontSize = 13.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
private fun BottomNavigationBar(
    onLayout: () -> Unit,
    onSaved: () -> Unit,
    onProfile: () -> Unit
) {
    NavigationBar(containerColor = Color(0xFF3A1F0F)) {

        NavigationBarItem(
            selected = true,
            onClick = {}, // Home (current)
            icon = { Icon(Icons.Filled.Home, null) },
            label = { Text("Home") }
        )

        NavigationBarItem(
            selected = false,
            onClick = onLayout,
            icon = { Icon(Icons.Filled.List, null) },
            label = { Text("Layout") }
        )

        // ✅ Uses onSaved
        NavigationBarItem(
            selected = false,
            onClick = onSaved,
            icon = { Icon(Icons.Filled.Star, null) },
            label = { Text("Saved") }
        )

        NavigationBarItem(
            selected = false,
            onClick = onProfile,
            icon = { Icon(Icons.Filled.Person, null) },
            label = { Text("Profile") }
        )
    }
}
