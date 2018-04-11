package com.guillaumepayet.remotenumpadserver.ui

import com.guillaumepayet.remotenumpadserver.Constants
import com.guillaumepayet.remotenumpadserver.connection.IConnectionInterface
import java.awt.Dimension
import javax.swing.*

/**
 * The status window is the main window of this application.
 * It stacks the required number of status panes, one for each connection interface available.
 */
class StatusWindow(connectionInterfaces: Iterable<IConnectionInterface>, private val closeAction: () -> Unit)
    : JFrame(Constants.appName) {

    init {
        defaultCloseOperation = DISPOSE_ON_CLOSE

        // Apply the OS's default look&feel
        val laf = UIManager.getSystemLookAndFeelClassName()
        UIManager.setLookAndFeel(laf)

        // Add a menu bar
        val exitItem = JMenuItem("Exit")
        exitItem.addActionListener { closeAction() }

        val menu = JMenu("File")
        menu.add(exitItem)

        val menuBar = JMenuBar()
        menuBar.add(menu)

        jMenuBar = menuBar

        // Lay out the status panes
        contentPane.layout = BoxLayout(contentPane, BoxLayout.Y_AXIS)

        add(Box.createVerticalGlue())

        connectionInterfaces.forEach {
            val pane = StatusPane(it)
            add(pane)

            add(Box.createVerticalGlue())
        }

        preferredSize = Dimension(300, 70 * connectionInterfaces.count())

        pack()
        isResizable = false
        setLocationRelativeTo(null)
    }


    override fun dispose() {
        super.dispose()

        if (!NumpadTrayIcon.isAvailable)
            closeAction()
    }
}