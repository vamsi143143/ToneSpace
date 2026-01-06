package com.example.tonespace.ui.welcome

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tonespace.network.HelpArticle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpCenterScreen(
    onBackClick: () -> Unit,
    onManageSubscriptionClick: () -> Unit // This seems unused, but kept for compatibility
) {
    val viewModel: ProfileViewModel = viewModel(factory = ProfileViewModelFactory(SessionManager(LocalContext.current)))
    val articles by viewModel.helpArticlesState.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    // Fetch articles when the screen is first launched
    LaunchedEffect(Unit) {
        viewModel.fetchHelpArticles()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Help Center") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Text("<", fontSize = 22.sp)
                    }
                }
            )
        }
    ) { pad ->
        Box(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
        ) {
            when (uiState) {
                is UiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is UiState.Error -> {
                    Text(
                        text = (uiState as UiState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center).padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
                else -> {
                    if (articles.isEmpty()) {
                        Text(
                            text = "Could not load help articles.",
                            modifier = Modifier.align(Alignment.Center).padding(16.dp),
                            textAlign = TextAlign.Center
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            items(articles) { article ->
                                ExpandableFAQItem(article = article)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ExpandableFAQItem(article: HelpArticle) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded }
            .padding(vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = article.title,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = if (isExpanded) "-" else "+",
                fontSize = 20.sp,
                fontWeight = FontWeight.Light
            )
        }

        // The answer, which is only visible when expanded
        AnimatedVisibility(visible = isExpanded) {
            Column {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = article.content,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }
        }
        Divider(modifier = Modifier.padding(top = 16.dp))
    }
}
