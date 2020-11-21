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

package com.guillaumepayet.remotenumpadserver.ui

import com.guillaumepayet.remotenumpadserver.Constants
import com.guillaumepayet.remotenumpadserver.connection.IConnectionInterface
import java.awt.Dimension
import javax.swing.*

/**
 * The status window is the main window of this application.
 * It stacks the required number of status panes, one for each connection interface available.
 *
 * @constructor Create the layout of the window
 * @param connectionInterfaces A list of the [IConnectionInterface] to display
 * @param closeAction The action to use when the application is to be closed
 */
class StatusWindow(connectionInterfaces: Iterable<IConnectionInterface>, private val closeAction: () -> Unit)
    : JFrame(Constants.APP_NAME) {

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