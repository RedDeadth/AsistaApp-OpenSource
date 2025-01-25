package com.example.login_app.data

import com.example.login_app.data.models.AttendanceRequest
import com.example.login_app.data.models.AttendanceResponse
import com.example.login_app.data.models.AuthRequest
import com.example.login_app.data.models.AuthResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthService {
    @POST("users/login/")
    suspend fun login(@Body request: AuthRequest): AuthResponse

    @POST("users/register/")
    suspend fun register(@Body request: AuthRequest): AuthResponse

    @POST("attendance/")
    suspend fun attendance(
        @Header("Authorization") token: String,
        @Body request: AttendanceRequest?
    ): AuthResponse

    @POST("register_attendance/")
    suspend fun registerAttendance(@Body attendanceRequest: AttendanceRequest): AttendanceResponse
}