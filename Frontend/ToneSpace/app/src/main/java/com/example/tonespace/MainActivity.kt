package com.example.tonespace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tonespace.ui.splash.SplashScreen
import com.example.tonespace.ui.theme.ToneSpaceTheme
import com.example.tonespace.ui.welcome.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ToneSpaceTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val designViewModel: DesignViewModel = viewModel(factory = DesignViewModelFactory())

                    NavHost(navController = navController, startDestination = "splash") {

                        composable("splash") {
                            SplashScreen(onNavigateNext = {
                                navController.navigate("welcome") {
                                    popUpTo("splash") { inclusive = true }
                                }
                            })
                        }

                        composable("welcome") {
                            WelcomeScreen(onNextClick = { navController.navigate("Tour") })
                        }
                        composable("tour") {
                            TourScreen(
                                onBackClick = { navController.popBackStack() },
                                onNextClick = { navController.navigate("layout") }
                            )
                        }

                        composable("layout") {
                            LayoutRecommendationsScreen(
                                onBackClick = { navController.popBackStack() },
                                onNextClick = { navController.navigate("login") }
                            )
                        }
                        composable("login") {
                            LoginScreen(
                                onLoginClick = {
                                    navController.navigate("home") {
                                        popUpTo("welcome") { inclusive = true }
                                    }
                                },
                                onRegisterClick = { navController.navigate("register") },
                                onForgotPasswordClick = { navController.navigate("forgot") }
                            )
                        }

                        composable("register") {
                            RegisterScreen(
                                onRegisterClick = { navController.popBackStack() },
                                onLoginClick = { navController.popBackStack() }
                            )
                        }

                        // Restored original forgot password navigation
                        composable("forgot") {
                            ForgotPasswordScreen(
                                onBackClick = { navController.popBackStack() }
                            )
                        }

                        composable("home") {
                            HomeScreen(
                                onStartAnalyzing = { navController.navigate("photo_tips") },
                                onExploreMoodThemes = { navController.navigate("ai_analysis") },
                                onMySavedDesigns = { navController.navigate("saved") },
                                onLayout = { navController.navigate("furniture/Minimal") },
                                onSaved = { navController.navigate("saved") },
                                onProfile = { navController.navigate("profile") }
                            )
                        }

                        composable("photo_tips") {
                            HowToTakePhotoScreen(
                                onCloseClick = { navController.popBackStack() },
                                onGotItClick = { navController.navigate("new_design") }
                            )
                        }

                        composable("new_design") {
                            NewDesignScreen(
                                onCloseClick = { navController.popBackStack() },
                                onImagePicked = { navController.navigate("upload_success") },
                                designViewModel = designViewModel
                            )
                        }

                        composable("upload_success") {
                            UploadSuccessScreen(
                                onCloseClick = { navController.popBackStack("home", false) },
                                onGenerateIdeasClick = { navController.navigate("ai_processing") },
                                onUploadDifferentPhotoClick = { navController.popBackStack() },
                                designViewModel = designViewModel
                            )
                        }

                        composable("ai_processing") {
                            AIProcessingScreen(
                                onAnalysisComplete = {
                                    navController.navigate("ai_analysis") {
                                        popUpTo("ai_processing") { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable("ai_analysis") {
                            AIAnalysisScreen(
                                onGenerateIdeasClick = { vibe ->
                                    navController.navigate("ai_explanation/$vibe")
                                },
                                onSkipClick = {
                                    designViewModel.setSelectedVibe("Minimal")
                                    navController.navigate("ai_explanation/Minimal")
                                },
                                designViewModel = designViewModel
                            )
                        }

                        composable("ai_explanation/{vibe}") { backStackEntry ->
                            val vibe = backStackEntry.arguments?.getString("vibe") ?: "Minimal"
                            AIDesignExplanationScreen(
                                vibe = vibe,
                                onNextClick = {
                                    navController.navigate("vibe_result/$vibe")
                                }
                            )
                        }

                        composable("vibe_result/{vibe}") { backStackEntry ->
                            val vibe = backStackEntry.arguments?.getString("vibe") ?: "Minimal"
                            VibeResultScreen(
                                vibe = vibe,
                                onColorPaletteClick = {
                                    navController.navigate("color_palette/$vibe")
                                },
                                onFurnitureClick = {
                                    navController.navigate("furniture/$vibe")
                                },
                                onDoneClick = {
                                    navController.navigate("home") {
                                        popUpTo("home") { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable("furniture/{vibe}") { backStackEntry ->
                            val vibe = backStackEntry.arguments?.getString("vibe") ?: "Minimal"
                            FurnitureRecommendationScreen(
                                vibe = vibe,
                                onBackClick = { navController.popBackStack() },
                                onNextClick = {
                                    navController.navigate("color_palette/$vibe")
                                }
                            )
                        }

                        composable("color_palette/{vibe}") { backStackEntry ->
                            val vibe = backStackEntry.arguments?.getString("vibe") ?: "Minimal"
                            ColorPaletteScreen(
                                vibe = vibe,
                                onBackClick = { navController.popBackStack() },
                                onFinishClick = {
                                    navController.navigate("design_comparison")
                                }
                            )
                        }

                        composable("design_comparison") {
                            DesignComparisonScreen(
                                onBackClick = { navController.popBackStack() },
                                onSaveClick = {
                                    navController.navigate("saved")
                                },
                                onRedesignClick = {
                                    navController.navigate("ai_analysis")
                                }
                            )
                        }

                        composable("saved") {
                            SavedScreen()
                        }

                        composable("profile") {
                            val context = LocalContext.current
                            ProfileScreen(
                                onBackClick = { navController.popBackStack() },
                                onPersonalInfoClick = { navController.navigate("personal_info") },
                                onChangePasswordClick = { navController.navigate("change_password") },
                                onNotificationsClick = { navController.navigate("notifications") },
                                onHelpCenterClick = { navController.navigate("help_center") },
                                onContactUsClick = { navController.navigate("contact_us") },
                                onLogoutClick = {
                                    val sessionManager = SessionManager(context)
                                    sessionManager.clear()
                                    navController.navigate("login") {
                                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable("personal_info") {
                            PersonalInformationScreen(
                                onBackClick = { navController.popBackStack() }
                            )
                        }

                        composable("change_password") {
                            ChangePasswordScreen(
                                onBackClick = { navController.popBackStack() }
                            )
                        }

                        composable("notifications") {
                            NotificationsScreen(
                                onBackClick = { navController.popBackStack() }
                            )
                        }

                        composable("help_center") {
                            HelpCenterScreen(
                                onBackClick = { navController.popBackStack() },
                                onManageSubscriptionClick = { /* Subscription removed */ }
                            )
                        }

                        composable("contact_us") {
                            ContactUsScreen(
                                onBackClick = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
