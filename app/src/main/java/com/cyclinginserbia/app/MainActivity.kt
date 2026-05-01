package com.cyclinginserbia.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.cyclinginserbia.app.ui.navigation.RootNavigation
import com.cyclinginserbia.app.ui.theme.CyclingInSerbiaTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            CyclingInSerbiaTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    RootNavigation()
                }
            }
        }
    }
}
