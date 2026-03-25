package com.example.asistaapp.presentation.location

import android.content.Context
import android.view.ViewGroup
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon

val GreenZone = Color(0xFF00C853)
val RedZone = Color(0xFFD50000)
val OrangeWarning = Color(0xFFFF6D00)

@Composable
fun LocationScreen(
    onAttendanceSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val vm: GeofenceViewModel = viewModel(factory = GeofenceViewModelFactory(context))
    val state by vm.uiState.collectAsStateWithLifecycle()

    // Configura el user agent de OSMDroid (requerido por OpenStreetMap)
    LaunchedEffect(Unit) {
        Configuration.getInstance().userAgentValue = "AsistaApp/1.0 (Perú)"
        vm.fetchUserLocation()
    }

    val zoneColor by animateColorAsState(
        targetValue = if (state.isInsideZone) GreenZone else RedZone,
        animationSpec = tween(600),
        label = "zone_color"
    )

    Column(
        modifier = modifier.fillMaxSize().background(Color(0xFF121212))
    ) {
        // ── Header ──────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1E1E1E))
                .padding(16.dp)
        ) {
            Text(
                text = "Validación de Área",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // ── Mapa OSMDroid ────────────────────────────────────────────────────
        Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
            OsmMapView(
                userLat = state.userLatitude,
                userLon = state.userLongitude,
                officeLat = state.officeLatitude,
                officeLon = state.officeLongitude,
                radiusMeters = state.allowedRadiusMeters,
                isInsideZone = state.isInsideZone,
                context = context
            )

            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color(0xFF00E5FF)
                )
            }
        }

        // ── Panel de información ─────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1E1E1E))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Indicador de estado
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = zoneColor.copy(alpha = 0.15f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(zoneColor, shape = RoundedCornerShape(50))
                    )
                    Column {
                        Text(
                            text = if (state.isInsideZone) "✓ Dentro del área de trabajo" else "✗ Fuera del área de trabajo",
                            color = zoneColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        state.distanceToOffice?.let { dist ->
                            Text(
                                text = if (dist < 1000) "Distancia: ${dist.toInt()} m de tu centro laboral"
                                       else "Distancia: ${"%.1f".format(dist / 1000)} km de tu centro laboral",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            // Mensaje de error
            state.errorMessage?.let { error ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = OrangeWarning.copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = error,
                        color = OrangeWarning,
                        modifier = Modifier.padding(12.dp),
                        fontSize = 13.sp
                    )
                }
            }

            // Botones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { vm.fetchUserLocation() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF00E5FF))
                ) {
                    Text("Actualizar GPS")
                }

                Button(
                    onClick = {
                        vm.registerAttendance()
                        if (state.isInsideZone) onAttendanceSuccess()
                    },
                    enabled = state.isInsideZone && !state.attendanceRegistered,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GreenZone,
                        disabledContainerColor = Color.Gray
                    )
                ) {
                    Text(
                        text = if (state.attendanceRegistered) "Registrado ✓" else "Marcar asistencia",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// ── OSMDroid embebido en Compose ─────────────────────────────────────────────
@Composable
private fun OsmMapView(
    userLat: Double?,
    userLon: Double?,
    officeLat: Double,
    officeLon: Double,
    radiusMeters: Double,
    isInsideZone: Boolean,
    context: Context
) {
    val officePoint = GeoPoint(officeLat, officeLon)

    AndroidView(
        factory = {
            MapView(it).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                controller.setZoom(17.0)
                controller.setCenter(officePoint)
            }
        },
        update = { mapView ->
            mapView.overlays.clear()

            // Círculo de la zona permitida
            val zoneCircle = Polygon().apply {
                points = Polygon.pointsAsCircle(officePoint, radiusMeters)
                fillColor = if (isInsideZone) 0x3300C853 else 0x33D50000
                strokeColor = if (isInsideZone) 0xFF00C853.toInt() else 0xFFD50000.toInt()
                strokeWidth = 3f
            }
            mapView.overlays.add(zoneCircle)

            // Marcador de la oficina/empresa
            val officeMarker = Marker(mapView).apply {
                position = officePoint
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                title = "Centro de trabajo"
                snippet = "Área de trabajo válida — radio ${radiusMeters.toInt()} m"
            }
            mapView.overlays.add(officeMarker)

            // Marcador de posición del usuario
            if (userLat != null && userLon != null) {
                val userPoint = GeoPoint(userLat, userLon)
                val userMarker = Marker(mapView).apply {
                    position = userPoint
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    title = "Tú estás aquí"
                    snippet = if (isInsideZone) "Dentro del área ✓" else "Fuera del área ✗"
                }
                mapView.overlays.add(userMarker)
                mapView.controller.animateTo(userPoint)
            }

            mapView.invalidate()
        },
        modifier = Modifier.fillMaxSize()
    )
}
