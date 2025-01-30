package de.nrunodos.plugins

import com.intellij.diff.DiffContext
import com.intellij.diff.FrameDiffTool.DiffViewer
import com.intellij.diff.tools.util.base.DiffViewerBase
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBList
import java.util.regex.PatternSyntaxException
import javax.swing.BoxLayout
import javax.swing.DefaultListModel
import javax.swing.JButton
import javax.swing.JCheckBox
import javax.swing.JList
import javax.swing.JOptionPane
import javax.swing.JPanel
import javax.swing.JScrollPane

class IntegratedConfigurationPanel(
    val context: DiffContext,
    private val viewer: DiffViewer
) {
    private val configuration = CustomDiffConfigState.getInstance()

    val rootPanel: JPanel = JPanel()
    private val whitespaceToggle: JCheckBox = JBCheckBox("Ignore whitespaces")
    private val listModel = DefaultListModel<String>()
    private val ignorePatterns: JList<String> = JBList(listModel)
    private val addButton = JButton("Add pattern")
    private val removeButton = JButton("Remove pattern")

    init {
        rootPanel.layout = BoxLayout(rootPanel, BoxLayout.Y_AXIS)
        rootPanel.add(whitespaceToggle)
        ignorePatterns.visibleRowCount = 3
        rootPanel.add(JScrollPane(ignorePatterns))
        rootPanel.add(addButton)
        rootPanel.add(removeButton)

        whitespaceToggle.isSelected = configuration.ignoreWhitespaces
        whitespaceToggle.addActionListener {
            refreshDiffWindow()
        }
        listModel.clear()
        listModel.addAll(configuration.ignorePatterns)

        addButton.addActionListener {
            val input = JOptionPane.showInputDialog("Enter a new pattern")
            if (!input.isNullOrBlank() && !listModel.contains(input)) {
                try {
                    Regex(input)
                } catch (e: PatternSyntaxException) {
                    NotificationGroupManager
                        .getInstance()
                        .getNotificationGroup("Invalid regex")
                        .createNotification(
                            "Invalid pattern",
                            "${e.pattern} is not a valid regex pattern: ${e.message}",
                            NotificationType.ERROR,
                        ).notify(null)
                    return@addActionListener
                }
                listModel.addElement(input)
                configuration.ignorePatterns.add(input)
                refreshDiffWindow()
            }
        }

        removeButton.addActionListener {
            ignorePatterns.selectedValue?.let {
                val selectedValue = ignorePatterns.selectedValue
                listModel.removeElement(selectedValue)
                configuration.ignorePatterns.remove(selectedValue)
            }
            refreshDiffWindow()
        }
    }

    private fun refreshDiffWindow() {
        when (viewer) {
            is DiffViewerBase -> viewer.scheduleRediff()
        }
    }
}
