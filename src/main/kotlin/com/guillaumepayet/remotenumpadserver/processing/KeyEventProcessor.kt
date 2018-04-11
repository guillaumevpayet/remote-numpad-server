package com.guillaumepayet.remotenumpadserver.processing

import com.guillaumepayet.remotenumpadserver.connection.IConnectionInterface
import com.guillaumepayet.remotenumpadserver.connection.IDataProcessor
import java.awt.event.KeyEvent

/**
 * This class processes key events and sends them over to a virtual keypad.
 */
class KeyEventProcessor(connectionInterfaces: Iterable<IConnectionInterface>) : IDataProcessor, IKeyEventGenerator {

    private val keypads = HashSet<IKeypad>()


    init {
        connectionInterfaces.forEach { it.registerDataProcessor(this) }
    }


    override fun processString(string: String) {
        val eventType = string[0]
        val keyName = string.substring(1)

        if (eventType == '+') {
            keypads.forEach { it.pressKey(keyCodeFromName(keyName)) }
        } else if (eventType == '-') {
            keypads.forEach { it.releaseKey(keyCodeFromName(keyName)) }
        }
    }


    override fun registerKeypad(keypad: IKeypad) {
        keypads.add(keypad)
    }

    override fun unregisterKeypad(keypad: IKeypad) {
        keypads.remove(keypad)
    }


    private fun keyCodeFromName(keyName: String): Int {
        return try {
            KeyEvent.VK_NUMPAD0 + keyName.toInt()
        } catch (e: NumberFormatException) {
            when (keyName.toLowerCase()) {
                "enter" -> KeyEvent.VK_ENTER
                "/" -> KeyEvent.VK_DIVIDE
                "*" -> KeyEvent.VK_MULTIPLY
                "-" -> KeyEvent.VK_SUBTRACT
                "+" -> KeyEvent.VK_ADD
                "." -> KeyEvent.VK_DECIMAL
                else -> KeyEvent.VK_NUM_LOCK
            }
        }
    }
}