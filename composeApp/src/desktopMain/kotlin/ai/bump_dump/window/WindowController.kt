package ai.bump_dump.window


import ai.bump_dump.assets.ui.uiThemeColors
import ai.bump_dump.nav.MainRouter
import ai.bump_dump.screener.ui.SignalListPresenter
import ai.bump_dump.screener.ui.SignalListScreen
import ai.bump_dump.settings.ui.SignalListSettingsPresenter
import ai.bump_dump.settings.ui.SignalListSettingsScreen
import ai.bump_dump.shared.Callback
import ai.bump_dump.shared.ServiceLocator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
@Preview
fun WindowController(
    exitApplication: Callback,
    title: String = "Dump And Bump",
    minHeight: Dp = 800.dp,
    minWidth: Dp = 560.dp
) {

    // mainWindow
    Window(
        onCloseRequest = exitApplication,
        title = title,
        alwaysOnTop = true,
    ) {
        window.minimumSize = java.awt.Dimension(minWidth.value.toInt(), minHeight.value.toInt())
        window.maximumSize = java.awt.Dimension(minWidth.value.toInt(), Int.MAX_VALUE)

        ServiceLocator.mainPresenter.isDarkTheme?.let { colors ->
            MaterialTheme(colors = uiThemeColors(colors)) {
                Content(
                    signalListPresenter = ServiceLocator.signalListPresenter,
                    settingsPresenter = ServiceLocator.settingsPresenter
                )
            }
        }
    }
}


@Composable
private fun Content(
    navController: NavHostController = rememberNavController(),
    signalListPresenter: SignalListPresenter,
    settingsPresenter: SignalListSettingsPresenter,
) {
    // Get current back stack entry
    val backStackEntry by navController.currentBackStackEntryAsState()
    // Get the name of the current screen
    val currentScreen = backStackEntry?.destination?.route ?: MainRouter.List.name

    Scaffold { innerPadding ->
        NavHost(navController = navController, startDestination = currentScreen) {
            composable(route = MainRouter.List.name) {
                SignalListScreen(signalListPresenter) {
                    navController.navigate(MainRouter.Settings.name)
                }
            }
            composable(route = MainRouter.Settings.name) {
                SignalListSettingsScreen(settingsPresenter) {
                    navController.navigateUp()
                }
            }
        }
    }
}