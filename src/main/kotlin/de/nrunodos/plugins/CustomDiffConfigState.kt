package de.nrunodos.plugins

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

@State(name = "CustomDiffConfigState", storages = [Storage("CustomDiffConfigState.xml")])
class CustomDiffConfigState : PersistentStateComponent<CustomDiffConfigState> {
  var ignoreWhitespaces = true
  val ignorePatterns = mutableListOf<String>()

  override fun getState(): CustomDiffConfigState {
    return this
  }

  override fun loadState(state: CustomDiffConfigState) {
    this.ignorePatterns.clear()
    this.ignorePatterns.addAll(state.ignorePatterns)
  }

  companion object {
    fun getInstance(): CustomDiffConfigState {
      return ApplicationManager.getApplication().getService(CustomDiffConfigState::class.java)
    }
  }
}