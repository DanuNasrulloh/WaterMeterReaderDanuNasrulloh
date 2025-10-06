package com.flindigital.watermeter.pages.customers

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Star
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flindigital.watermeter.data.DummyCustomers
import com.flindigital.watermeter.data.model.Customer

private val HeaderGreen = Color(0xFF14B8A6)
private val LightGray = Color(0xFFF1F5F9)
private val WarningOrange = Color(0xFFFF8A00)

@Composable
fun CustomerListScreen(onRecordClick: (Customer) -> Unit = {}) {
    var selectedTab by remember { mutableStateOf(CustomerTab.NotRecorded) }
    var query by remember { mutableStateOf("") }

    val all = DummyCustomers.customers
    val filtered = all.filter { c ->
        (selectedTab == CustomerTab.NotRecorded && !c.isRecorded ||
                selectedTab == CustomerTab.Recorded && c.isRecorded) &&
                (c.userName.contains(query, ignoreCase = true) ||
                        c.userId.contains(query, ignoreCase = true) ||
                        c.fullAddress.contains(query, ignoreCase = true))
    }

    Column(modifier = Modifier.fillMaxSize()) {
        HeaderSection(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it },
            query = query,
            onQueryChange = { query = it }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(LightGray)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filtered) { customer ->
                CustomerItem(customer = customer, onRecordClick = onRecordClick)
            }
        }
    }
}

@Composable
private fun HeaderSection(
    selectedTab: CustomerTab,
    onTabSelected: (CustomerTab) -> Unit,
    query: String,
    onQueryChange: (String) -> Unit
) {
    Surface(color = HeaderGreen) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Daftar Pelanggan",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier.weight(1f)
                )
                HeaderMonthPill()
            }
            Spacer(modifier = Modifier.height(12.dp))
            SegmentedTabs(selectedTab = selectedTab, onTabSelected = onTabSelected)
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                placeholder = { Text("Cari…") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )
        }
    }
}

@Composable
private fun HeaderMonthPill() {
    Surface(
        color = Color(0xFF0EA5A3),
        contentColor = Color.White,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Sept 2025", fontSize = 12.sp)
            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun SegmentedTabs(
    selectedTab: CustomerTab,
    onTabSelected: (CustomerTab) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF0EA5A3)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Segment(
            text = "Belum Dicatat",
            selected = selectedTab == CustomerTab.NotRecorded,
            onClick = { onTabSelected(CustomerTab.NotRecorded) },
            modifier = Modifier.weight(1f)
        )
        Segment(
            text = "Sudah Dicatat",
            selected = selectedTab == CustomerTab.Recorded,
            onClick = { onTabSelected(CustomerTab.Recorded) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun Segment(text: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val background = if (selected) Color.White else Color(0xFF0EA5A3)
    val content = if (selected) Color(0xFF0EA5A3) else Color.White
    Box(
        modifier = modifier
            .background(background)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = content, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun CustomerItem(customer: Customer, onRecordClick: (Customer) -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Left decorative icon
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFEFD5)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (customer.isRecorded) Icons.Outlined.Star else Icons.Outlined.Star,
                        contentDescription = null,
                        tint = WarningOrange,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "ID - ${customer.userId}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    modifier = Modifier.weight(1f)
                )
                Button(onClick = { onRecordClick(customer) }, shape = RoundedCornerShape(8.dp)) {
                    Text("Catat")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = customer.userName, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = customer.fullAddress,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF70757A),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFF3E7)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint = WarningOrange,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

private enum class CustomerTab { NotRecorded, Recorded }
