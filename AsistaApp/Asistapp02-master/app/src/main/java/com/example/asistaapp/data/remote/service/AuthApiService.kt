package com.example.asistaapp.data.remote.service

import com.example.asistaapp.data.remote.dto.AttendanceRequestDto
import com.example.asistaapp.data.remote.dto.AttendanceResponseDto
import com.example.asistaapp.data.remote.dto.AuthRequestDto
import com.example.asistaapp.data.remote.dto.AuthResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("login/")
    suspend fun login(@Body request: AuthRequestDto): AuthResponseDto

    @POST("register/")
    suspend fun register(@Body request: AuthRequestDto): AuthResponseDto

    @POST("attendance/")
    suspend fun registerAttendance(@Body request: AttendanceRequestDto): AttendanceResponseDto
}
