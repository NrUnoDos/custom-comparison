package de.nrunodos.plugins

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

@State(name = "CustomDiffConfigState", storages = [Storage("CustomDiffConfigState.xml")])
class CustomDiffConfigState : PersistentStateComponent<CustomDiffConfigState> {
    var ignoreWhitespaces = true
    val ignorePatterns = mutableSetOf<String>()
    val ignoredPsiPaths = mutableSetOf<String>()

    override fun getState(): CustomDiffConfigState = this

    override fun loadState(state: CustomDiffConfigState) {
        this.ignoreWhitespaces = state.ignoreWhitespaces
        this.ignorePatterns.clear()
        this.ignorePatterns.addAll(state.ignorePatterns)
        this.ignoredPsiPaths.clear()
        this.ignoredPsiPaths.addAll(state.ignoredPsiPaths)
    }

    companion object {
        fun getInstance(): CustomDiffConfigState =
            ApplicationManager.getApplication().getService(CustomDiffConfigState::class.java)
    }
}
