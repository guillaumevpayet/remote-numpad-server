package com.guillaumepayet.remotenumpadserver.connection.bluetooth

import java.io.Closeable

/**
 * This interface must be implemented by all back-ends of the bluetooth connection interface.
 */
interface IBluetoothServer : Closeable {

    /**
     * Open the server. This method may block until the app is closed as long as it is working.
     * If the server is not available, return false, otherwise, return true.
     */
    fun open(uuid: String): Boolean
}