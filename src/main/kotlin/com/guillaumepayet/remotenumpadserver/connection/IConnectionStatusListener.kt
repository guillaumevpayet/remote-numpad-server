package com.guillaumepayet.remotenumpadserver.connection

/**
 * Connection status listeners are notified by [IConnectionInterface] objects for any change in the
 * [ConnectionStatus] (e.g.: connection opened, closed, lost, etc.).
 *
 * @see IConnectionInterface
 * @see ConnectionStatus
 */
interface IConnectionStatusListener {

    /**
     * Method called when the connection has changed state.
     *
     * @param connectionStatus the new state of the connection
     */
    fun onConnectionStatusChange(connectionStatus: ConnectionStatus)
}