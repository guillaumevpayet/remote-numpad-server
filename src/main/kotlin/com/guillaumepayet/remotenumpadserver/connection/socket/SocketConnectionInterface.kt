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

package com.guillaumepayet.remotenumpadserver.connection.socket

import com.guillaumepayet.remotenumpadserver.connection.AbstractConnectionInterface
import com.guillaumepayet.remotenumpadserver.connection.ConnectionStatus
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketException

/**
 * A connection interface based on sockets.
 */
class SocketConnectionInterface : AbstractConnectionInterface() {

    companion object {

        /**
         * The port to listen to.
         */
        private const val PORT = 4576
    }


    override val name: String
        get() = "Socket (Wifi)"


    @Volatile
    private lateinit var serverSocket: ServerSocket

    @Volatile
    private var clientSocket: Socket? = null


    override fun listen() {
        serverSocket = ServerSocket(PORT)
        onConnectionStatusChange(ConnectionStatus.SERVER_READY)

        while (!serverSocket.isClosed) {
            // clientSocket is null when the application is closing
            clientSocket = try {
                serverSocket.accept()
            } catch (e: SocketException) {
                null
            }

            // This block only executes when clientSocket is non-null
            clientSocket?.use { socket ->
                onConnectionStatusChange(ConnectionStatus.CLIENT_CONNECTED)

                socket.outputStream.writer().use { writer ->
                    socket.inputStream.reader().buffered().use { reader ->
                        var input: String? = ""

                        // Block until a line is received
                        while (input != null) {
                            input = reader.readLine()

                            if (input != null) {
                                if (input.toLowerCase() == "name") {
                                    // When the 'name' string is received, the name of the device is sent back
                                    val name = InetAddress.getLocalHost().hostName
                                    writer.write(name + '\n')
                                    writer.flush()
                                } else {
                                    // The input is sent directly to the processor
                                    onStringReception(input)
                                }
                            }
                        }

                        onConnectionStatusChange(ConnectionStatus.CLIENT_DISCONNECTED)
                    }
                }
            }

            clientSocket = null
        }
    }

    override fun stop() {
        clientSocket?.close()
        clientSocket = null

        try {
            serverSocket.close()
        } finally {
            onConnectionStatusChange(ConnectionStatus.SERVER_STOPPED)
        }
    }
}