package com.flindigital.watermeter.pages.camera

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.exifinterface.media.ExifInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun CameraScreen(
    onCaptured: (fullPhoto: File, croppedPhoto: File) -> Unit = { _, _ -> }
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasPermission by remember { mutableStateOf(false) }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    if (!hasPermission) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Meminta izin kamera…")
        }
        return
    }

    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val previewView = remember { PreviewView(context).apply { scaleType = PreviewView.ScaleType.FILL_CENTER } }
    val imageCapture = remember {
        ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .setTargetAspectRatio(AspectRatio.RATIO_16_9)
            .build()
    }

    var isBinding by remember { mutableStateOf(true) }

    DisposableEffect(Unit) {
        val executor = ContextCompat.getMainExecutor(context)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                .build()
            preview.setSurfaceProvider(previewView.surfaceProvider)

            try {
                cameraProvider.unbindAll()
                val camera = cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageCapture
                )
                try { camera.cameraControl.setZoomRatio(2f) } catch (_: Exception) {}
                isBinding = false
            } catch (e: Exception) {
                e.printStackTrace()
                isBinding = false
            }
        }, executor)

        onDispose { /* no-op */ }
    }

    // Overlay size as fraction of the preview size
    val overlayWidthFraction = 0.75f
    val overlayHeightFraction = 0.22f

    Surface(color = MaterialTheme.colorScheme.background) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f),
                    contentAlignment = Alignment.Center
                ) {
                    AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())

                    CropOverlay(
                        widthFraction = overlayWidthFraction,
                        heightFraction = overlayHeightFraction
                    )

                    // Zoom indicator
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                            .background(Color(0x80000000), CircleShape)
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(text = "2x", color = Color.White)
                    }
                }

                Spacer(Modifier.weight(1f))

                ShutterButton(
                    onClick = {
                        captureDualPhotos(
                            context = context,
                            imageCapture = imageCapture,
                            overlayWidthFraction = overlayWidthFraction,
                            overlayHeightFraction = overlayHeightFraction,
                            onSaved = onCaptured
                        )
                    }
                )

                Spacer(Modifier.height(28.dp))
            }

            if (isBinding) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
private fun CropOverlay(widthFraction: Float, heightFraction: Float) {
    AndroidView(
        factory = { ctx ->
            object : android.view.View(ctx) {
                private val borderPaint = android.graphics.Paint().apply {
                    color = android.graphics.Color.GREEN
                    style = android.graphics.Paint.Style.STROKE
                    strokeWidth = 6f
                    isAntiAlias = true
                }
                private val dimPaint = android.graphics.Paint().apply {
                    color = android.graphics.Color.parseColor("#80000000")
                    style = android.graphics.Paint.Style.FILL
                }
                override fun onDraw(canvas: android.graphics.Canvas) {
                    super.onDraw(canvas)
                    val w = width.toFloat()
                    val h = height.toFloat()
                    val rectW = w * widthFraction
                    val rectH = h * heightFraction
                    val left = (w - rectW) / 2f
                    val top = (h - rectH) / 2f
                    val right = left + rectW
                    val bottom = top + rectH

                    // dim outside
                    val path = android.graphics.Path().apply {
                        addRect(0f, 0f, w, h, android.graphics.Path.Direction.CW)
                        addRect(left, top, right, bottom, android.graphics.Path.Direction.CCW)
                    }
                    canvas.drawPath(path, dimPaint)

                    // border
                    canvas.drawRect(left, top, right, bottom, borderPaint)
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
private fun ShutterButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(88.dp)
            .background(color = Color.White, shape = CircleShape)
            .padding(6.dp),
        contentAlignment = Alignment.Center
    ) {
        IconButton(onClick = onClick, modifier = Modifier.fillMaxSize()) {}
    }
}

private fun captureDualPhotos(
    context: Context,
    imageCapture: ImageCapture,
    overlayWidthFraction: Float,
    overlayHeightFraction: Float,
    onSaved: (File, File) -> Unit
) {
    val outputDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) ?: context.cacheDir
    val time = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis())
    val fullFile = File(outputDir, "WATERMETER_${time}_full.jpg")

    val outputOptions = ImageCapture.OutputFileOptions.Builder(fullFile).build()
    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onError(exception: ImageCaptureException) {
                exception.printStackTrace()
            }
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                CoroutineScope(Dispatchers.IO).launch {
                    val rotated = decodeAndRotate(fullFile)
                    val cropped = cropByFraction(rotated, overlayWidthFraction, overlayHeightFraction)
                    val cropFile = File(outputDir, "WATERMETER_${time}_crop.jpg")
                    FileOutputStream(cropFile).use { out ->
                        cropped.compress(Bitmap.CompressFormat.JPEG, 95, out)
                    }
                    onSaved(fullFile, cropFile)
                }
            }
        }
    )
}

private fun decodeAndRotate(file: File): Bitmap {
    val bitmap = BitmapFactory.decodeFile(file.absolutePath)
    val exif = ExifInterface(file.absolutePath)
    val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
    val matrix = android.graphics.Matrix()
    when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
        ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
        ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
        ExifInterface.ORIENTATION_TRANSPOSE -> { matrix.postRotate(90f); matrix.postScale(-1f, 1f) }
        ExifInterface.ORIENTATION_TRANSVERSE -> { matrix.postRotate(270f); matrix.postScale(-1f, 1f) }
        ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.postScale(-1f, 1f)
        ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.postScale(1f, -1f)
        else -> {}
    }
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}

private fun cropByFraction(bitmap: Bitmap, widthFraction: Float, heightFraction: Float): Bitmap {
    val bmpW = bitmap.width
    val bmpH = bitmap.height
    val rectW = (bmpW * widthFraction).toInt()
    val rectH = (bmpH * heightFraction).toInt()
    val left = ((bmpW - rectW) / 2).coerceAtLeast(0)
    val top = ((bmpH - rectH) / 2).coerceAtLeast(0)
    val safeW = rectW.coerceAtMost(bmpW - left)
    val safeH = rectH.coerceAtMost(bmpH - top)
    return Bitmap.createBitmap(bitmap, left, top, safeW, safeH)
}
