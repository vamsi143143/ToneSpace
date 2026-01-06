package com.example.tonespace.network

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface AuthApi {

    // ---------- AUTHENTICATION ----------
    @FormUrlEncoded
    @POST("login.php")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<LoginResponse>

    @FormUrlEncoded
    @POST("register.php")
    suspend fun register(
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<AuthResponse>

    @POST("forgot_password.php")
    suspend fun forgotPassword(@Body req: ForgotPasswordRequest): Response<AuthResponse>

    @POST("reset_password.php")
    suspend fun resetPassword(@Body req: ResetPasswordRequest): Response<AuthResponse>
    
    @POST("change_password.php")
    suspend fun changePassword(@Body req: ChangePasswordRequest): Response<AuthResponse>

    // ---------- PROFILE ----------
    @GET("get_user_profile.php")
    suspend fun getUserProfile(@Query("user_id") userId: Int): Response<UserProfileResponse>

    @POST("update_user_profile.php")
    suspend fun updateUserProfile(@Body req: UpdateProfileRequest): Response<UpdateProfileResponse>

    // ---------- CONTACT & HELP ----------
    @POST("contact_us.php")
    suspend fun contactUs(@Body req: ContactUsRequest): Response<AuthResponse>

    @GET("get_help_articles.php")
    suspend fun getHelpArticles(): Response<HelpArticlesResponse>

    // ---------- NOTIFICATIONS ----------
    @GET("get_notifications.php")
    suspend fun getNotifications(@Query("user_id") userId: Int): Response<NotificationsResponse>
    
    // ---------- AI & SAVED ITEMS ----------
    @POST("analyze_design_with_ai.php")
    suspend fun analyzeDesign(@Body req: AnalyzeRequest): Response<AnalysisResponse>

    @GET("get_saved_items.php")
    suspend fun getSavedItems(@Query("user_id") userId: Int): Response<SavedItemsResponse>

    // ADDED: Endpoint for saving a new design
    @Multipart
    @POST("save_design.php")
    suspend fun saveDesign(
        @Part("user_id") userId: RequestBody,
        @Part("title") title: RequestBody,
        @Part designImage: MultipartBody.Part
    ): Response<AuthResponse>

    // ADDED: Endpoint for deleting a saved item
    @POST("delete_saved_item.php")
    suspend fun deleteSavedItem(@Body req: DeleteSavedItemRequest): Response<AuthResponse>
}
