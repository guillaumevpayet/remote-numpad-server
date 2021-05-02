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

import com.guillaumepayet.remotenumpadserver.connection.ConnectionStatus
import org.scijava.nativelib.NativeLibraryUtil
import org.scijava.nativelib.NativeLoader
import java.io.FileOutputStream
import java.io.FileNotFoundException
import java.io.File
import java.io.IOException

/**
 * A bluetooth server based on a native-made native library.
 *
 * @constructor Save the path to the service dictionary into a property
 */
class NativeServer(private val connectionInterface: BluetoothConnectionInterface) : IBluetoothServer {

    /**
     * @constructor The "static" constructor checks and loads the library.
     */
    companion object {

        private const val LIBRARY_NAME = "NativeServer"

        /**
         * Extract a file from the JAR.
         */
        private fun extractResource(path: String, to: String?): File {
            val prefix = path.substring(1, path.lastIndexOf('.'))
            val suffix = path.substring(path.lastIndexOf('.'))

            val file: File

            if (to != null) {
                file = File(to)
                file.parentFile.mkdirs()
                file.createNewFile()
            } else {
                file = File.createTempFile(prefix, suffix)
            }

            if (!file.exists())
                throw FileNotFoundException()

            file.deleteOnExit()

            NativeServer::class.java.getResourceAsStream(path)?.use { inputStream ->
                val buffer = ByteArray(inputStream.available())
                inputStream.read(buffer)

                FileOutputStream(file).use { outputStream -> outputStream.write(buffer) }
            }

            return file
        }
    }


    init {
        val tmpdir = System.getProperty("java.library.path").split(':')[0]
        System.setProperty("java.library.tmpdir", tmpdir)
        val libraryName = NativeLibraryUtil.getPlatformLibraryName(LIBRARY_NAME)
        val outsideLib = extractResource("/$libraryName", "$tmpdir/$libraryName")

        if (!outsideLib.exists())
            throw IOException("Unable to extract native library.")

        try {
            NativeLoader.loadLibrary(LIBRARY_NAME)
        } catch (e: IOException) {
            val cause = e.cause!!
            System.err.println("Error ${cause.javaClass}: ${cause.message}")
            throw e
        }
    }


    external override fun open(uuid: String): Boolean

    external override fun close()


    /**
     * Notify the connection interface of a status change.
     */
    private fun connectionStatusChanged(connectionStatusString: String) {
        val connectionStatus = enumValueOf<ConnectionStatus>(connectionStatusString)
        connectionInterface.onConnectionStatusChange(connectionStatus)
    }

    /**
     * Notify the connection interface when data is received.
     */
    private fun stringReceived(string: String) {
        println("stringReceived('$string')")
//        connectionInterface.onStringReception(string)
    }
}