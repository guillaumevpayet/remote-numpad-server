package com.guillaumepayet.remotenumpadserver.processing

/**
 * A key event generator generates key events and sends them as keycodes to (virtual) keypads.
 */
interface IKeyEventGenerator {

    /**
     * Register a keypad for receiveing key events.
     */
    fun registerKeypad(keypad: IKeypad)

    /**
     * Unregister a keypad so it no longer receives key events.
     */
    fun unregisterKeypad(keypad: IKeypad)
}