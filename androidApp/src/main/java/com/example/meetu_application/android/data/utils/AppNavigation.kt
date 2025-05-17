import androidx.compose.animation.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.meetu_application.android.ui.screens.MainScreen
import com.example.meetu_application.android.ui.screens.WalletScreen
import com.google.accompanist.navigation.animation.composable

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalAnimationApi::class)
@Composable
fun AppNavigation(navController: NavHostController) {
    SharedTransitionLayout {
        NavHost(navController = navController, startDestination = "main") {
            composable("main") { MainScreen(navController) }
            composable("wallet") { WalletScreen(navController) }
        }
    }
}
