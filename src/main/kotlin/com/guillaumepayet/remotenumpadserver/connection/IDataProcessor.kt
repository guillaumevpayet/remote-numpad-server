package com.guillaumepayet.remotenumpadserver.connection

/**
 * A data processor is the class that will process the data received over a connection before it is used.
 */
interface IDataProcessor {

    /**
     * Process a string that was just received.
     */
    fun processString(string: String)
}