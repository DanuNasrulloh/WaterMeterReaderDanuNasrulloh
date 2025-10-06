package com.flindigital.watermeter.pages.customers

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flindigital.watermeter.data.DummyCustomers
import com.flindigital.watermeter.data.model.Customer
import kotlin.collections.get

private val LightGray = Color(0xFFF1F5F9)
private val WarningOrange = Color(0xFFFF8A00)


@Composable
fun CustomerListScreen(
    customers: List<Customer> = emptyList(),
    onRecordClick: (Customer) -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGray)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(customers, key = { it.userId }) { customer ->
            CustomerItem(customer = customer, onRecordClick = onRecordClick)
        }
        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
fun CustomerItem(customer: Customer, onRecordClick: (Customer) -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                // icon kecil di kiri
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFEFD5)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Star,
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
                Button(
                    onClick = { onRecordClick(customer) },
                    shape = RoundedCornerShape(3.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
                    modifier = Modifier.height(25.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF14B8A6),
                        contentColor = Color.White
                    )
                ) {
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

@Preview(showBackground = true)
@Composable
private fun CustomerItemPreview() {
    MaterialTheme(
        colorScheme = lightColorScheme(
            surface = LightGray,
            background = LightGray
        )
    ) {
        CustomerItem(
            customer = DummyCustomers.customers[0],
            onRecordClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CustomerListScreenPreview() {
    MaterialTheme(
        colorScheme = lightColorScheme(
            surface = LightGray,
            background = LightGray
        )
    ) {
        CustomerListScreen(customers = DummyCustomers.customers)
    }
}
