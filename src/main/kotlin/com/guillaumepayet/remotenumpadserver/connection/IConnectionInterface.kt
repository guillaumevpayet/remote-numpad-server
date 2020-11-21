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

package com.guillaumepayet.remotenumpadserver.connection

/**
 * This interface describes how an object can receive data from a client or other interface for cross-
 * -application communication. When the connection status of the connection changes,
 * [IConnectionStatusListener] objects are notified. The data receives is passed on to an [IDataProcessor]
 * object for processing.
 *
 * @see IConnectionStatusListener
 * @see IDataProcessor
 */
interface IConnectionInterface : IConnectionStatusListener {

    val name: String

    /**
     * Register an [IConnectionStatusListener] object to be notified when the connection status
     * changes.
     *
     * @param listener the connection status listener to register
     */
    fun registerConnectionStatusListener(listener: IConnectionStatusListener)

    /**
     * Unregister an [IConnectionStatusListener] object that it is no longer notified of connection
     * status changes.
     *
     * @param listener the connection status listener to unregister
     */
    fun unregisterConnectionStatusListener(listener: IConnectionStatusListener)

    /**
     * Register an [IDataProcessor] object to process the data when it arrives.
     *
     * @param processor the data processor to register
     */
    fun registerDataProcessor(processor: IDataProcessor)

    /**
     * Unregister an [IDataProcessor] object so that it no longer processes the data when it arrives.
     *
     * @param processor the data processor to unregister
     */
    fun unregisterDataProcessor(processor: IDataProcessor)

    /**
     * Start listening for a connection from a client.
     */
    fun listen()

    /**
     * Stop listening (closes any open connection).
     */
    fun stop()
}