package com.guillaumepayet.remotenumpadserver.processing

import java.awt.Robot

/**
 * This virtual keypad simulates key events using Java's Robot API.
 */
class VirtualNumpad(keyEventGenerator: IKeyEventGenerator) : IKeypad {

    private val robot = Robot()


    init {
        keyEventGenerator.registerKeypad(this)
    }


    @Synchronized
    override fun pressKey(keyCode: Int) {
        robot.keyPress(keyCode)
    }

    @Synchronized
    override fun releaseKey(keyCode: Int) {
        robot.keyRelease(keyCode)
    }
}