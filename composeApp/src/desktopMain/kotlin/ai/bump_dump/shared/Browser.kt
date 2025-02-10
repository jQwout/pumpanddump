package ai.bump_dump.shared

import java.awt.Desktop
import java.net.URI


fun openBrowser(url: String) {
    val desktop = Desktop.getDesktop()
    if (desktop.isSupported(Desktop.Action.BROWSE)) {
        desktop.browse(URI(url))
    } else {
        println("Desktop browsing not supported")
    }
}