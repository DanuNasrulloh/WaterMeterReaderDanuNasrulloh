package com.flindigital.watermeter.pages.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.ViewList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flindigital.watermeter.data.DummyCustomers
import com.flindigital.watermeter.data.model.Customer
import com.flindigital.watermeter.pages.customers.CustomerItem

private val HeaderGreen = Color(0xFF14B8A6)
private val LightGray = Color(0xFFF1F5F9)

private enum class CustomerTab { NotRecorded, Recorded }

/** Tab bawah sesuai gambar */
enum class BottomTab(val label: String) {
    Home("Beranda"),
    List("List Pelanggan"),
    History("Riwayat"),
    Profile("Profile")
}

@Composable
fun HomeScreen(
    onNavigate: (Customer) -> Unit = {},
    onBottomTabSelected: (BottomTab) -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(CustomerTab.NotRecorded) }
    var query by remember { mutableStateOf("") }
    var bottomTab by remember { mutableStateOf(BottomTab.List) }

    val all = DummyCustomers.customers
    val filtered = remember(selectedTab, query, all) {
        all.filter { c ->
            (selectedTab == CustomerTab.NotRecorded && !c.isRecorded ||
                    selectedTab == CustomerTab.Recorded && c.isRecorded) &&
                    (c.userName.contains(query, true) ||
                            c.userId.contains(query, true) ||
                            c.fullAddress.contains(query, true))
        }
    }

    Scaffold(
        containerColor = LightGray,
        bottomBar = {
            BottomNavBar(
                current = bottomTab,
                onTabSelected = {
                    bottomTab = it
                    onBottomTabSelected(it)
                }
            )
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(LightGray)
                .padding(inner)
        ) {
            HeaderSection(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
            SearchBar(
                query = query,
                onQueryChange = { query = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            )
            if (filtered.isEmpty()) {
                Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("Tidak ada data pada tab ini")
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(LightGray)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filtered, key = { it.userId }) { customer ->
                        CustomerItem(customer = customer, onRecordClick = { onNavigate(customer) })
                    }
                    item { Spacer(modifier = Modifier.height(15.dp)) }
                }
            }
        }
    }
}


@Composable
private fun BottomNavBar(
    current: BottomTab,
    onTabSelected: (BottomTab) -> Unit
) {
    val shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp)

    Surface(
        color = Color.White,
        shape = shape,
        tonalElevation = 2.dp,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)        // tinggi nyaman
                .clip(shape),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomItem(
                selected = current == BottomTab.Home,
                onClick = { onTabSelected(BottomTab.Home) },
                icon = { Icon(Icons.Outlined.Home, contentDescription = null) },
                label = "Beranda"
            )
            BottomItem(
                selected = current == BottomTab.List,
                onClick = { onTabSelected(BottomTab.List) },
                icon = { Icon(Icons.Outlined.ViewList, contentDescription = null) },
                label = "List Pelanggan" // tidak akan kepotong
            )
            BottomItem(
                selected = current == BottomTab.History,
                onClick = { onTabSelected(BottomTab.History) },
                icon = { Icon(Icons.Outlined.ReceiptLong, contentDescription = null) },
                label = "Riwayat"
            )
            BottomItem(
                selected = current == BottomTab.Profile,
                onClick = { onTabSelected(BottomTab.Profile) },
                icon = { Icon(Icons.Outlined.Person, contentDescription = null) },
                label = "Profile"
            )
        }
    }
}

@Composable
private fun RowScope.BottomItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    label: String
) {
    val color = if (selected) HeaderGreen else Color(0xFF6B7280)

    Column(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .clickable(onClick = onClick)
            .padding(top = 8.dp, bottom = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // ikon seragam 22dp, mewarisi warna dari 'color'
        CompositionLocalProvider(LocalContentColor provides color) {
            Box(Modifier.size(22.dp)) { icon() }
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,                 // ukuran label diperkecil
            lineHeight = 12.sp,
            fontWeight = FontWeight.Medium,
            color = color,
            maxLines = 1,                     // ⬅️ tidak akan turun baris
            softWrap = false,
            overflow = TextOverflow.Clip,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun HeaderSection(
    selectedTab: CustomerTab,
    onTabSelected: (CustomerTab) -> Unit
) {
    Surface(color = HeaderGreen) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
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
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
        placeholder = { Text("Cari…") },
        singleLine = true,
        modifier = modifier,
        shape = RoundedCornerShape(10.dp)
    )
}

@Composable
private fun HeaderMonthPill() {
    Surface(
        color = Color(0xFF0EA5A3),
        contentColor = Color.White,
        shape = RoundedCornerShape(5.dp)
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
private fun Segment(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
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


@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFF14B8A6),
            surface = Color(0xFFF1F5F9),
            background = Color(0xFFF1F5F9)
        )
    ) {
        HomeScreen()
    }
}
