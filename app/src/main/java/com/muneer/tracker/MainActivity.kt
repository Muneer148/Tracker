package com.muneer.tracker
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.muneer.tracker.ui.TrackerRoot
class MainActivity:ComponentActivity(){
 private val vm:TrackerViewModel by viewModels()
 override fun onCreate(savedInstanceState:Bundle?){super.onCreate(savedInstanceState);setContent{TrackerRoot(vm)}}
 override fun onResume(){super.onResume();vm.refreshToday()}
}