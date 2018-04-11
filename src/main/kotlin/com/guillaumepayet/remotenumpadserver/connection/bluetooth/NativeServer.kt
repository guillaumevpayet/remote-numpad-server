package com.guillaumepayet.remotenumpadserver.connection.bluetooth

import com.guillaumepayet.remotenumpadserver.connection.ConnectionStatus
import org.scijava.nativelib.NativeLoader
import java.io.FileOutputStream
import java.io.FileNotFoundException
import java.io.File
import java.io.IOException

/**
 * A bluetooth server based on a native-made native library.
 */
@Suppress("unused")
class NativeServer(private val connectionInterface: BluetoothConnectionInterface) : IBluetoothServer {

    companion object {

        private var serviceDictionary = ""

        /**
         * This "static" constructor checks and loads the library.
         */
        init {
            val tmpdir = System.getProperty("java.library.path").split(':')[0]
            System.setProperty("java.library.tmpdir", tmpdir)
            NativeLoader.loadLibrary("NativeServer")

            val os = System.getProperty("os.name").toLowerCase()

            // The MacOS library has an extra dependency, this extracts it
			if (os.startsWith("mac")) {
				val insidePath = "/ServiceDictionary.plist"
				val outsidePath = tmpdir + insidePath

				try {
					extractResource(insidePath, outsidePath)
				} catch (e: IOException) {
					System.err.println("Unable to extract the Bluetooth service dictionary.")
				}
			}
        }

        /**
         * Extract a file from the JAR.
         */
        private fun extractResource(path: String, to: String?): File {
            val prefix = path.substring(1, path.lastIndexOf('.'))
            val suffix = path.substring(path.lastIndexOf('.'))

            val file: File

            if (to != null) {
                file = File(to)
                file.createNewFile()
            } else {
                file = File.createTempFile(prefix, suffix)
            }

            if (!file.exists())
                throw FileNotFoundException()

            file.deleteOnExit()

            NativeServer::class.java.getResourceAsStream(path).use({ inputStream ->
                val buffer = ByteArray(inputStream.available())
                inputStream.read(buffer)

                FileOutputStream(file).use { outputStream -> outputStream.write(buffer) }
            })

            serviceDictionary = file.path
            return file
        }
    }


    init {
        setProperty("service_dictionary", serviceDictionary)
    }


    external override fun open(uuid: String): Boolean

    external override fun close()


    /**
     * Set a property in the native implementation.
     */
    private external fun setProperty(key: String, value: String)


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
        connectionInterface.onStringReception(string)
    }
}