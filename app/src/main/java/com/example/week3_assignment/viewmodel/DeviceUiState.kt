package com.example.week3_assignment.viewmodel

import com.example.week3_assignment.model.Device

data class DeviceUiState(
    val devices: List<Device> = emptyList(),
    val rooms: List<String> = emptyList(),
    val searchQuery: String = "",
    val selectedRoom: String? = null,
    val totalPowerWatt: Double = 0.0
)