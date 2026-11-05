package cl.duoc.zaror

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import cl.duoc.zaror.ui.AppNav
import cl.duoc.zaror.ui.theme.ZarorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ZarorTheme {
                AppNav()
            }
        }
    }
}
