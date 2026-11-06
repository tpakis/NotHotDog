package ai.thanasakis.nothotdog

import ai.thanasakis.nothotdog.ui.NotHotDogApp
import ai.thanasakis.nothotdog.ui.theme.NotHotDogTheme
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
        )
        setContent {
            NotHotDogTheme {
                NotHotDogApp()
            }
        }
    }
}
