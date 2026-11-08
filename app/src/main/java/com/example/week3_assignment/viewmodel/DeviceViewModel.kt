package com.example.week3_assignment.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.week3_assignment.data.DeviceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class DeviceViewModel(
    repository: DeviceRepository = DeviceRepository()
) : ViewModel() {

    private val allDevices = MutableStateFlow(repository.getDevices())
    private val searchQuery = MutableStateFlow("")
    val searchText: StateFlow<String> = searchQuery
    private val selectedRoom = MutableStateFlow<String?>(null)

    val uiState: StateFlow<DeviceUiState> = combine(
        allDevices, searchQuery, selectedRoom
    ) { devices, query, room ->
        val filtered = devices.filter { device ->
            val matchesName = device.name.contains(query.trim(), ignoreCase = true)
            val matchesRoom = room == null || device.room == room
            matchesName && matchesRoom
        }
        val total = devices
            .filter { it.isOnline && it.isActive }
            .sumOf { it.powerWatt }

        DeviceUiState(
            devices = filtered,
            rooms = devices.map { it.room }.distinct(),
            searchQuery = query,
            selectedRoom = room,
            totalPowerWatt = total
        )
    }
        .flowOn(Dispatchers.Default)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DeviceUiState()
        )

    fun onSearchQueryChange(query: String) {
        searchQuery.value = query
    }

    fun onRoomSelected(room: String?) {
        selectedRoom.value = room
    }

    fun onToggleDevice(deviceId: Int) {
        allDevices.update { list ->
            list.map { device ->
                if (device.id == deviceId && device.isOnline) {
                    device.copy(isActive = !device.isActive)
                } else {
                    device
                }
            }
        }
    }
}