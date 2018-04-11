package com.guillaumepayet.remotenumpadserver.connection

/**
 * An abstract connection interface which should be extended by all connection interfaces.
 * This class handles all the generic observer-observable operations for connection interfaces.
 */
abstract class AbstractConnectionInterface
    : IConnectionInterface {

    private val listeners = HashSet<IConnectionStatusListener>()
    private val processors = HashSet<IDataProcessor>()


    override fun registerConnectionStatusListener(listener: IConnectionStatusListener) {
        listeners.add(listener)
    }

    override fun unregisterConnectionStatusListener(listener: IConnectionStatusListener) {
        listeners.remove(listener)
    }

    override fun registerDataProcessor(processor: IDataProcessor) {
        processors.add(processor)
    }

    override fun unregisterDataProcessor(processor: IDataProcessor) {
        processors.remove(processor)
    }

    override fun onConnectionStatusChange(connectionStatus: ConnectionStatus) {
        listeners.forEach { it.onConnectionStatusChange(connectionStatus) }
    }


    fun onStringReception(string: String) {
        processors.forEach { it.processString(string) }
    }
}