package de.nrunodos.plugins

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBList
import java.util.regex.PatternSyntaxException
import javax.swing.*

class CustomDiffConfigGui {
  private val configuration = CustomDiffConfigState.getInstance()

  val rootPanel: JPanel = JPanel()
  private val whitespaceToggle: JCheckBox = JBCheckBox("Ignore whitespaces")
  private val listModel = DefaultListModel<String>()
  private val ignorePatterns: JList<String> = JBList(listModel)
  private val addButton = JButton("Add pattern")
  private val removeButton = JButton("Remove pattern")

  init {
    init()
  }

  private fun init() {
    rootPanel.layout = BoxLayout(rootPanel, BoxLayout.Y_AXIS)
    rootPanel.add(whitespaceToggle)
    rootPanel.add(JScrollPane(ignorePatterns))
    rootPanel.add(addButton)
    rootPanel.add(removeButton)

    whitespaceToggle.isSelected = configuration.ignoreWhitespaces
    listModel.clear()
    listModel.addAll(configuration.ignorePatterns)

    addButton.addActionListener {
      val input = JOptionPane.showInputDialog("Enter a new pattern")
      if (!input.isNullOrBlank() && !listModel.contains(input)) {
        try {
          Regex(input)
        } catch (e: PatternSyntaxException) {
          NotificationGroupManager.getInstance()
            .getNotificationGroup("Invalid regex")
            .createNotification(
              "Invalid pattern",
              "${e.pattern} is not a valid regex pattern: ${e.message}",
              NotificationType.ERROR
            ).notify(null)
          return@addActionListener
        }
        listModel.addElement(input)
      }
    }

    removeButton.addActionListener {
      ignorePatterns.selectedValue?.let { listModel.removeElement(it) }
    }
  }

  fun isModified(): Boolean {
    return configuration.ignoreWhitespaces != whitespaceToggle.isSelected ||
        configuration.ignorePatterns.size != listModel.size ||
        configuration.ignorePatterns.any { !listModel.contains(it) }
  }

  fun apply() {
    configuration.ignoreWhitespaces = whitespaceToggle.isSelected
    configuration.ignorePatterns.clear()
    configuration.ignorePatterns.addAll(listModel.elements().toList())
  }

  fun reset() {
    init()
  }
}