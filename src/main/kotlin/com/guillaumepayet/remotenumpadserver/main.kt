package com.guillaumepayet.remotenumpadserver

import com.guillaumepayet.remotenumpadserver.connection.IConnectionInterface
import com.guillaumepayet.remotenumpadserver.connection.bluetooth.BluetoothConnectionInterface
import com.guillaumepayet.remotenumpadserver.connection.socket.SocketConnectionInterface
import com.guillaumepayet.remotenumpadserver.processing.KeyEventProcessor
import com.guillaumepayet.remotenumpadserver.processing.VirtualNumpad
import com.guillaumepayet.remotenumpadserver.ui.NumpadTrayIcon
import com.guillaumepayet.remotenumpadserver.ui.StatusWindow
import java.util.concurrent.atomic.AtomicBoolean

fun main(args: Array<String>) {
    // Prepare the server(s)
    val connectionInterfaces = ArrayList<IConnectionInterface>()
    connectionInterfaces += SocketConnectionInterface()
    connectionInterfaces += BluetoothConnectionInterface()

    // Setup the UI
    val running = AtomicBoolean(true)
    val closeAction = { running.set(false) }
    val window = StatusWindow(connectionInterfaces, closeAction)
    val openAction = { window.isVisible = true }
    val trayIcon = NumpadTrayIcon(openAction, closeAction)

    // Start showing the UI (if there is no tray icon, the window is opened)
    trayIcon.show()

    // Prepare the virtual numpad
    val keyEventProcessor = KeyEventProcessor(connectionInterfaces)
    VirtualNumpad(keyEventProcessor)

    // Start the server(s)
    val serversThreads = connectionInterfaces.map {
        val serverThread = Thread(it::listen)
        serverThread.start()
        serverThread
    }

    // Wait for the "close" signal (from the tray icon or the window)
    while (running.get())
        Thread.yield()

    // Stop the servers
    connectionInterfaces.forEach { it.stop() }
    serversThreads.forEach { it.join() }

    // Destroy the UI
    window.dispose()
    trayIcon.hide()

    // TODO Find a better way to kill the Qt Bluetooth thread
    System.exit(0)
}