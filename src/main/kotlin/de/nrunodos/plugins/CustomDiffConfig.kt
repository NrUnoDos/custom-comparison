package de.nrunodos.plugins

import com.intellij.openapi.options.SearchableConfigurable
import org.jetbrains.annotations.Nls
import javax.swing.JComponent

class CustomDiffConfig : SearchableConfigurable {
    private var configGui: CustomDiffConfigGui? = null

    override fun getId(): String = "de.nrunodos.plugins.CustomDiffConfig"

    @Nls(capitalization = Nls.Capitalization.Title)
    override fun getDisplayName(): String = "Custom Diff"

    override fun createComponent(): JComponent? {
        configGui = CustomDiffConfigGui()
        return configGui?.rootPanel
    }

    override fun disposeUIResources() {
        configGui = null
    }

    override fun isModified(): Boolean = configGui?.isModified() ?: false

    override fun apply() {
        configGui?.apply()
    }

    override fun reset() {
        configGui?.reset()
    }
}
