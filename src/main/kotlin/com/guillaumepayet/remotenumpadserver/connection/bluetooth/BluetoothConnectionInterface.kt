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

package com.guillaumepayet.remotenumpadserver.connection.bluetooth

import com.guillaumepayet.remotenumpadserver.connection.AbstractConnectionInterface
import com.guillaumepayet.remotenumpadserver.connection.ConnectionStatus
import java.io.IOException

/**
 * A connection interface based on Bluetooth. It uses a different backend depending on availability and operating
 * system.
 *
 * @constructor Pick an implementation depending on the OS
 */
class BluetoothConnectionInterface : AbstractConnectionInterface() {

    companion object {

        /**
         * The UUID of the "Remote Numpad" service
         */
        private const val uuid = "6be5ccef-5d32-48e3-a3a0-d89e558a40f1"
    }


    override val name: String
        get() = "Bluetooth"


    /**
     * The list of back-ends to try depending on operating system.
     */
    private val servers: MutableList<IBluetoothServer> = ArrayList()

    @Volatile
    private var server: IBluetoothServer? = null


    init {
        // Populating the list of back-ends
        val os = System.getProperty("os.name").toLowerCase()

        if (os.startsWith("win"))
            servers += BluecoveServer(this)

        if (os.startsWith("mac"))
            try {
                servers += NativeServer(this)
            } catch (e: IOException) {
                System.err.println("Error while loading native library: ${e.message}")
            }
    }


    override fun listen() {
        // Try all back-ends until one works
        for (server in servers) {
            this.server = server

            if (server.open(uuid))
                break
            else
                this.server = null
        }

        if (server == null)
            onConnectionStatusChange(ConnectionStatus.SERVER_UNAVAILABLE)
    }

    override fun stop() {
        server?.close()
        onConnectionStatusChange(ConnectionStatus.SERVER_STOPPED)
    }
}