package com.example.week3_assignment.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.week3_assignment.model.Device
import com.example.week3_assignment.viewmodel.DeviceUiState
import com.example.week3_assignment.viewmodel.DeviceViewModel

@Composable
fun DeviceScreen(viewModel: DeviceViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchText by viewModel.searchText.collectAsStateWithLifecycle()

    DeviceScreenContent(
        uiState = uiState,
        searchText = searchText,
        onSearchChange = viewModel::onSearchQueryChange,
        onRoomSelected = viewModel::onRoomSelected,
        onToggleDevice = viewModel::onToggleDevice
    )
}

@Composable
fun DeviceScreenContent(
    uiState: DeviceUiState,
    searchText: String,
    onSearchChange: (String) -> Unit,
    onRoomSelected: (String?) -> Unit,
    onToggleDevice: (Int) -> Unit
) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            EnergyCard(totalPowerWatt = uiState.totalPowerWatt)

            OutlinedTextField(
                value = searchText,
                onValueChange = onSearchChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Cihaz ara") },
                singleLine = true
            )

            RoomFilterRow(
                rooms = uiState.rooms,
                selectedRoom = uiState.selectedRoom,
                onRoomSelected = onRoomSelected
            )

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(items = uiState.devices, key = { it.id }) { device ->
                    DeviceItem(device = device, onToggle = { onToggleDevice(device.id) })
                }
            }
        }
    }
}

@Composable
fun EnergyCard(totalPowerWatt: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Anlık Toplam Tüketim", style = MaterialTheme.typography.titleMedium)
            Text(
                text = "%.1f W".format(totalPowerWatt),
                style = MaterialTheme.typography.headlineLarge
            )
        }
    }
}

@Composable
fun RoomFilterRow(
    rooms: List<String>,
    selectedRoom: String?,
    onRoomSelected: (String?) -> Unit
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            FilterChip(
                selected = selectedRoom == null,
                onClick = { onRoomSelected(null) },
                label = { Text("Tümü") }
            )
        }
        items(items = rooms, key = { it }) { room ->
            FilterChip(
                selected = selectedRoom == room,
                onClick = { onRoomSelected(room) },
                label = { Text(room) }
            )
        }
    }
}

@Composable
fun DeviceItem(device: Device, onToggle: () -> Unit) {
    val statusText = when {
        !device.isOnline -> "Çevrimdışı"
        device.isActive -> "Açık"
        else -> "Kapalı"
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = device.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "${device.room} • ${device.powerWatt} W",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(text = statusText, style = MaterialTheme.typography.bodySmall)
            }
            Button(onClick = onToggle, enabled = device.isOnline) {
                Text(if (device.isActive) "Kapat" else "Aç")
            }
        }
    }
}