package com.muneer.tracker.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.muneer.tracker.TrackerViewModel
import com.muneer.tracker.planner.PlannerScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackerV3Shell(vm: TrackerViewModel) {
    var tab by rememberSaveable { mutableIntStateOf(0) }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(if (tab == 0) "Planner" else "Tracker") },
                actions = {
                    TextButton(onClick = { tab = if (tab == 0) 1 else 0 }) {
                        Text(if (tab == 0) "Workspace" else "Planner")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = tab == 0,
                    onClick = { tab = 0 },
                    icon = { Text("▦") },
                    label = { Text("Planner") }
                )
                NavigationBarItem(
                    selected = tab == 1,
                    onClick = { tab = 1 },
                    icon = { Text("⌂") },
                    label = { Text("Workspace") }
                )
            }
        }
    ) { padding ->
        if (tab == 0) PlannerScreen(vm, Modifier.padding(padding))
        else TrackerApp(vm)
    }
}
