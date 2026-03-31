package de.nrunodos.plugins

import com.intellij.diff.DiffContext
import com.intellij.diff.FrameDiffTool.DiffViewer
import com.intellij.diff.tools.util.base.DiffViewerBase
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.ui.Messages
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBList
import com.intellij.util.ui.JBUI
import java.awt.BorderLayout
import java.util.regex.PatternSyntaxException
import javax.swing.*

class IntegratedConfigurationPanel(
    val context: DiffContext,
    private val viewer: DiffViewer
) {
    private val configuration = CustomDiffConfigState.getInstance()

    val rootPanel: JPanel = JPanel(BorderLayout())
    private val whitespaceToggle: JCheckBox = JBCheckBox("Ignore whitespaces")

    private val listModel = DefaultListModel<String>()
    private val ignorePatterns: JBList<String> = JBList(listModel)

    private val psiListModel = DefaultListModel<String>()
    private val ignoredPsiPaths: JBList<String> = JBList(psiListModel)

    init {
        val topPanel = JPanel(BorderLayout())
        topPanel.add(whitespaceToggle, BorderLayout.WEST)
        topPanel.border = JBUI.Borders.empty(5)
        rootPanel.add(topPanel, BorderLayout.NORTH)

        val mainContent = JPanel()
        mainContent.layout = BoxLayout(mainContent, BoxLayout.X_AXIS)

        // Regex Patterns section
        val patternsPanel = createRegexPatternsPanel()

        val patternsWrapper = JPanel(BorderLayout())
        patternsWrapper.add(JLabel("Regex Patterns:"), BorderLayout.NORTH)
        patternsWrapper.add(patternsPanel, BorderLayout.CENTER)
        patternsWrapper.border = JBUI.Borders.empty(5)

        // PSI Elements section
        val psiPanel = createPsiPathsPanel()

        val psiWrapper = JPanel(BorderLayout())
        psiWrapper.add(JLabel("Ignored PSI Elements (Alt+Click to add):"), BorderLayout.NORTH)
        psiWrapper.add(psiPanel, BorderLayout.CENTER)
        psiWrapper.border = JBUI.Borders.empty(5)

        mainContent.add(patternsWrapper)
        mainContent.add(psiWrapper)
        rootPanel.add(mainContent, BorderLayout.CENTER)

        // Initial values
        whitespaceToggle.isSelected = configuration.ignoreWhitespaces
        whitespaceToggle.addActionListener {
            configuration.ignoreWhitespaces = whitespaceToggle.isSelected
            refreshDiffWindow()
        }

        listModel.clear()
        listModel.addAll(configuration.ignorePatterns)

        psiListModel.clear()
        psiListModel.addAll(configuration.ignoredPsiPaths)
    }

    private fun createRegexPatternsPanel(): JComponent {
        return ToolbarDecorator.createDecorator(ignorePatterns)
            .setAddAction {
                val input = Messages.showInputDialog(rootPanel, "Enter a new pattern", "Add Pattern", null)
                if (!input.isNullOrBlank() && !listModel.contains(input) && validateAndAddRegex(input)) {
                    refreshDiffWindow()
                }
            }
            .setEditAction {
                val selectedValue = ignorePatterns.selectedValue
                if (selectedValue != null) {
                    val input =
                        Messages.showInputDialog(rootPanel, "Edit pattern", "Edit Pattern", null, selectedValue, null)
                    if (!input.isNullOrBlank() && input != selectedValue && validateAndReplaceRegex(
                            selectedValue,
                            input
                        )
                    ) {
                        refreshDiffWindow()
                    }
                }
            }
            .setRemoveAction {
                val selectedValue = ignorePatterns.selectedValue
                if (selectedValue != null) {
                    listModel.removeElement(selectedValue)
                    configuration.ignorePatterns.remove(selectedValue)
                    refreshDiffWindow()
                }
            }
            .disableUpDownActions()
            .createPanel()
    }

    private fun validateAndAddRegex(input: String): Boolean {
        try {
            Regex(input)
            listModel.addElement(input)
            configuration.ignorePatterns.add(input)
            return true
        } catch (e: PatternSyntaxException) {
            showInvalidRegexNotification(e)
            return false
        }
    }

    private fun validateAndReplaceRegex(oldValue: String, newValue: String): Boolean {
        try {
            Regex(newValue)
            listModel.removeElement(oldValue)
            configuration.ignorePatterns.remove(oldValue)
            listModel.addElement(newValue)
            configuration.ignorePatterns.add(newValue)
            return true
        } catch (e: PatternSyntaxException) {
            showInvalidRegexNotification(e)
            return false
        }
    }

    private fun showInvalidRegexNotification(e: PatternSyntaxException) {
        NotificationGroupManager.getInstance()
            .getNotificationGroup("Invalid regex")
            .createNotification(
                "Invalid pattern",
                "${e.pattern} is not a valid regex pattern: ${e.message}",
                NotificationType.ERROR,
            ).notify(null)
    }

    private fun createPsiPathsPanel(): JComponent {
        return ToolbarDecorator.createDecorator(ignoredPsiPaths)
            .setAddAction {
                val input = Messages.showInputDialog(rootPanel, "Enter a new PSI path prefix", "Add PSI Path", null)
                if (!input.isNullOrBlank() && !psiListModel.contains(input)) {
                    psiListModel.addElement(input)
                    configuration.ignoredPsiPaths.add(input)
                    refreshDiffWindow()
                }
            }
            .setEditAction {
                val selectedValue = ignoredPsiPaths.selectedValue
                if (selectedValue != null) {
                    val input = Messages.showInputDialog(
                        rootPanel,
                        "Edit PSI path prefix",
                        "Edit PSI Path",
                        null,
                        selectedValue,
                        null
                    )
                    if (!input.isNullOrBlank() && input != selectedValue) {
                        psiListModel.removeElement(selectedValue)
                        configuration.ignoredPsiPaths.remove(selectedValue)
                        psiListModel.addElement(input)
                        configuration.ignoredPsiPaths.add(input)
                        refreshDiffWindow()
                    }
                }
            }
            .setRemoveAction {
                val selectedValue = ignoredPsiPaths.selectedValue
                if (selectedValue != null) {
                    psiListModel.removeElement(selectedValue)
                    configuration.ignoredPsiPaths.remove(selectedValue)
                    refreshDiffWindow()
                }
            }
            .disableUpDownActions()
            .createPanel()
    }

    fun refreshConfiguration() {
        psiListModel.clear()
        psiListModel.addAll(configuration.ignoredPsiPaths)
        listModel.clear()
        listModel.addAll(configuration.ignorePatterns)
        whitespaceToggle.isSelected = configuration.ignoreWhitespaces
    }

    private fun refreshDiffWindow() {
        when (viewer) {
            is DiffViewerBase -> viewer.scheduleRediff()
        }
    }
}
