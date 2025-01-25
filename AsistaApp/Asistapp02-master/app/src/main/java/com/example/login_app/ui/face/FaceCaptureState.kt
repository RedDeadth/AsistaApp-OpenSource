package com.example.login_app.ui.face

import android.graphics.Bitmap

sealed class FaceCaptureState {
    object Initial : FaceCaptureState()
    data class Success(
        val image: Bitmap?,
        val fileName: String,
        val currentUser: String = "",
        val currentDateTime: String = ""
    ) : FaceCaptureState()
    data class Error(val message: String) : FaceCaptureState()
}