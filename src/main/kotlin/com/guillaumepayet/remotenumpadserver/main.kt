/*
 * Remote Numpad Server - a server for the Remote Numpad.
 * Copyright (C) 2016-2020 Guillaume Payet
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.guillaumepayet.remotenumpadserver

import com.guillaumepayet.remotenumpadserver.connection.IConnectionInterface
import com.guillaumepayet.remotenumpadserver.connection.bluetooth.BluetoothConnectionInterface
import com.guillaumepayet.remotenumpadserver.connection.socket.SocketConnectionInterface
import com.guillaumepayet.remotenumpadserver.processing.KeyEventProcessor
import com.guillaumepayet.remotenumpadserver.processing.VirtualNumpad
import com.guillaumepayet.remotenumpadserver.ui.NumpadTrayIcon
import com.guillaumepayet.remotenumpadserver.ui.StatusWindow
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.atomic.AtomicBoolean

/**
 * The main function runs in a main coroutine, starts the other coroutines and waits for then to complete before
 * cleaning up and closing.
 */
fun main() = runBlocking {
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
    connectionInterfaces.forEach { GlobalScope.launch { it.listen() } }

    // Wait for the "close" signal (from the tray icon or the window)
    while (running.get())
        Thread.sleep(20)

    // Stop the servers
    connectionInterfaces.forEach { it.stop() }

    // Destroy the UI
    window.dispose()
    trayIcon.hide()
}