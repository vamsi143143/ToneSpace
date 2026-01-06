package com.example.tonespace.ui.welcome

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tonespace.R

data class Vibe(val name: String, val imageRes: Int)

@Composable
fun AIAnalysisScreen(
    onGenerateIdeasClick: (String) -> Unit,
    onSkipClick: () -> Unit,
    designViewModel: DesignViewModel = viewModel(factory = DesignViewModelFactory())
) {
    val vibes = listOf(
        Vibe("Calm", R.drawable.vibe_calm),
        Vibe("Cozy", R.drawable.vibe_cozy),
        Vibe("Energetic", R.drawable.vibe_energetic),
        Vibe("Focus", R.drawable.vibe_focus),
        Vibe("Luxury", R.drawable.vibe_luxury),
        Vibe("Minimal", R.drawable.vibe_minimal),
        Vibe("Playful", R.drawable.vibe_playful),
        Vibe("Romantic", R.drawable.vibe_romantic)
    )

    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "Our AI has analyzed your space.",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "What kind of vibe are you going for?",
            fontSize = 16.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(vibes) { vibe ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable {
                        designViewModel.setSelectedVibe(vibe.name)
                        onGenerateIdeasClick(vibe.name)
                    }
                ) {
                    Image(
                        // CORRECTED: Use the specific image for each vibe
                        painter = painterResource(id = vibe.imageRes),
                        contentDescription = vibe.name,
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(vibe.name, fontWeight = FontWeight.Medium)
                }
            }
        }

        Button(
            onClick = { 
                val defaultVibe = vibes.first().name
                designViewModel.setSelectedVibe(defaultVibe)
                onGenerateIdeasClick(defaultVibe)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Generate Ideas")
        }

        TextButton(onClick = onSkipClick, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text("Skip for now")
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}
