package com.example.tonespace.network

import com.google.gson.annotations.SerializedName

// ---------- GENERIC & AUTH ----------

data class AuthResponse(val success: Boolean, val message: String)

data class LoginResponse(val success: Boolean, val message: String, val user: User?)

data class ForgotPasswordRequest(val email: String)

data class ResetPasswordRequest(
    val email: String,
    @SerializedName("new_password") val newPassword: String
)

data class ChangePasswordRequest(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("current_password") val currentPassword: String,
    @SerializedName("new_password") val newPassword: String
)

// ---------- USER & PROFILE ----------

data class User(
    val id: Int,
    val name: String?,
    val email: String,
    val phone: String?,
    val gender: String?,
    val dob: String?,
    @SerializedName("profile_image_url") val profileImageUrl: String?
)

data class UpdateProfileRequest(
    @SerializedName("user_id") val userId: String,
    val name: String,
    val phone: String,
    val gender: String,
    val dob: String
)

data class UserProfileResponse(val success: Boolean, val user: User?, val message: String?)

data class UpdateProfileResponse(val success: Boolean, val message: String, @SerializedName("profile_image_url") val profileImageUrl: String?)

// ---------- CONTACT & HELP ----------

data class ContactUsRequest(
    @SerializedName("user_id") val userId: Int?,
    val name: String,
    val email: String,
    val message: String
)

data class HelpArticle(
    val id: Int,
    val title: String,
    val content: String,
    val category: String
)

data class HelpArticlesResponse(val success: Boolean, val articles: List<HelpArticle>)

// ---------- PAYMENTS & SUBSCRIPTIONS ----------

// All subscription models have been removed.

// ---------- NOTIFICATIONS ----------

data class Notification(
    val id: Int,
    val message: String,
    @SerializedName("is_read") val isRead: Boolean,
    @SerializedName("created_at") val createdAt: String
)

data class NotificationsResponse(val success: Boolean, val notifications: List<Notification>)


// ---------- UPLOADS (DESIGNS/PHOTOS) ----------

data class UploadResponse(
    val success: Boolean, 
    val message: String,
    @SerializedName("image_url") val imageUrl: String?,
    @SerializedName("design_id") val designId: Int?,
    @SerializedName("photo_id") val photoId: Int?
)


// ---------- AI & SAVED ITEMS ----------

data class AnalyzeRequest(@SerializedName("design_id") val designId: Int)

data class AnalysisResponse(val success: Boolean, val analysis: AiAnalysisResult?)

data class AiAnalysisResult(
    @SerializedName("color_palette") val colorPalette: List<String>,
    @SerializedName("furniture_recommendations") val furnitureRecommendations: List<FurnitureRecommendation>,
    @SerializedName("layout_suggestions") val layoutSuggestions: List<String>
)

data class FurnitureRecommendation(val name: String, val style: String, val url: String)

data class SavedItem(
    val id: Int, // Changed from saved_item_id to id to match the table
    @SerializedName("item_type") val itemType: String,
    @SerializedName("saved_at") val savedAt: String,
    @SerializedName("design_id") val designId: Int?,
    @SerializedName("title") val designTitle: String?,
    @SerializedName("image_url") val designImageUrl: String?
)

data class SavedItemsResponse(val success: Boolean, @SerializedName("saved_items") val savedItems: List<SavedItem>)

// ADDED: Request for deleting a saved item
data class DeleteSavedItemRequest(
    @SerializedName("saved_item_id") val savedItemId: Int,
    @SerializedName("user_id") val userId: Int
)
