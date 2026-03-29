package com.example.asistaapp.presentation.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.math.*

// ─── Configura aquí las coordenadas del centro de trabajo ───────────────────
// Puedes cambiar estas coordenadas a la ubicación real de la empresa en Perú
private const val OFFICE_LATITUDE = -12.0464 // Ejemplo: Lima, Plaza Mayor
private const val OFFICE_LONGITUDE = -77.0428
private const val ALLOWED_RADIUS_METERS = 150.0 // radio permitido en metros
// ────────────────────────────────────────────────────────────────────────────

data class GeofenceUiState(
    val isLoading: Boolean = false,
    val userLatitude: Double? = null,
    val userLongitude: Double? = null,
    val distanceToOffice: Double? = null,
    val isInsideZone: Boolean = false,
    val attendanceRegistered: Boolean = false,
    val errorMessage: String? = null,
    val officeLatitude: Double = OFFICE_LATITUDE,
    val officeLongitude: Double = OFFICE_LONGITUDE,
    val allowedRadiusMeters: Double = ALLOWED_RADIUS_METERS
)

class GeofenceViewModel(private val context: Context) : ViewModel() {

    private val _uiState = MutableStateFlow(GeofenceUiState())
    val uiState: StateFlow<GeofenceUiState> = _uiState

    @SuppressLint("MissingPermission")
    fun fetchUserLocation() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            try {
                val fusedClient = LocationServices.getFusedLocationProviderClient(context)
                fusedClient.lastLocation.addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        val distance = haversineDistance(
                            location.latitude, location.longitude,
                            OFFICE_LATITUDE, OFFICE_LONGITUDE
                        )
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            userLatitude = location.latitude,
                            userLongitude = location.longitude,
                            distanceToOffice = distance,
                            isInsideZone = distance <= ALLOWED_RADIUS_METERS
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "No se pudo obtener tu ubicación. Activa el GPS."
                        )
                    }
                }.addOnFailureListener {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Error al obtener ubicación: ${it.message}"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error inesperado: ${e.message}"
                )
            }
        }
    }

    fun registerAttendance() {
        if (!_uiState.value.isInsideZone) return
        _uiState.value = _uiState.value.copy(attendanceRegistered = true)
    }

    fun dismissError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    private fun haversineDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadius = 6371000.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2)
        return earthRadius * 2 * atan2(sqrt(a), sqrt(1 - a))
    }
}

class GeofenceViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GeofenceViewModel::class.java)) return GeofenceViewModel(context) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
