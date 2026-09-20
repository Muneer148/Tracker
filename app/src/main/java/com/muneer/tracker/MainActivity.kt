package com.muneer.tracker
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.ui.theme.MyApplicationTheme
import com.muneer.tracker.ui.TrackerApp
class MainActivity:ComponentActivity(){
 private val vm:TrackerViewModel by viewModels()
 override fun onCreate(savedInstanceState:Bundle?){super.onCreate(savedInstanceState);setContent{MyApplicationTheme{Surface(Modifier.fillMaxSize()){TrackerApp(vm)}}}}
}