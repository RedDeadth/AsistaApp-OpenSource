package com.example.asistaapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AuthRequestDto(
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String,
    @SerializedName("email") val email: String? = null
)

data class AuthResponseDto(
    @SerializedName("access") val access: String?,
    @SerializedName("refresh") val refresh: String?,
    @SerializedName("error") val error: String?
)

data class ErrorResponseDto(@SerializedName("error") val error: String?)

data class AttendanceRequestDto(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("location") val location: String,
    @SerializedName("face_verified") val faceVerified: Boolean
)

data class AttendanceResponseDto(
    @SerializedName("id") val id: Int,
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String?
)
