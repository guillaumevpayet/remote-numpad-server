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


    private fun disconnect() {
        connection?.close()
        connection = null
    }
}