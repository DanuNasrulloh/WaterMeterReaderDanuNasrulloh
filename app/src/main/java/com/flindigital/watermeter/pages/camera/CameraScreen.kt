package com.flindigital.watermeter.pages.camera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.util.Log
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Locale

private const val TAG = "CameraScreen"

data class CropBoxFractions(
    val left: Float,
    val top: Float,
    val width: Float,
    val height: Float
)

private val HeaderGreen = Color(0xFF14B8A6)

@Composable
fun CameraScreen(
    userId: String,
    onCaptured: (fullPath: String, cropPath: String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var previewView: PreviewView? by remember { mutableStateOf(null) }
    var isCapturing by remember { mutableStateOf(false) }

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
        )
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted -> hasPermission = granted }
    )
    LaunchedEffect(Unit) {
        if (!hasPermission) permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    // Crop rectangle placement as fraction of view
    val cropBox = CropBoxFractions(
        left = 0.16f,
        top = 0.34f,
        width = 0.68f,
        height = 0.18f
    )

    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Surface(color = HeaderGreen) {
            Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Text(
                    text = "Ambil Foto Meter",
                    style = MaterialTheme.typography.titleMedium.copy(color = Color.White),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        if (!hasPermission) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Memerlukan izin kamera")
            }
            return@Column
        }

        Box(modifier = Modifier.weight(1f)) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    val pv = PreviewView(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        scaleType = PreviewView.ScaleType.FILL_CENTER
                    }
                    previewView = pv

                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder()
                        .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                        .build()

                    val imgCapture = ImageCapture.Builder()
                        .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .build()
                    imageCapture = imgCapture

                    preview.surfaceProvider = pv.surfaceProvider
                    try {
                        cameraProvider.unbindAll()
                        val camera = cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            CameraSelector.DEFAULT_BACK_CAMERA,
                            preview,
                            imgCapture
                        )
                        // Apply 2x zoom when starting
                        camera.cameraControl.setZoomRatio(2.0f)
                    } catch (e: Exception) {
                        Log.e(TAG, "Binding failed", e)
                    }

                    pv
                }
            )

            // Overlay: translucent backdrop + green rectangle
            CameraOverlay(cropBox = cropBox, strokeWidth = 3.dp)

            // 2x zoom pill
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black.copy(alpha = 0.35f))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(text = "2x", color = Color.White)
            }

            // Shutter button
            Box(modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 32.dp)) {
                if (isCapturing) {
                    CircularProgressIndicator(color = HeaderGreen)
                } else {
                    ShutterButton(onClick = {
                        val imgCap = imageCapture ?: return@ShutterButton
                        val pv = previewView ?: return@ShutterButton
                        isCapturing = true
                        captureAndCrop(
                            context = context,
                            imageCapture = imgCap,
                            previewView = pv,
                            cropBox = cropBox,
                            onResult = { full, crop ->
                                isCapturing = false
                                onCaptured(full, crop)
                            },
                            onError = {
                                isCapturing = false
                                Log.e(TAG, "Capture error", it)
                            }
                        )
                    })
                }
            }
        }
    }
}

@Composable
private fun CameraOverlay(cropBox: CropBoxFractions, strokeWidth: Dp) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        // darkened backdrop
        drawRect(color = Color.Black.copy(alpha = 0.35f))

        val leftPx = size.width * cropBox.left
        val topPx = size.height * cropBox.top
        val widthPx = size.width * cropBox.width
        val heightPx = size.height * cropBox.height

        // Clear the crop area
        drawRect(
            color = Color.Transparent,
            topLeft = androidx.compose.ui.geometry.Offset(leftPx, topPx),
            size = androidx.compose.ui.geometry.Size(widthPx, heightPx),
            blendMode = androidx.compose.ui.graphics.BlendMode.Clear
        )
        // Green outline
        drawRect(
            color = Color(0xFF22C55E),
            topLeft = androidx.compose.ui.geometry.Offset(leftPx, topPx),
            size = androidx.compose.ui.geometry.Size(widthPx, heightPx),
            style = Stroke(width = strokeWidth.toPx())
        )
    }
}

@Composable
private fun ShutterButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(84.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.95f))
            .padding(6.dp)
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(CircleShape)
                .background(Color.White)
        )
        Button(
            onClick = onClick,
            modifier = Modifier.matchParentSize(),
            shape = CircleShape
        ) {}
    }
}

private fun captureAndCrop(
    context: Context,
    imageCapture: ImageCapture,
    previewView: PreviewView,
    cropBox: CropBoxFractions,
    onResult: (String, String) -> Unit,
    onError: (Throwable) -> Unit
) {
    val dir = File(context.filesDir, "app_images").apply { if (!exists()) mkdirs() }
    val ts = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis())
    val fullFile = File(dir, "meteran_full_${ts}.jpg")
    val cropFile = File(dir, "meteran_crop_${ts}.jpg")

    val output = ImageCapture.OutputFileOptions.Builder(fullFile).build()
    imageCapture.takePicture(
        output,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onError(exception: ImageCaptureException) {
                onError(exception)
            }

            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                try {
                    val bitmap = BitmapFactory.decodeFile(fullFile.absolutePath)
                    val cropRect = computeCropRect(bitmap.width, bitmap.height, cropBox)
                    val cropped = Bitmap.createBitmap(
                        bitmap,
                        cropRect.left,
                        cropRect.top,
                        cropRect.width(),
                        cropRect.height()
                    )
                    saveBitmap(cropped, cropFile)
                    onResult(fullFile.absolutePath, cropFile.absolutePath)
                } catch (e: Exception) {
                    onError(e)
                }
            }
        }
    )
}

private fun computeCropRect(imageW: Int, imageH: Int, crop: CropBoxFractions): Rect {
    val left = (imageW * crop.left).toInt().coerceIn(0, imageW)
    val top = (imageH * crop.top).toInt().coerceIn(0, imageH)
    val width = (imageW * crop.width).toInt().coerceIn(1, imageW - left)
    val height = (imageH * crop.height).toInt().coerceIn(1, imageH - top)
    return Rect(left, top, left + width, top + height)
}

private fun saveBitmap(bitmap: Bitmap, file: File) {
    FileOutputStream(file).use { out ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 92, out)
    }
}
