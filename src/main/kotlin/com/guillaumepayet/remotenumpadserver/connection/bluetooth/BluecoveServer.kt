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

package com.guillaumepayet.remotenumpadserver.connection.bluetooth

import com.guillaumepayet.remotenumpadserver.connection.ConnectionStatus
import java.io.IOException
import javax.bluetooth.ServiceRegistrationException
import javax.microedition.io.Connector
import javax.microedition.io.StreamConnection
import javax.microedition.io.StreamConnectionNotifier

/**
 * A bluetooth server based on Bluecove. It works on Windows and Linux. It is not used on MacOS because it uses
 * removed (obsoleted) APIs.
 *
 * @param connectionInterface The connection interface to send data to
 */
class BluecoveServer(private val connectionInterface: BluetoothConnectionInterface) : IBluetoothServer {

    @Volatile
    private lateinit var notifier: StreamConnectionNotifier

    @Volatile
    private var running = true

    @Volatile
    private var connection: StreamConnection? = null


    override fun open(uuid: String): Boolean {
        // Bluecove's URLs have this specific format
        val url = "btspp://localhost:" + uuid.replace("-", "") + ";name=RemoteNumpad"

        try {
            notifier = Connector.open(url) as StreamConnectionNotifier
        } catch (e: ServiceRegistrationException) {
            System.err.println("Bluecove: Unable to register the bluetooth service: " + e.message)
            return false
        }

        connectionInterface.onConnectionStatusChange(ConnectionStatus.SERVER_READY)

        while (running) {
            // connection is null when the application is closing
            connection = try {
                notifier.acceptAndOpen()
            } catch (e: IOException) {
                null
            }

            // This block only executes when connection is non-null
            connection?.openInputStream()?.reader()?.buffered()?.use { reader ->
                connectionInterface.onConnectionStatusChange(ConnectionStatus.CLIENT_CONNECTED)
                var input: String? = ""

                while (input != null) {
                    // Block until a line is received
                    input = reader.readLine()

                    if (input != null)
                        // The input is sent directly to the processor
                        connectionInterface.onStringReception(input)
                }

                connectionInterface.onConnectionStatusChange(ConnectionStatus.CLIENT_DISCONNECTED)
            }

            disconnect()
        }

        return true
    }

    override fun close() {
        disconnect()
        running = false

        try {
            notifier.close()
        } catch (e: UninitializedPropertyAccessException) {
        }
    }


    /**
     * Close the open connection.
     */
    private fun disconnect() {
        connection?.close()
        connection = null
    }
}