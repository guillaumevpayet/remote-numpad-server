package com.guillaumepayet.remotenumpadserver.processing

/**
 * A virtual keypad which receives key events to simulate them
 */
interface IKeypad {

    /**
     * Simulate a key press
     */
    fun pressKey(keyCode: Int)

    /**
     * Simulate a key release
     */
    fun releaseKey(keyCode: Int)
}