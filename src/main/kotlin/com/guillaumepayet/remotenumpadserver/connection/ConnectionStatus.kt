package com.guillaumepayet.remotenumpadserver.connection

/**
 * The status of the connection.
 */
enum class ConnectionStatus {
    SERVER_UNAVAILABLE, SERVER_STOPPED, SERVER_READY, CLIENT_CONNECTED, CLIENT_DISCONNECTED
}