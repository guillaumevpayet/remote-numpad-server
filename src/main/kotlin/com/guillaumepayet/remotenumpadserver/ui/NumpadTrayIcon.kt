package com.guillaumepayet.remotenumpadserver.ui

import com.guillaumepayet.remotenumpadserver.Constants
import java.awt.MenuItem
import java.awt.PopupMenu
import java.awt.SystemTray
import java.awt.TrayIcon
import javax.imageio.ImageIO

/**
 * The tray icon is showing in the system tray if one is available, otherwise, it opens the status window directly.
 */
class NumpadTrayIcon(private val openAction: () -> Unit, closeAction: () -> Unit) {

    companion object {

        var isAvailable = false
            private set

        private val systemTray: SystemTray? = try {
            SystemTray.getSystemTray()
        } catch (e: UnsupportedOperationException) {
            System.err.println("No system tray found, not using system tray icon.")
            null
        } catch (e: SecurityException) {
            System.err.println("Not authorised to access system tray, not using system tray icon.")
            null
        }
    }


    private lateinit var trayIcon: TrayIcon

    init {
        val image = if (systemTray == null) {
            null
        } else try {
            ImageIO.read(javaClass.getResource("/Icon.png"))
        } catch (e: Exception) {
            System.err.println("Unable to load the icon file, not using system tray icon.")
            null
        }

        if (image != null) {
            val popupMenu = PopupMenu()
            trayIcon = TrayIcon(image, Constants.appName, popupMenu)

            val openItem = MenuItem("Open")
            openItem.addActionListener { openAction() }

            val exitItem = MenuItem("Exit")
            exitItem.addActionListener { closeAction() }

            popupMenu.add(openItem)
            popupMenu.add(exitItem)

            trayIcon.addActionListener { openAction() }
            trayIcon.isImageAutoSize = true
            isAvailable = true
        }
    }

    fun show() {
        if (isAvailable) {
            systemTray?.add(trayIcon)
        } else {
            openAction()
        }
    }

    fun hide() {
        if (isAvailable)
            systemTray?.remove(trayIcon)
    }
}