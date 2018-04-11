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