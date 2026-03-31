package de.nrunodos.plugins

import com.intellij.psi.PsiElement
import java.util.*

object PsiPathUtils {
    fun retrievePsiPath(element: PsiElement): String {
        val stack = Stack<String>()
        var current: PsiElement? = element
        while (current != null && current.node != null) {
            stack.push("${current.node.elementType}")
            current = current.parent
        }
        return stack.reversed().joinToString(": ")
    }
}
