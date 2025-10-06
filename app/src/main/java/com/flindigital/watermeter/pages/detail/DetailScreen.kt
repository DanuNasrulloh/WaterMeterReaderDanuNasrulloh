package com.flindigital.watermeter.pages.detail

import android.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import android.widget.ImageView
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.bumptech.glide.Glide
import java.io.File
import com.flindigital.watermeter.data.DummyCustomers
import androidx.compose.ui.text.font.FontWeight


private val HeaderGreen = Color(0xFF14B8A6)
private val LightGray = Color(0xFFF1F5F9)

data class DetailArgs(
    val userId: String,
    val fullPath: String?,
    val cropPath: String?
)

@Composable
fun DetailScreen(
    userId: String,
    fullPath: String?,
    cropPath: String?,
    onSave: (meterNumber: String) -> Unit = {}
) {
    val customer = DummyCustomers.customers.firstOrNull { it.userId == userId }

    var meterNumber by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Surface(color = HeaderGreen) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier
                        .size(30.dp)
                        .padding(end = 8.dp)
                )
                Text(
                    text = "Catat Data",
                    style = MaterialTheme.typography.titleMedium.copy(color = Color.White),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .background(LightGray)
                .verticalScroll(rememberScrollState())
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Card with customer info and ID header pill style
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "ID - ${customer?.userId ?: userId}",
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Black),
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Filled.LocationOn,
                            contentDescription = null,
                            tint = HeaderGreen
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    InfoRow("Nama Pelanggan", customer?.userName ?: "-")
                    InfoRow("Hari/Tanggal", customer?.date ?: "-")
                    InfoRow("Jam", customer?.time ?: "-")
                    InfoRow("Alamat", customer?.fullAddress ?: "-")
                    InfoRow(
                        "Latlong",
                        "${customer?.latitude ?: 0.0}, ${customer?.longitude ?: 0.0}"
                    )
                }
            }

            Text(
                "Foto",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF70757A)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                PhotoThumb(path = fullPath, width = 92.dp, height = 92.dp)
                PhotoThumb(path = cropPath, modifier = Modifier.weight(1f), height = 100.dp)
            }

            Text("Angka Meter", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = Color(0xFF70757A))
            OutlinedTextField(
                value = meterNumber,
                onValueChange = { meterNumber = it },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = { onSave(meterNumber) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = HeaderGreen,
                    contentColor = Color.White
                )
            ){
                Text("Simpan Data")
            }
        }
    }
}

@Composable
private fun PhotoThumb(
    path: String?,
    modifier: Modifier = Modifier,
    width: Dp? = null,
    height: Dp
) {
    val base = modifier
        .clip(RoundedCornerShape(8.dp))
        .let { m ->
            when {
                width != null -> m.size(width = width, height = height)
                else -> m.fillMaxWidth().height(height)
            }
        }
    if (path.isNullOrBlank()) {
        Box(modifier = base.background(Color(0xFFE2E8F0)), contentAlignment = Alignment.Center) {
            Text("Tidak ada foto", color = Color(0xFF70757A))
        }
    } else {
        AndroidView(
            modifier = base,
            factory = { ctx ->
                ImageView(ctx).apply {
                    scaleType = ImageView.ScaleType.CENTER_CROP
                    Glide.with(ctx).load(File(path)).into(this)
                }
            }
        )
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = Color(0xFF70757A),
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DetailScreenPreview() {
    MaterialTheme(
        colorScheme = androidx.compose.material3.lightColorScheme(
            primary = HeaderGreen,
            surface = LightGray,
            background = LightGray
        )
    ) {
        val sample = DummyCustomers.customers.firstOrNull()
        DetailScreen(
            userId = sample?.userId ?: "0001",
            fullPath = null,
            cropPath = null,
            onSave = {}
        )
    }
}


