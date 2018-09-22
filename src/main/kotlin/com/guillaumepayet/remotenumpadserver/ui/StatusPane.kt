/*
 * Remote Numpad Server - a server for the Remote Numpad.
 * Copyright (C) 2016-2018 Guillaume Payet
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

package com.guillaumepayet.remotenumpadserver.ui

import com.guillaumepayet.remotenumpadserver.connection.IConnectionInterface
import com.guillaumepayet.remotenumpadserver.connection.ConnectionStatus
import com.guillaumepayet.remotenumpadserver.connection.IConnectionStatusListener
import java.awt.Color
import java.awt.Dimension
import java.util.*
import javax.swing.*
import kotlin.concurrent.*

/**
 * Each status pane is linked to a connection interface. It reports on the status of the connection interface.
 */
class StatusPane(connectionInterface: IConnectionInterface) : JPanel(), IConnectionStatusListener {

    companion object {

        /**
         * The texts to display for each connection status
         */
        private val texts = mapOf(
                Pair(ConnectionStatus.SERVER_UNAVAILABLE, "Unavailable"),
                Pair(ConnectionStatus.SERVER_STOPPED, "Server stopped"),
                Pair(ConnectionStatus.SERVER_READY, "Server ready"),
                Pair(ConnectionStatus.CLIENT_CONNECTED, "Client connected"),
                Pair(ConnectionStatus.CLIENT_DISCONNECTED, "Client disconnected")
        )

        /**
         * The color to use for each connection status
         */
        private val colors = mapOf(
                Pair(ConnectionStatus.SERVER_UNAVAILABLE, Color.GRAY),
                Pair(ConnectionStatus.SERVER_STOPPED, Color.GRAY),
                Pair(ConnectionStatus.SERVER_READY, Color(0, 180, 0)),
                Pair(ConnectionStatus.CLIENT_CONNECTED, Color.BLUE),
                Pair(ConnectionStatus.CLIENT_DISCONNECTED, Color(0, 180, 0))
        )
    }


    private val statusText = JLabel()
    private var task: TimerTask? = null

    init {
        connectionInterface.registerConnectionStatusListener(this)

        layout = BoxLayout(this, BoxLayout.X_AXIS)

        add(Box.createHorizontalGlue())

        val label = JLabel(connectionInterface.name)
        label.preferredSize = Dimension(100, 20)
        add(label)

        add(Box.createHorizontalGlue())

        statusText.preferredSize = Dimension(150, 20)
        add(statusText)

        add(Box.createHorizontalGlue())

        preferredSize = Dimension(250, 20)
    }


    override fun onConnectionStatusChange(connectionStatus: ConnectionStatus) {
        task?.cancel()
        task = null

        statusText.text = texts[connectionStatus]
        statusText.foreground = colors[connectionStatus]

        if (connectionStatus == ConnectionStatus.CLIENT_DISCONNECTED) {
            task = java.util.Timer().schedule(2000) {
                onConnectionStatusChange(ConnectionStatus.SERVER_READY)
            }
        }
    }
}