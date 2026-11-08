package com.example.week3_assignment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.week3_assignment.ui.DeviceScreen
import com.example.week3_assignment.ui.theme.Week3_assignmentTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Week3_assignmentTheme {
                DeviceScreen()
            }
        }
    }
}